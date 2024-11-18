package com.streaming.adjustmentservice.config.batch;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StatisticDummyTest {

    @Autowired
    private Job statisticDummyJob;

    @Autowired
    private JobLauncher jobLauncher;

    @Test
    void runBatchJob() throws Exception {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(statisticDummyJob, parameters);
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
}