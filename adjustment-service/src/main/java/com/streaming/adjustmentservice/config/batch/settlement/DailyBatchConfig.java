//package com.streaming.adjustmentservice.config.batch;
//
//import com.streaming.adjustmentservice.dto.SettlementWrapper;
//import com.streaming.adjustmentservice.dto.StatisticWrapper;
//import com.streaming.adjustmentservice.entity.VideoSnapshot;
//import com.streaming.adjustmentservice.entity.settlement.DailySettlement;
//import com.streaming.adjustmentservice.entity.statistic.DailyStatistic;
//import com.streaming.adjustmentservice.service.RevenueCalculatorService;
//import com.streaming.adjustmentservice.service.port.DailyStatisticRepository;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityManagerFactory;
//import jakarta.persistence.EntityTransaction;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.item.database.JpaPagingItemReader;
//import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Collections;
//
//@Configuration
//@RequiredArgsConstructor
//public class DailyBatchConfig {
//
//    private final JobRepository jobRepository;
//    private final PlatformTransactionManager platformTransactionManager;
//    private final RevenueCalculatorService revenueCalculatorService;
//    private final EntityManagerFactory entityManagerFactory;
//    private final DailyStatisticRepository dailyStatisticRepository;
//
//    // -1일 00:00:00
//    private final LocalDateTime START_DATE =
//            LocalDateTime.now().minusDays(1)
//                    .withHour(0)
//                    .withMinute(0)
//                    .withSecond(0);
//    // 00:00:00
//    private final LocalDateTime END_DATE =
//            LocalDateTime.now()
//                    .withHour(0)
//                    .withMinute(0)
//                    .withSecond(0);
//
//    // -1일
//    private final LocalDate TARGET_DATE = LocalDate.now().minusDays(1);
//
//
//    // FIXME JDBC로 추후 성능 개선 요망 (전체 코드에 대해서)
//    @Bean
//    public Job dailyStatisticJob() {
//        return new JobBuilder("dailyStatisticJob", jobRepository)
//                .start(dailySettlementStep())
//                .build();
//    }
//
//    // 정산 스텝
//    @Bean
//    public Step dailySettlementStep() {
//        return new StepBuilder("dailySettlementStep", jobRepository)
//                .<StatisticWrapper, SettlementWrapper>chunk(10, platformTransactionManager)
//                .reader(dailySettlementReader())
//                .processor(dailySettlementProcessor())
//                .writer(dailySettlementWriter())
//                .build();
//    }
//
//    // StatisticWrapper로 앞서 저장한 DailyStatisitc과, 조회수 누적합을 저장한 VideoSnapshot 가져오기
//    @Bean
//    public JpaPagingItemReader<StatisticWrapper> dailySettlementReader() {
//        return new JpaPagingItemReaderBuilder<StatisticWrapper>()
//                .name("dailySettlementReader")
//                .entityManagerFactory(entityManagerFactory)
//                .pageSize(10)
//                .queryString("""
//                        select new com.streaming.settlement.adjustmentservice.dto.StatisticWrapper(ds, vs, ds.videoId)
//                        from DailyStatistic ds
//                        left join VideoSnapshot vs on vs.video.id = ds.video.id
//                        where ds.statisticDate = :TARGET_DATE
//                        """)
//                .parameterValues(Collections.singletonMap("TARGET_DATE", TARGET_DATE))
//                .build();
//    }
//
//    // 누적합 업데이트 및 정산 로직 수행
//    @Bean
//    public ItemProcessor<StatisticWrapper, SettlementWrapper> dailySettlementProcessor() {
//        return statisticWrapper -> {
//            DailyStatistic dailyStatistic = statisticWrapper.getDailyStatistic();
//            VideoSnapshot videoSnapshot = statisticWrapper.getVideoSnapshot();
//            Long videoId = statisticWrapper.getDailyStatistic().getVideoId();
//
//            videoSnapshot.updateViewCount(dailyStatistic);
//            videoSnapshot.updateDate(TARGET_DATE);
//
//            long totalVideoViews = videoSnapshot.getVideoViewCount();
//            long currentVideoViews = dailyStatistic.getVideoViewCount();
//            long totalAdvertisementViews = videoSnapshot.getAdvertisementViewCount();
//            long currentAdvertisementViews = dailyStatistic.getAdvertisementViewCount();
//
//            BigDecimal videoRevenue = revenueCalculatorService.views(totalVideoViews, currentVideoViews);
//            BigDecimal advertisementRevenue = revenueCalculatorService.advertisements(totalAdvertisementViews, currentAdvertisementViews);
//
//            DailySettlement dailySettlement = DailySettlement.from(videoRevenue, advertisementRevenue, videoId);
//
//            return SettlementWrapper.from(dailySettlement, videoSnapshot);
//        };
//    }
//
//    // DailyStatistic, VideoSnapshot 저장 및 병합 수행
//    @Bean
//    public ItemWriter<SettlementWrapper> dailySettlementWriter() {
//        return settlementResult -> {
//            EntityManager em = entityManagerFactory.createEntityManager();
//            EntityTransaction tx = em.getTransaction();
//
//            try {
//                tx.begin();
//                for (SettlementWrapper wrapper : settlementResult) {
//                    em.merge(wrapper.getVideoSnapshot());
//                    em.persist(wrapper.getDailySettlement());
//                }
//                tx.commit();
//            } catch (Exception ex) {
//                tx.rollback();
//            } finally {
//                em.close();
//            }
//        };
//    }
//}
