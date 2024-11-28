package com.streaming.adjustmentservice.config.batch.settlement;

import com.streaming.adjustmentservice.dto.batch.SettlementWrapper;
import com.streaming.adjustmentservice.dto.batch.StatisticWrapper;
import com.streaming.adjustmentservice.entity.settlement.DailySettlement;
import com.streaming.adjustmentservice.entity.settlement.VideoSnapshot;
import com.streaming.adjustmentservice.entity.statistic.DailyStatistic;
import com.streaming.adjustmentservice.service.RevenueCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static com.streaming.common.constant.DatasourceConstant.READ_DATASOURCE;
import static com.streaming.common.constant.DatasourceConstant.WRITE_DATASOURCE;

@Configuration
@RequiredArgsConstructor
public class DailySettlementBatchConfig {

    private final JobRepository jobRepository;
    private final RevenueCalculatorService revenueCalculatorService;

    @Value("${spring.batch.settlements.thread-count}")
    private int THREAD_COUNT;

    @Value("${spring.batch.settlements.partition-size}")
    private int PARTITION_SIZE;

    @Value("${spring.batch.settlements.chunk-size}")
    private int CHUNK_SIZE;

    // 정산 작업 시간 : (시작일 - 1일) 00:00:00
//    private final LocalDate TARGET_DATE = LocalDate.now().minusDays(1);
    private final LocalDate TARGET_DATE = LocalDate.of(2024, 10, 30);

    @Bean
    public Job dailySettlementJob(
            @Qualifier("metaTransactionManager") PlatformTransactionManager transactionManager,
            @Qualifier(READ_DATASOURCE) DataSource readDataSource,
            @Qualifier(WRITE_DATASOURCE) DataSource writeDataSource
    ) {
        return new JobBuilder("dailySettlementJob", jobRepository)
                .start(dailySettlementStep(transactionManager, readDataSource, writeDataSource))
                .build();
    }

    @Bean
    public Step dailySettlementStep(
            PlatformTransactionManager transactionManager,
            DataSource readDataSource,
            DataSource writeDataSource
    ) {
        return new StepBuilder("dailySettlementStep", jobRepository)
                .partitioner("settlementPartitioner", settlementPartitioner(readDataSource))
                .step(settlementSlaveStep(transactionManager, readDataSource, writeDataSource))
                .gridSize(PARTITION_SIZE)
                .taskExecutor(settlementTaskExecutor())
                .build();
    }

