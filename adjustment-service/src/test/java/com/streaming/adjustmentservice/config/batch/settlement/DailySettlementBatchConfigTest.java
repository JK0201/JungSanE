package com.streaming.adjustmentservice.config.batch.settlement;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j(topic = "Settlement Batch Test")
@SpringBootTest
class DailySettlementBatchConfigTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job dailySettlementJob;

    @Autowired
    private TaskExecutor statisticTaskExecutor;

    @Test
    @DisplayName("DailySettlement 배치 작업 성능 테스트")
    void testBatchJobPerformance() throws Exception {
        // Given
        long initialPlaybackCount = 20_000_000;
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("datetime", LocalDateTime.now().toString())
                .toJobParameters();

        // When
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Statistic Batch Job Execution");

        JobExecution jobExecution = jobLauncher.run(dailySettlementJob, jobParameters);

        stopWatch.stop();

        // Then
        ExitStatus exitStatus = jobExecution.getExitStatus();
        BatchStatus batchStatus = jobExecution.getStatus();
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();

        ThreadPoolTaskExecutor threadPoolExecutor = (ThreadPoolTaskExecutor) statisticTaskExecutor;
        ThreadPoolExecutor executor = threadPoolExecutor.getThreadPoolExecutor();

        double totalTimeSeconds = stopWatch.getTotalTimeSeconds();
        long writeCount = stepExecution.getWriteCount();
        long readCount = stepExecution.getReadCount();
        double completionPercentage = (writeCount * 100.0) / readCount;
        double processingSpeed = writeCount / totalTimeSeconds;

        // Statistic 배치 작업 결과
        log.info("\n==== Batch Job Execution Results ====");
        log.info("Job Status: {}", jobExecution.getStatus());
        log.info("Exit Status: {}", jobExecution.getExitStatus());
        log.info("Completion: {}%", String.format("%.2f", completionPercentage));
        log.info("");
        log.info("Total Execution Time: {} seconds", String.format("%.2f", totalTimeSeconds));
        log.info("Input Data Count: {}", String.format("%,d", initialPlaybackCount));
        log.info("Read Count: {}", String.format("%,d", readCount));
        log.info("Write Count: {}", String.format("%,d", writeCount));
        log.info("Processing Speed: {} items/second", String.format("%,.2f", processingSpeed));
        log.info("Commit Count: {}", stepExecution.getCommitCount());
        log.info("==================================\n");
        
        assertThat(batchStatus).isEqualTo(BatchStatus.COMPLETED);
        assertThat(exitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
        assertThat(completionPercentage).isEqualTo(100.0);
    }
}