package com.streaming.adjustmentservice.config.batch.settlement;

import com.streaming.adjustmentservice.dto.SettlementWrapper;
import com.streaming.adjustmentservice.dto.StatisticWrapper;
import com.streaming.adjustmentservice.entity.settlement.DailySettlement;
import com.streaming.adjustmentservice.entity.settlement.VideoSnapshot;
import com.streaming.adjustmentservice.entity.statistic.DailyStatistic;
import com.streaming.adjustmentservice.service.RevenueCalculatorService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
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
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import static com.streaming.common.constant.DatasourceConstant.READ_DATASOURCE;

@Configuration
@RequiredArgsConstructor
public class DailySettlementBatchConfig {

    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final RevenueCalculatorService revenueCalculatorService;

    @Value("${spring.batch.settlements.thread-count}")
    private int THREAD_COUNT;

    @Value("${spring.batch.settlements.partition-size}")
    private int PARTITION_SIZE;

    @Value("${spring.batch.settlements.chunk-size}")
    private int CHUNK_SIZE;

    // -1일 00:00:00
    private final LocalDate TARGET_DATE = LocalDate.now().minusDays(1);

    @Bean
    public Job dailySettlementJob(
            @Qualifier("metaTransactionManager") PlatformTransactionManager transactionManager,
            @Qualifier(READ_DATASOURCE) DataSource readDataSource
    ) {
        return new JobBuilder("dailySettlementJob", jobRepository)
                .start(dailySettlementStep(transactionManager, readDataSource))
                .build();
    }

    @Bean
    public Step dailySettlementStep(
            PlatformTransactionManager transactionManager,
            DataSource readDataSource
    ) {
        return new StepBuilder("dailySettlementStep", jobRepository)
                .partitioner("settlementPartitioner", settlementPartitioner(readDataSource))
                .step(settlementSlaveStep(transactionManager))
                .gridSize(PARTITION_SIZE)
                .taskExecutor(settlementTaskExecutor())
                .build();
    }

    @Bean
    public Step settlementSlaveStep(
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("settlementSlaveStep", jobRepository)
                .<StatisticWrapper, SettlementWrapper>chunk(CHUNK_SIZE, transactionManager)
                .reader(dailySettlementReader(null, null))
                .processor(dailySettlementProcessor())
                .writer(dailySettlementWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<StatisticWrapper> dailySettlementReader(
            @Value("#{stepExecutionContext[minStatisticId]}") Long minStatisticId,
            @Value("#{stepExecutionContext[maxStatisticId]}") Long maxStatisticId
    ) {
        return new JpaPagingItemReaderBuilder<StatisticWrapper>()
                .name("dailySettlementReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("""
                        select new com.streaming.adjustmentservice.dto.StatisticWrapper(ds, vs)
                        from DailyStatistic ds
                        left join VideoSnapshot vs on vs.videoId = ds.videoId
                        where ds.statisticDate = :TARGET_DATE
                        and ds.id >= :minStatisticId
                        and ds.id < :maxStatisticId
                        """)
                .parameterValues(Map.of(
                        "TARGET_DATE", TARGET_DATE,
                        "minStatisticId", minStatisticId,
                        "maxStatisticId", maxStatisticId
                ))
                .build();
    }

    @Bean
    public ItemProcessor<StatisticWrapper, SettlementWrapper> dailySettlementProcessor() {
        return statisticWrapper -> {
            DailyStatistic dailyStatistic = statisticWrapper.getDailyStatistic();
            VideoSnapshot videoSnapshot = statisticWrapper.getVideoSnapshot();

            if (videoSnapshot == null) {
                videoSnapshot = VideoSnapshot.fromStatistic(dailyStatistic);
            }

            videoSnapshot.updateSnapshot(dailyStatistic);

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
            DailySettlement dailySettlement = DailySettlement.fromRevenue(
                    videoRevenue,
                    advertisementRevenue,
                    settlementDate,
                    videoId,
                    uploaderId
            );

            return SettlementWrapper.from(dailySettlement, videoSnapshot);
        };
    }

    @Bean
    public ItemWriter<SettlementWrapper> dailySettlementWriter() {
        return settlementResult -> {
            EntityManager em = entityManagerFactory.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            try {
                tx.begin();
                for (SettlementWrapper wrapper : settlementResult) {
                    em.merge(wrapper.getVideoSnapshot());
                    em.merge(wrapper.getDailySettlement());
                }
                tx.commit();
            } catch (Exception ex) {
                tx.rollback();
            } finally {
                em.close();
            }
        };
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