    @Bean
    public Step settlementSlaveStep(
            PlatformTransactionManager transactionManager,
            DataSource readDataSource,
            DataSource writeDataSource
    ) {
        return new StepBuilder("settlementSlaveStep", jobRepository)
                .<StatisticWrapper, SettlementWrapper>chunk(CHUNK_SIZE, transactionManager)
                .reader(dailySettlementReader(readDataSource, null, null))
                .processor(dailySettlementProcessor())
                .writer(dailySettlementWriter(writeDataSource))
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<StatisticWrapper> dailySettlementReader(
            @Qualifier(READ_DATASOURCE) DataSource readDataSource,
            @Value("#{stepExecutionContext[minStatisticId]}") Long minStatisticId,
            @Value("#{stepExecutionContext[maxStatisticId]}") Long maxStatisticId
    ) {
        return new JdbcCursorItemReaderBuilder<StatisticWrapper>()
                .name("dailySettlementReader")
                .fetchSize(CHUNK_SIZE)
                .dataSource(readDataSource)
                .sql("""
                        SELECT
                            ds.*, 
                            vs.video_id as vs_video_id, 
                            vs.video_view_count as vs_video_view_count, 
                            vs.advertisement_view_count as vs_advertisement_view_count, 
                            vs.snapshot_date as vs_snapshot_date
                        FROM daily_statistic ds 
                        LEFT JOIN video_snapshot vs ON vs.video_id = ds.video_id 
                        WHERE ds.statistic_date = ? 
                        AND ds.daily_statistic_id >= ? 
                        AND ds.daily_statistic_id < ? 
                        ORDER BY ds.daily_statistic_id
                        """)
                .preparedStatementSetter(ps -> {
                    ps.setDate(1, java.sql.Date.valueOf(TARGET_DATE));
                    ps.setLong(2, minStatisticId);
                    ps.setLong(3, maxStatisticId);
                })
                .rowMapper((rs, rowNum) -> {
                    DailyStatistic dailyStatistic = DailyStatistic.of(
                            rs.getLong("video_id"),
                            rs.getLong("uploader_id"),
                            rs.getLong("video_played_time"),
                            rs.getLong("video_view_count"),
                            rs.getLong("advertisement_view_count"),
                            rs.getDate("statistic_date").toLocalDate()
                    );

                    Long snapshotVideoId = rs.getObject("vs_video_id", Long.class);
                    VideoSnapshot videoSnapshot =
                            snapshotVideoId != null ? VideoSnapshot.of(
                                    rs.getLong("vs_video_id"),
                                    rs.getLong("vs_video_view_count"),
                                    rs.getLong("vs_advertisement_view_count"),
                                    rs.getDate("vs_snapshot_date").toLocalDate()
                            ) : null;

                    return StatisticWrapper.of(dailyStatistic, videoSnapshot);
                })
                .build();
    }

    @Bean
    public ItemProcessor<StatisticWrapper, SettlementWrapper> dailySettlementProcessor() {
        return statisticWrapper -> {
            DailyStatistic dailyStatistic = statisticWrapper.getDailyStatistic();
            VideoSnapshot videoSnapshot = statisticWrapper.getVideoSnapshot();

            if (videoSnapshot == null) videoSnapshot = VideoSnapshot.from(dailyStatistic);
            else videoSnapshot.updateSnapshot(dailyStatistic);

            long totalVideoViews = videoSnapshot.getVideoViewCount();
            long currentVideoViews = dailyStatistic.getVideoViewCount();
            long totalAdvertisementViews = videoSnapshot.getAdvertisementViewCount();
            long currentAdvertisementViews = dailyStatistic.getAdvertisementViewCount();

            BigDecimal videoRevenue = revenueCalculatorService.calculateVideoRevenue(
                    totalVideoViews,
                    currentVideoViews
            );
            BigDecimal advertisementRevenue = revenueCalculatorService.calculateAdvertisementRevenue(
                    totalAdvertisementViews,
                    currentAdvertisementViews
            );

            Long videoId = dailyStatistic.getVideoId();
            Long uploaderId = dailyStatistic.getUploaderId();
            LocalDate settlementDate = dailyStatistic.getStatisticDate();

            DailySettlement dailySettlement = DailySettlement.of(
                    videoRevenue,
                    advertisementRevenue,
                    settlementDate,
                    videoId,
                    uploaderId
            );

            return SettlementWrapper.of(dailySettlement, videoSnapshot);
        };
    }

    @Bean
    public CompositeItemWriter<SettlementWrapper> dailySettlementWriter(
            @Qualifier(WRITE_DATASOURCE) DataSource writeDataSource
    ) {
        return new CompositeItemWriterBuilder<SettlementWrapper>()
                .delegates(List.of(
                        videoSnapshot(writeDataSource),
                        dailySettlement(writeDataSource)
                ))
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<SettlementWrapper> videoSnapshot(DataSource writeDataSource) {
        return new JdbcBatchItemWriterBuilder<SettlementWrapper>()
                .dataSource(writeDataSource)
                .sql("""
                        INSERT INTO video_snapshot (
                            video_id, video_view_count,
                            advertisement_view_count, snapshot_date
                        ) VALUES (?, ?, ?, ?)
                        ON CONFLICT (video_id)
                        DO UPDATE SET
                            video_view_count = ?,
                            advertisement_view_count = ?,
                            snapshot_date = ?
                        """)
                .itemPreparedStatementSetter((item, ps) -> {
                    VideoSnapshot videoSnapshot = item.getVideoSnapshot();
                    ps.setLong(1, videoSnapshot.getVideoId());
                    ps.setLong(2, videoSnapshot.getVideoViewCount());
                    ps.setLong(3, videoSnapshot.getAdvertisementViewCount());
                    ps.setDate(4, java.sql.Date.valueOf(videoSnapshot.getSnapshotDate()));
                    ps.setLong(5, videoSnapshot.getVideoViewCount());
                    ps.setLong(6, videoSnapshot.getAdvertisementViewCount());
                    ps.setDate(7, java.sql.Date.valueOf(videoSnapshot.getSnapshotDate()));
                })
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<SettlementWrapper> dailySettlement(DataSource writeDataSource) {
        return new JdbcBatchItemWriterBuilder<SettlementWrapper>()
                .dataSource(writeDataSource)
                .sql("""
                        INSERT INTO daily_settlement (
                            video_id, uploader_id, settlement_date,
                            video_revenue, advertisement_revenue, total_revenue
                        ) VALUES (?, ?, ?, ?, ?, ?)
                        """)
                .itemPreparedStatementSetter((item, ps) -> {
                    DailySettlement dailySettlement = item.getDailySettlement();
                    ps.setLong(1, dailySettlement.getVideoId());
                    ps.setLong(2, dailySettlement.getUploaderId());
                    ps.setDate(3, java.sql.Date.valueOf(dailySettlement.getSettlementDate()));
                    ps.setBigDecimal(4, dailySettlement.getVideoRevenue());
                    ps.setBigDecimal(5, dailySettlement.getAdvertisementRevenue());
                    ps.setBigDecimal(6, dailySettlement.getTotalRevenue());
                })
                .build();
    }

    @Bean
    public SettlementPartitioner settlementPartitioner(DataSource readDataSource) {
        return new SettlementPartitioner(
                new JdbcTemplate(readDataSource),
                TARGET_DATE
        );
    }

    @Bean
    public TaskExecutor settlementTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(THREAD_COUNT); // 기본적으로 유지할 쓰레드 수
        executor.setMaxPoolSize(THREAD_COUNT); // 최대로 생성할 수 있는 쓰레드 수
        executor.setThreadNamePrefix("settlement-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 큐가 가득 찼을 경우 처리 방식

        return executor;
    }
}
