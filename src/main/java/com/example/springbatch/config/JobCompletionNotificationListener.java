package com.example.springbatch.config;

import com.example.springbatch.model.Person;
import com.example.springbatch.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Autowired
    private PersonRepository personRepository;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("!!! JOB STARTING !!!");
        log.info("Job Name: {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED !!!");
            log.info("Verifying the results....");

            long count = personRepository.count();
            log.info("Total persons in database: {}", count);

            List<Person> samplePersons = personRepository.findAll().stream()
                    .limit(5)
                    .toList();

            log.info("Sample of first 5 persons:");
            samplePersons.forEach(person -> log.info("Found <{}> in the database.", person));

        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("!!! JOB FAILED !!!");
            jobExecution.getAllFailureExceptions().forEach(throwable -> log.error("Exception: ", throwable));
        }
    }
}
