package com.streaming.adjustmentservice.config.batch.statistic;

import com.streaming.adjustmentservice.entity.statistic.DailyStatistic;
import com.streaming.adjustmentservice.entity.statistic.PeriodType;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
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
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static com.streaming.common.constant.DatasourceConstant.READ_DATASOURCE;
import static com.streaming.common.constant.DatasourceConstant.WRITE_DATASOURCE;

@Configuration
@RequiredArgsConstructor
public class DailyStatisticBatchConfig {

    private final JobRepository jobRepository;

    @Value("${spring.batch.statistics.thread-count}")
    private int THREAD_COUNT;

    @Value("${spring.batch.statistics.partition-size}")
    private int PARTITION_SIZE;

    @Value("${spring.batch.statistics.chunk-size}")
    private int CHUNK_SIZE;

//    private final LocalDateTime batchStartTime = LocalDateTime.now();
//    // 시작 시간 -> (시작 시간 - 2시간) : 00분 : 00초
//    private final LocalDateTime START_TIME = batchStartTime
//            .minusHours(1)
//            .withMinute(0)
//            .withSecond(0);
//
//    // 종료 시간 -> (시작 시간) : 00분 : 00초
//    private final LocalDateTime END_TIME = batchStartTime
//            .withMinute(0)
//            .withSecond(0);

    private final LocalDateTime START_TIME = LocalDateTime.of(2024, 10, 27, 0, 0, 0);
    private final LocalDateTime END_TIME = LocalDateTime.of(2024, 11, 26, 0, 0, 0);

    @Bean
    public Job dailyStatisticJob(
            @Qualifier("metaTransactionManager") PlatformTransactionManager transactionManager,
            @Qualifier(READ_DATASOURCE) DataSource readDataSource,
            @Qualifier(WRITE_DATASOURCE) DataSource writeDataSource
    ) {
        return new JobBuilder("dailyStatisticJob", jobRepository)
                .start(dailyStatisticStep(transactionManager, readDataSource, writeDataSource))
                .build();
    }

    @Bean
    public Step dailyStatisticStep(
            PlatformTransactionManager transactionManager,
            DataSource readDataSource,
            DataSource writeDataSource
    ) {
        return new StepBuilder("dailyStatisticStep", jobRepository)
                .partitioner("statisticPartitioner", statisticPartitioner(readDataSource))
                .step(statisticSlaveStep(transactionManager, readDataSource, writeDataSource))
                .gridSize(PARTITION_SIZE)
                .taskExecutor(statisticTaskExecutor())
                .build();
    }

