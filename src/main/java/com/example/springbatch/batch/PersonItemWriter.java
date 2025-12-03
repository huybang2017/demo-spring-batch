package com.example.springbatch.batch;

import com.example.springbatch.model.Person;
import com.example.springbatch.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersonItemWriter implements ItemWriter<Person> {

    private static final Logger log = LoggerFactory.getLogger(PersonItemWriter.class);

    @Autowired
    private PersonRepository personRepository;

    @Override
    public void write(Chunk<? extends Person> chunk) throws Exception {
        log.info("Writing {} persons to database", chunk.size());
        personRepository.saveAll(chunk.getItems());
        log.info("Successfully wrote {} persons", chunk.size());
    }
}
