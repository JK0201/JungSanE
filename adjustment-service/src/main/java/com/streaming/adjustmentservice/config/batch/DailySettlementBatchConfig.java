//package com.streaming.adjustmentservice.config.batch;
//
//import com.streaming.adjustmentservice.repository.VideoSnapshotJpaRepository;
//import jakarta.persistence.EntityManagerFactory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import java.time.LocalDate;
//
//@Configuration
//@RequiredArgsConstructor
//public class DailySettlementBatchConfig {
//
//    private final JobRepository jobRepository;
//    private final EntityManagerFactory entityManagerFactory;
//    private final VideoSnapshotJpaRepository videoSnapshotJpaRepository;
//
//    private final LocalDate TARGET_DATE = LocalDate.now().minusDays(1);
//
//    @Bean
//    public Job dailySettlementJob(
//            @Qualifier("metaTransactionManager") PlatformTransactionManager transactionManager
//    ) {
//        return new JobBuilder("dailySettlementJob", jobRepository)
//                .start(dailySettlementStep(transactionManager))
//                .build();
//    }
//
//    @Bean
//    public Step dailySettlementStep(PlatformTransactionManager transactionManager) {
//        return new StepBuilder("dailySettlementStep", jobRepository)
//                .<>
//    }
//}
