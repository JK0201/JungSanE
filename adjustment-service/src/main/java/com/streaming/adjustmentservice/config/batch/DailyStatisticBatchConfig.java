package com.streaming.adjustmentservice.config.batch;

import com.streaming.adjustmentservice.dto.PlaybackSummary;
import com.streaming.adjustmentservice.entity.statistic.DailyStatistic;
import com.streaming.adjustmentservice.entity.statistic.VideoSnapshot;
import com.streaming.adjustmentservice.repository.VideoSnapshotJpaRepository;
import com.streaming.adjustmentservice.service.port.DailyStatisticRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DailyStatisticBatchConfig {

    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final DailyStatisticRepository dailyStatisticRepository;
    private final VideoSnapshotJpaRepository videoSnapshotJpaRepository;

    private final LocalDateTime batchStartTime = LocalDateTime.now();
//    // 배치 시작시간 -2시간 + 00분 00초
//    private final LocalDateTime START_TIME = batchStartTime
//        .minusHours(2)
//        .withMinute(0)
//        .withSecond(0);
//
//    // 배치 시작시간 -1초
//    private final LocalDateTime END_TIME = batchStartTime
//        .withMinute(0)
//        .withSecond(0)
//        .minusSeconds(1);

    // FIXME 더미 데이터 테스트용
    private final LocalDateTime START_TIME =
            LocalDateTime.now().minusDays(7)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0);

    // 현재 시간
    private final LocalDateTime END_TIME = LocalDateTime.now();

    @Bean
    public Job dailyStatisticJob(
            @Qualifier("metaTransactionManager") PlatformTransactionManager transactionManager
    ) {
        return new JobBuilder("dailyStatisticJob", jobRepository)
                .start(dailyStatisticStep(transactionManager))
                .next(videoSnapshotStep(transactionManager))
                .build();
    }

    @Bean
    public Step dailyStatisticStep(PlatformTransactionManager transactionManager) {
        return new StepBuilder("dailyStatisticStep", jobRepository)
                .<PlaybackSummary, DailyStatistic>chunk(100, transactionManager)
                .reader(dailyStatisticReader())
                .processor(dailyStatisticProcessor())
                .writer(dailyStatisticWriter())
                .build();
    }

    // PlaybackSummary로 GROUP BY로 연산한 Playback 가져오기
    @Bean
    public JpaPagingItemReader<PlaybackSummary> dailyStatisticReader() {
        return new JpaPagingItemReaderBuilder<PlaybackSummary>()
                .name("dailyStatisticReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
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
                        group by p.videoId, p.uploaderId
                        """)
                .parameterValues(Map.of(
                        "START_TIME", START_TIME,
                        "END_TIME", END_TIME
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
    public Step videoSnapshotStep(PlatformTransactionManager transactionManager) {
        return new StepBuilder("videoSnapshotStep", jobRepository)
                .<DailyStatistic, VideoSnapshot>chunk(100, transactionManager)
                .reader(videoSnapshotReader())
                .processor(videoSnapshotProcessor())
                .writer(videoSnapshotWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<DailyStatistic> videoSnapshotReader() {
        return new JpaPagingItemReaderBuilder<DailyStatistic>()
                .name("videoSnapshotReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
                .queryString("""
                        select d from DailyStatistic d 
                        where d.statisticDate = :START_TIME 
                        """)
                .parameterValues(Collections.singletonMap("START_TIME", START_TIME))
                .build();
    }

    @Bean
    ItemProcessor<DailyStatistic, VideoSnapshot> videoSnapshotProcessor() {
        return VideoSnapshot::fromStatistic;
    }

    @Bean
    public ItemWriter<VideoSnapshot> videoSnapshotWriter() {
        return videoSnapshotJpaRepository::saveAll;
    }
}
