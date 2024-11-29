package com.streaming.adjustmentservice.config.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchSchedulerConfig {

    private final JobLauncher jobLauncher;
    private final Job dailyStatisticJob;
    private final Job dailySettlementJob;

//    @Scheduled(cron = "0 0 0/1 * * *")
//    public void runDailyStatisticJob() {
//        try {
//            JobParameters jobParameters = new JobParametersBuilder()
//                    .addString("datetime", LocalDateTime.now().toString())
//                    .toJobParameters();
//
//            log.info("Start Statistic Job: {}", LocalDateTime.now());
//            JobExecution jobExecution = jobLauncher.run(dailyStatisticJob, jobParameters);
//            log.info("Finish Daily Statistic Job: {}, Status: {}", LocalDateTime.now(), jobExecution.getStatus());
//        } catch (Exception ex) {
//            log.error("Daily Statistic Job failed", ex);
//        }
//    }
//
//    @Scheduled(cron = "0 0 5 * * *")
//    public void runDailySettlementJob() {
//        try {
//            JobParameters jobParameters = new JobParametersBuilder()
//                    .addString("datetime", LocalDateTime.now().toString())
//                    .toJobParameters();
//
//            log.info("Start Settlement Job: {}", LocalDateTime.now());
//            JobExecution jobExecution = jobLauncher.run(dailySettlementJob, jobParameters);
//            log.info("Finish Settlement Job: {}, Status: {}", LocalDateTime.now(), jobExecution.getStatus());
//        } catch (Exception ex) {
//            log.error("Daily Settlement Job failed", ex);
//        }
//    }
}
