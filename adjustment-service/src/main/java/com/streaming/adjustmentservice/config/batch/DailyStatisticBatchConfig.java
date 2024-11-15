package com.streaming.adjustmentservice.config.batch;

import com.streaming.adjustmentservice.dto.PlaybackSummary;
import com.streaming.adjustmentservice.entity.statistic.DailyStatistic;
import com.streaming.adjustmentservice.service.port.DailyStatisticRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import static com.streaming.common.constant.DatasourceConstant.READ_DATASOURCE;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DailyStatisticBatchConfig {

    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final DailyStatisticRepository dailyStatisticRepository;

    @Value("${spring.batch.statistics.thread-count}")
    private int threadCount;

    @Value("${spring.batch.statistics.chunk-size}")
    private int chunkSize;

    private final LocalDateTime batchStartTime = LocalDateTime.now();
    // 배치 시작 시간 -> (시작 시 - 2시간) : 00분 : 00초
    private final LocalDateTime START_TIME = batchStartTime
            .minusHours(3)
            .withMinute(0)
            .withSecond(0);
    // 배치 종료 시간 -> (시작 시) : 00분 : 00초
    private final LocalDateTime END_TIME = batchStartTime
            .withMinute(0)
            .withSecond(0);

    @Bean
    public Job dailyStatisticJob(
            @Qualifier("metaTransactionManager") PlatformTransactionManager transactionManager,
            @Qualifier(READ_DATASOURCE) DataSource readDataSource
    ) {
        return new JobBuilder("dailyStatisticJob", jobRepository)
                .start(dailyStatisticStep(transactionManager, readDataSource))
                .build();
    }

    @Bean
    public Step dailyStatisticStep(
            PlatformTransactionManager transactionManager,
            DataSource readDataSource
    ) {
        return new StepBuilder("dailyStatisticStep", jobRepository)
                .partitioner("statisticPartition", statisticPartitioner(readDataSource))
                .step(statisticSlaveStep(transactionManager))
                .gridSize(threadCount * 4)
                .taskExecutor(statisticTaskExecutor())
                .build();
    }

    @Bean
    public Step statisticSlaveStep(PlatformTransactionManager transactionManager) {
        return new StepBuilder("statisticSlaveStep", jobRepository)
                .<PlaybackSummary, DailyStatistic>chunk(chunkSize, transactionManager)
                .reader(dailyStatisticReader(null, null))
                .processor(dailyStatisticProcessor())
                .writer(dailyStatisticWriter())
                .build();
    }

    // PlaybackSummary로 GROUP BY로 연산한 Playback 가져오기
    @Bean
    @StepScope
    public JpaPagingItemReader<PlaybackSummary> dailyStatisticReader(
            @Value("#{stepExecutionContext[minVideoId]}") Long minVideoId,
            @Value("#{stepExecutionContext[maxVideoId]}") Long maxVideoId
    ) {
        return new JpaPagingItemReaderBuilder<PlaybackSummary>()
                .name("dailyStatisticReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("""
                        select new com.streaming.adjustmentservice.dto.PlaybackSummary(
                            sum(p.videoPlayedTime), 
                            sum(case when p.isNewView = true then 1 else 0 end), 
                            sum(p.advertisementViewCount), 
                            p.videoId, 
                            p.uploaderId
                        ) 
                        from PlaybackLog p 
                        where p.createdAt >= :START_TIME 
                        and p.createdAt < :END_TIME 
                        and p.videoId >= :minVideoId
                        and p.videoId < :maxVideoId
                        group by p.videoId, p.uploaderId
                        order by p.videoId
                        """)
                .parameterValues(Map.of(
                        "START_TIME", START_TIME,
                        "END_TIME", END_TIME,
                        "minVideoId", minVideoId,
                        "maxVideoId", maxVideoId
                ))
                .build();
    }

    // DailyStatistic 객체로 맵핑
    @Bean
    public ItemProcessor<PlaybackSummary, DailyStatistic> dailyStatisticProcessor() {
        return summary -> DailyStatistic.fromSummary(summary, START_TIME);
    }

    // DailyStatistic 저장
    @Bean
    public ItemWriter<DailyStatistic> dailyStatisticWriter() {
        return dailyStatisticRepository::saveAll;
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
        executor.setCorePoolSize(threadCount); // 기본적으로 유지할 쓰레드 수
        executor.setMaxPoolSize(threadCount); // 최대로 생성할 수 있는 쓰레드 수
        executor.setThreadNamePrefix("statistic-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 큐가 가득 찼을 경우 처리 방식

        return executor;
    }

//    @Bean
//    public Step videoSnapshotStep(
//            PlatformTransactionManager transactionManager,
//            DataSource readDataSource
//    ) {
//        return new StepBuilder("videoSnapshotStep", jobRepository)
//                .partitioner("videoSnapshotPartition", videoBasedPartitioner(readDataSource))
//                .step(videoSnapshotSlaveStep(transactionManager))
//                .gridSize(threadCount)
//                .taskExecutor(statisticTaskExecutor())
//                .build();
//    }
//
//    @Bean
//    public Step videoSnapshotSlaveStep(PlatformTransactionManager transactionManager) {
//        return new StepBuilder("videoSnapshotSlaveStep", jobRepository)
//                .<DailyStatistic, VideoSnapshot>chunk(chunkSize, transactionManager)
//                .reader(videoSnapshotReader(null, null))
//                .processor(videoSnapshotProcessor())
//                .writer(videoSnapshotWriter())
//                .build();
//    }
//
//    @Bean
//    public JpaPagingItemReader<DailyStatistic> videoSnapshotReader(
//            @Value("#{stepExecutionContext[minVideoId]}") Long minVideoId,
//            @Value("#{stepExecutionContext[maxVideoId]}") Long maxVideoId
//    ) {
//        return new JpaPagingItemReaderBuilder<DailyStatistic>()
//                .name("videoSnapshotReader")
//                .entityManagerFactory(entityManagerFactory)
//                .pageSize(chunkSize)
//                .queryString("""
//                        select ds from DailyStatistic ds
//                        where ds.statisticDate = :TARGET_DATE
//                        """)
//                .parameterValues(Collections.singletonMap("TARGET_DATE", LocalDate.now()))
//                .build();
//    }
//
//    @Bean
//    ItemProcessor<DailyStatistic, VideoSnapshot> videoSnapshotProcessor() {
//        return VideoSnapshot::fromStatistic;
//    }
//
//    @Bean
//    public ItemWriter<VideoSnapshot> videoSnapshotWriter() {
//        return videoSnapshotJpaRepository::saveAll;
//    }
}
