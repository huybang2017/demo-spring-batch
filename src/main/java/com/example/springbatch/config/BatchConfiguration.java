package com.example.springbatch.config;

import com.example.springbatch.batch.PersonItemProcessor;
import com.example.springbatch.batch.PersonItemWriter;
import com.example.springbatch.dto.PersonInputDTO;
import com.example.springbatch.model.Person;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfiguration {

    @Autowired
    private PersonItemProcessor personItemProcessor;

    @Autowired
    private PersonItemWriter personItemWriter;

    @Bean
    public FlatFileItemReader<PersonInputDTO> reader() {
        return new FlatFileItemReaderBuilder<PersonInputDTO>()
                .name("personItemReader")
                .resource(new ClassPathResource("input-data.csv"))
                .delimited()
                .names("firstName", "lastName", "email", "age", "salary")
                .linesToSkip(1)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                    {
                        setTargetType(PersonInputDTO.class);
                    }
                })
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<PersonInputDTO, Person>chunk(5000, transactionManager)
                .reader(reader())
                .processor(personItemProcessor)
                .writer(personItemWriter)
                .build();
    }

    @Bean
    public Job importPersonJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
        return new JobBuilder("importPersonJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }
}
