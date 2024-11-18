package com.streaming.adjustmentservice.config.batch;

import com.streaming.adjustmentservice.entity.statistic.DailyStatistic;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Random;

import static com.streaming.common.constant.DatasourceConstant.WRITE_DATASOURCE;

@Configuration
@RequiredArgsConstructor
public class StatisticDummy {

    private static final long TOTAL_COUNT = 100_000_000;
    private final JobRepository jobRepository;

    @Bean
    public Job statisticDummyJob(Step statisticDummyStep) {
        return new JobBuilder("statisticDummyJob", jobRepository)
                .start(statisticDummyStep)
                .build();
    }

    @Bean
    public Step statisticDummyStep(
            @Qualifier("metaTransactionManager") PlatformTransactionManager transactionManager,
            @Qualifier(WRITE_DATASOURCE) DataSource writeDataSource
    ) {
        return new StepBuilder("statisticDummyStep", jobRepository)
                .<DailyStatistic, DailyStatistic>chunk(50000, transactionManager)
                .reader(statisticDummyReader())
                .writer(statisticDummyWriter(writeDataSource))
                .build();
    }

    @Bean
    public ItemReader<DailyStatistic> statisticDummyReader() {
        return new ItemReader<>() {
            private long currentIndex = 0;
            private final LocalDate yesterday = LocalDate.now().minusDays(1);
            private final Random random = new Random();

            @Override
            public DailyStatistic read() {
                if (currentIndex > TOTAL_COUNT) {
                    return null;
                }

                currentIndex++;

                return DailyStatistic.test(
                        currentIndex,
                        currentIndex,
                        random.nextLong(50, 86400),
                        random.nextLong(50, 1200000),
                        random.nextLong(50, 1200000),
                        yesterday
                );
            }
        };
    }

    @Bean
    public JdbcBatchItemWriter<DailyStatistic> statisticDummyWriter(
            @Qualifier(WRITE_DATASOURCE) DataSource writeDataSource
    ) {
        JdbcBatchItemWriter<DailyStatistic> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(writeDataSource);
        writer.setSql("""
                INSERT INTO daily_statistic
                (video_id, uploader_id, video_played_time, video_view_count, advertisement_view_count, statistic_date)
                VALUES (?, ?, ?, ?, ?, ?)
                """);

        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getVideoId());
            ps.setLong(2, item.getUploaderId());
            ps.setLong(3, item.getVideoPlayedTime());
            ps.setLong(4, item.getVideoViewCount());
            ps.setLong(5, item.getAdvertisementViewCount());
            ps.setDate(6, java.sql.Date.valueOf(item.getStatisticDate()));
        });

        return writer;
    }
}