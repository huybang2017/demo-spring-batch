package com.example.springbatch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springbatch.model.ProcessingResult;
import com.example.springbatch.repository.PersonRepository;

// Service xử lý bằng Spring Batch
@Service
public class BatchProcessingService {

    private static final Logger log = LoggerFactory.getLogger(BatchProcessingService.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importPersonJob;

    @Autowired
    private PersonRepository personRepository;

    public ProcessingResult processBatchWay() throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("=== BẮT ĐẦU XỬ LÝ VỚI SPRING BATCH ===");

        // Clear old data
        personRepository.deleteAll();

        // Chạy batch job
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(importPersonJob, jobParameters);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Lấy thông tin từ StepExecution
        int totalRead = 0;
        int totalSkipped = 0;
        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            totalRead += stepExecution.getReadCount();
            totalSkipped += stepExecution.getReadSkipCount() + stepExecution.getProcessSkipCount()
                    + stepExecution.getWriteSkipCount();
        }

        // Đếm kết quả từ database
        long totalProcessed = personRepository.count();

        // Tính memory usage
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        String memoryUsage = usedMemory + " MB";

        ProcessingResult result = new ProcessingResult(
                "SPRING BATCH", totalRead, (int) totalProcessed, totalSkipped, duration, memoryUsage);

        log.info("=== KẾT THÚC XỬ LÝ VỚI SPRING BATCH ===");
        log.info("Thời gian: {} ms", duration);
        log.info("Đọc: {}, Xử lý: {}, Bỏ qua: {}", totalRead, totalProcessed, totalSkipped);

        return result;
    }
}
