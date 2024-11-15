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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DailySettlementBatchConfig {

    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final RevenueCalculatorService revenueCalculatorService;

    // -1일 00:00:00
    private final LocalDate START_DATE = LocalDate.now().minusDays(1);

    // 00:00:00
    private final LocalDate END_DATE = LocalDate.now();

    @Bean
    public Job dailySettlementJob(
            @Qualifier("metaTransactionManager") PlatformTransactionManager transactionManager
    ) {
        return new JobBuilder("dailySettlementJob", jobRepository)
                .start(dailySettlementStep(transactionManager))
                .build();
    }

    @Bean
    public Step dailySettlementStep(
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("dailySettlementStep", jobRepository)
                .<StatisticWrapper, SettlementWrapper>chunk(5000, transactionManager)
                .reader(dailySettlementReader())
                .processor(dailySettlementProcessor())
                .writer(videoSnapshotItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<StatisticWrapper> dailySettlementReader() {
        return new JpaPagingItemReaderBuilder<StatisticWrapper>()
                .name("dailySettlementReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(5000)
                .queryString("""
                        select new com.streaming.adjustmentservice.dto.StatisticWrapper(ds, vs)
                        from DailyStatistic ds
                        left join VideoSnapshot vs on vs.videoId = ds.videoId
                        where ds.statisticDate >= :START_DATE
                        and ds.statisticDate < :END_DATE
                        """)
                .parameterValues(Map.of(
                        "START_DATE", START_DATE,
                        "END_DATE", END_DATE
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
    public ItemWriter<SettlementWrapper> videoSnapshotItemWriter() {
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
}
