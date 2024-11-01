package com.streaming.settlement.adjustment.config;

import com.streaming.settlement.adjustment.dto.PlaybackSummary;
import com.streaming.settlement.adjustment.dto.SettlementWrapper;
import com.streaming.settlement.adjustment.dto.StatisticWrapper;
import com.streaming.settlement.adjustment.entity.DailySettlement;
import com.streaming.settlement.adjustment.entity.DailyStatistic;
import com.streaming.settlement.adjustment.repository.DailyStatisticRepository;
import com.streaming.settlement.adjustment.util.RevenueCalculator;
import com.streaming.settlement.video.entity.Video;
import com.streaming.settlement.video.entity.VideoSnapshot;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DailyBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final RevenueCalculator revenueCalculator;
    private final EntityManagerFactory entityManagerFactory;
    private final DailyStatisticRepository dailyStatisticRepository;

    // -1일 00:00:00
    private final LocalDateTime START_DATE =
            LocalDateTime.now().minusDays(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0);
    // 00:00:00
    private final LocalDateTime END_DATE =
            LocalDateTime.now()
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0);

    // -1일
    private final LocalDate TARGET_DATE = LocalDate.now().minusDays(1);


    // FIXME JDBC로 추후 성능 개선 요망 (전체 코드에 대해서)
    @Bean
    public Job dailyStatisticJob() {
        return new JobBuilder("dailyStatisticJob", jobRepository)
                .start(dailyStatisticStep())
                .next(dailySettlementStep())
                .build();
    }

    // 통계 스텝
    @Bean
    public Step dailyStatisticStep() {
        return new StepBuilder("dailyStatisticStep", jobRepository)
                .<PlaybackSummary, DailyStatistic>chunk(10, platformTransactionManager)
                .reader(dailyStatisticReader())
                .processor(dailyStatisticProcessor())
                .writer(dailyStatisticWriter())
                .build();
    }

    // PlaybackSummary로 쿼리로 연산한 Playback 가져오기 
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
                            p.video
                        ) 
                        from Playback p 
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

    // DailyStatistic 객체로 맵핑
    @Bean
    public ItemProcessor<PlaybackSummary, DailyStatistic> dailyStatisticProcessor() {
        return DailyStatistic::fromSummary;
    }

    // DailyStatistic 저장
    @Bean
    public ItemWriter<DailyStatistic> dailyStatisticWriter() {
        return dailyStatisticRepository::saveAll;
    }

    // 정산 스텝
    @Bean
    public Step dailySettlementStep() {
        return new StepBuilder("dailySettlementStep", jobRepository)
                .<StatisticWrapper, SettlementWrapper>chunk(10, platformTransactionManager)
                .reader(dailySettlementReader())
                .processor(dailySettlementProcessor())
                .writer(dailySettlementWriter())
                .build();
    }

    // StatisticWrapper로 앞서 저장한 DailyStatisitc과, 조회수 누적합을 저장한 VideoSnapshot 가져오기
    @Bean
    public JpaPagingItemReader<StatisticWrapper> dailySettlementReader() {
        return new JpaPagingItemReaderBuilder<StatisticWrapper>()
                .name("dailySettlementReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(10)
                .queryString("""
                        select new com.streaming.settlement.adjustment.dto.StatisticWrapper(ds, vs, ds.video) 
                        from DailyStatistic ds 
                        join VideoSnapshot vs on vs.video.id = ds.video.id  
                        where ds.statisticDate = :TARGET_DATE
                        """)
                .parameterValues(Collections.singletonMap("TARGET_DATE", TARGET_DATE))
                .build();
    }

    // 누적합 업데이트 및 정산 로직 수행
    @Bean
    public ItemProcessor<StatisticWrapper, SettlementWrapper> dailySettlementProcessor() {
        return statisticWrapper -> {
            DailyStatistic dailyStatistic = statisticWrapper.getDailyStatistic();
            VideoSnapshot videoSnapshot = statisticWrapper.getVideoSnapshot();
            Video video = statisticWrapper.getVideo();

            videoSnapshot.updateViewCount(dailyStatistic);
            videoSnapshot.updateDate(TARGET_DATE);

            long totalVideoViews = videoSnapshot.getVideoViewCount();
            long currentVideoViews = dailyStatistic.getVideoViewCount();
            long totalAdvertisementViews = videoSnapshot.getAdvertisementViewCount();
            long currentAdvertisementViews = dailyStatistic.getAdvertisementViewCount();

            BigDecimal videoRevenue = revenueCalculator.views(totalVideoViews, currentVideoViews);
            BigDecimal advertisementRevenue = revenueCalculator.advertisements(totalAdvertisementViews, currentAdvertisementViews);

            DailySettlement dailySettlement = DailySettlement.from(videoRevenue, advertisementRevenue, video);

            return SettlementWrapper.from(dailySettlement, videoSnapshot);
        };
    }

    // DailyStatistic, VideoSnapshot 저장 및 병합 수행
    @Bean
    public ItemWriter<SettlementWrapper> dailySettlementWriter() {
        return settlementResult -> {
            EntityManager em = entityManagerFactory.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            try {
                tx.begin();
                for (SettlementWrapper wrapper : settlementResult) {
                    em.merge(wrapper.getVideoSnapshot());
                    em.persist(wrapper.getDailySettlement());
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