    @Bean
    public Step statisticSlaveStep(
            PlatformTransactionManager transactionManager,
            DataSource readDataSource,
            DataSource writeDataSource
    ) {
        return new StepBuilder("statisticSlaveStep", jobRepository)
                .<DailyStatistic, DailyStatistic>chunk(CHUNK_SIZE, transactionManager)
                .reader(dailyStatisticReader(readDataSource, null, null))
                .writer(dailyStatisticWriter(writeDataSource))
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<DailyStatistic> dailyStatisticReader(
            @Qualifier(READ_DATASOURCE) DataSource readDataSource,
            @Value("#{stepExecutionContext[minVideoId]}") Long minVideoId,
            @Value("#{stepExecutionContext[maxVideoId]}") Long maxVideoId
    ) {
        return new JdbcCursorItemReaderBuilder<DailyStatistic>()
                .name("dailyStatisticReader")
                .fetchSize(CHUNK_SIZE)
                .dataSource(readDataSource)
                .sql("""
                        SELECT 
                            SUM(video_played_time) as video_played_time, 
                            SUM(CASE WHEN is_new_view = true THEN 1 ELSE 0 END) as video_view_count, 
                            SUM(advertisement_view_count) as advertisement_view_count, 
                            video_id, 
                            uploader_id 
                        FROM playback_log 
                        WHERE created_at >= ? 
                        AND created_at < ? 
                        AND video_id >= ? 
                        AND video_id < ? 
                        GROUP BY video_id, uploader_id 
                        ORDER BY video_id
                        """)
                .preparedStatementSetter(ps -> {
                    ps.setTimestamp(1, Timestamp.valueOf(START_TIME));
                    ps.setTimestamp(2, Timestamp.valueOf(END_TIME));
                    ps.setLong(3, minVideoId);
                    ps.setLong(4, maxVideoId);
                })
                .rowMapper((rs, rowNum) -> DailyStatistic.of(
                        rs.getLong("video_id"),
                        rs.getLong("uploader_id"),
                        rs.getLong("video_played_time"),
                        rs.getLong("video_view_count"),
                        rs.getLong("advertisement_view_count"),
                        START_TIME.toLocalDate()
                ))
                .build();
    }

    @Bean
    public CompositeItemWriter<DailyStatistic> dailyStatisticWriter(
            @Qualifier(WRITE_DATASOURCE) DataSource writeDataSource
    ) {
        return new CompositeItemWriterBuilder<DailyStatistic>()
                .delegates(List.of(
                        videoSummary(writeDataSource),
                        dailyStatistic(writeDataSource)
                ))
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<DailyStatistic> videoSummary(DataSource writeDataSource) {
        return new JdbcBatchItemWriterBuilder<DailyStatistic>()
                .dataSource(writeDataSource)
                .sql("""
                        INSERT INTO video_summary (
                            video_id, uploader_id, period_type, 
                            video_view_count, video_played_time, 
                            start_date, end_date, updated_at
                        ) 
                        VALUES
                            (?, ?, 'DAILY', ?, ?, ?, ?, ?), 
                            (?, ?, 'WEEKLY', ?, ?, ?, ?, ?), 
                            (?, ?, 'MONTHLY', ?, ?, ?, ?, ?)
                        ON CONFLICT (video_id, period_type) 
                        DO UPDATE SET
                            video_view_count = CASE
                                WHEN EXCLUDED.start_date != video_summary.start_date THEN EXCLUDED.video_view_count 
                                ELSE video_summary.video_view_count + EXCLUDED.video_view_count 
                            END, 
                            video_played_time = CASE
                                WHEN EXCLUDED.start_date != video_summary.start_date THEN EXCLUDED.video_played_time 
                                ELSE video_summary.video_played_time + EXCLUDED.video_played_time 
                            END,
                            start_date = EXCLUDED.start_date, 
                            end_date = EXCLUDED.end_date, 
                            updated_at = EXCLUDED.updated_at
                        """)
                .itemPreparedStatementSetter((item, ps) -> {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDate currentDate = item.getStatisticDate();

                    PeriodType.PeriodTypeRange dailyRange = PeriodType.DAILY.calculateRange(currentDate);
                    ps.setLong(1, item.getVideoId());
                    ps.setLong(2, item.getUploaderId());
                    ps.setLong(3, item.getVideoViewCount());
                    ps.setLong(4, item.getVideoPlayedTime());
                    ps.setDate(5, Date.valueOf(dailyRange.start()));
                    ps.setDate(6, Date.valueOf(dailyRange.end()));
                    ps.setTimestamp(7, Timestamp.valueOf(now));

                    PeriodType.PeriodTypeRange weeklyRange = PeriodType.WEEKLY.calculateRange(currentDate);
                    ps.setLong(8, item.getVideoId());
                    ps.setLong(9, item.getUploaderId());
                    ps.setLong(10, item.getVideoViewCount());
                    ps.setLong(11, item.getVideoPlayedTime());
                    ps.setDate(12, Date.valueOf(weeklyRange.start()));
                    ps.setDate(13, Date.valueOf(weeklyRange.end()));
                    ps.setTimestamp(14, Timestamp.valueOf(now));

                    PeriodType.PeriodTypeRange monthlyRange = PeriodType.MONTHLY.calculateRange(currentDate);
                    ps.setLong(15, item.getVideoId());
                    ps.setLong(16, item.getUploaderId());
                    ps.setLong(17, item.getVideoViewCount());
                    ps.setLong(18, item.getVideoPlayedTime());
                    ps.setDate(19, Date.valueOf(monthlyRange.start()));
                    ps.setDate(20, Date.valueOf(monthlyRange.end()));
                    ps.setTimestamp(21, Timestamp.valueOf(now));
                })
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<DailyStatistic> dailyStatistic(DataSource writeDataSource) {
        return new JdbcBatchItemWriterBuilder<DailyStatistic>()
                .dataSource(writeDataSource)
                .sql("""
                        INSERT INTO daily_statistic (
                            video_id, uploader_id, video_played_time, 
                            video_view_count, advertisement_view_count, 
                            statistic_date
                        ) VALUES (?, ?, ?, ?, ?, ?) 
                        ON CONFLICT (video_id, statistic_date) 
                        DO UPDATE SET 
                            video_played_time = daily_statistic.video_played_time + EXCLUDED.video_played_time,
                            video_view_count = daily_statistic.video_view_count + EXCLUDED.video_view_count,
                            advertisement_view_count = daily_statistic.advertisement_view_count + EXCLUDED.advertisement_view_count,
                            uploader_id = EXCLUDED.uploader_id
                        """)
                .itemPreparedStatementSetter((item, ps) -> {
                    ps.setLong(1, item.getVideoId());
                    ps.setLong(2, item.getUploaderId());
                    ps.setLong(3, item.getVideoPlayedTime());
                    ps.setLong(4, item.getVideoViewCount());
                    ps.setLong(5, item.getAdvertisementViewCount());
                    ps.setDate(6, Date.valueOf(item.getStatisticDate()));
                })
                .build();
    }

    @Bean
    public StatisticPartitioner statisticPartitioner(DataSource readDataSource) {
        return new StatisticPartitioner(
                new JdbcTemplate(readDataSource),
                START_TIME,
                END_TIME);
    }

    @Bean
    public TaskExecutor statisticTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(THREAD_COUNT); // 기본적으로 유지할 쓰레드 수
        executor.setMaxPoolSize(THREAD_COUNT); // 최대로 생성할 수 있는 쓰레드 수
        executor.setThreadNamePrefix("statistic-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 큐가 가득 찼을 경우 처리 방식

        return executor;
    }
}
