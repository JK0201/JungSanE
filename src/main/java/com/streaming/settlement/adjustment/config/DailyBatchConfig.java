package com.streaming.settlement.adjustment.config;

import com.streaming.settlement.adjustment.dto.PlaybackSummary;
import com.streaming.settlement.adjustment.entity.DailyStatistic;
import com.streaming.settlement.adjustment.repository.DailyStatisticRepository;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DailyBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final DailyStatisticRepository dailyStatisticRepository;

    private final LocalDateTime START_DATE = LocalDateTime.now()
            .minusDays(7)
            .withHour(0).withMinute(0).withSecond(0);

    private final LocalDateTime END_DATE = LocalDateTime.now()
            .plusDays(7)
            .withHour(0).withMinute(0).withSecond(0);

    @Bean
    public Job dailyStatisticJob() {
        return new JobBuilder("dailyStatisticJob", jobRepository)
                .start(dailyStatisticStep())
                .build();
    }

    @Bean
    public Step dailyStatisticStep() {
        return new StepBuilder("dailyStatisticStep", jobRepository)
                .<PlaybackSummary, DailyStatistic>chunk(10, platformTransactionManager)
                .reader(dailyStatisticReader())
                .processor(dailyStatisticProcessor())
                .writer(dailyStatisticWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<PlaybackSummary> dailyStatisticReader() {
        return new JpaPagingItemReaderBuilder<PlaybackSummary>()
                .name("dailyStatisticReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(10)
                .queryString("""
                        select new com.streaming.settlement.adjustment.dto.PlaybackSummary(
                            sum(p.videoPlayedTime), 
                            count(p.id), 
                            sum(p.advertisementViewCount), 
                            v
                        ) 
                        from Playback p 
                        join p.video v 
                        where p.createdAt >= :START_DATE 
                        and p.createdAt < :END_DATE 
                        group by p.video.id
                        """)
                .parameterValues(Map.of(
                        "START_DATE", START_DATE,
                        "END_DATE", END_DATE
                ))
                .build();
    }

    @Bean
    public ItemProcessor<PlaybackSummary, DailyStatistic> dailyStatisticProcessor() {
        return DailyStatistic::fromSummary;
    }

    @Bean
    public ItemWriter<DailyStatistic> dailyStatisticWriter() {
        return dailyStatisticRepository::saveAll;
    }
}
