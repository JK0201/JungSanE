package com.streaming.adjustmentservice.config.batch.statistic;

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

@Slf4j(topic = "Statistic Batch Test")
@SpringBootTest
class DailyStatisticBatchConfigTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job dailyStatisticJob;

    @Autowired
    private TaskExecutor statisticTaskExecutor;

    @Test
    @DisplayName("DailyStatistic 배치 작업 성능 테스트")
    void testBatchJobPerformance() throws Exception {
        // Given
        long initialPlaybackCount = 20_000_000;
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("datetime", LocalDateTime.now().toString())
                .toJobParameters();

        // When
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Statistic Batch Job Execution");

        JobExecution jobExecution = jobLauncher.run(dailyStatisticJob, jobParameters);

        stopWatch.stop();

        // Then
        ExitStatus exitStatus = jobExecution.getExitStatus();
        BatchStatus batchStatus = jobExecution.getStatus();
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();

        ThreadPoolTaskExecutor threadPoolExecutor = (ThreadPoolTaskExecutor) statisticTaskExecutor;
        ThreadPoolExecutor executor = threadPoolExecutor.getThreadPoolExecutor();

        assertThat(batchStatus).isEqualTo(BatchStatus.COMPLETED);
        assertThat(exitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

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

        // 쓰레드 풀
        log.info("\n==== Thread Pool Statistics ====");
        log.info("Active Threads: {}", executor.getActiveCount());
        log.info("Core Pool Size: {}", executor.getCorePoolSize());
        log.info("Current Pool Size: {}", executor.getPoolSize());
        log.info("Largest Pool Size: {}", executor.getLargestPoolSize());
        log.info("Maximum Pool Size: {}", executor.getMaximumPoolSize());
        log.info("Completed Task Count: {}", executor.getCompletedTaskCount());
        log.info("Queue Size: {}", executor.getQueue().size());
        log.info("==================================\n");

        assertThat(completionPercentage).isEqualTo(100.0);
    }
}