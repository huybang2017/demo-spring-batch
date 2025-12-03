package com.example.springbatch.batch;

import com.example.springbatch.dto.PersonInputDTO;
import com.example.springbatch.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class PersonItemProcessor implements ItemProcessor<PersonInputDTO, Person> {

    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

    @Override
    public Person process(PersonInputDTO item) throws Exception {

        String firstName = item.getFirstName().toUpperCase();
        String lastName = item.getLastName().toUpperCase();

        // Example: Calculate bonus (10% of salary)
        Double salaryWithBonus = item.getSalary() * 1.10;

        Person transformedPerson = new Person(
                firstName,
                lastName,
                item.getEmail().toLowerCase(),
                item.getAge(),
                salaryWithBonus);

        // Log chi tiết đã bị tắt để tăng performance
        // log.info("Processing person: {} {} -> Salary updated from {} to {}",
        // item.getFirstName(), item.getLastName(), item.getSalary(), salaryWithBonus);

        if (item.getAge() < 18) {
            // Log warning cho trường hợp bỏ qua vẫn giữ để debug
            log.warn("Skipping person {} {} (age: {})", firstName, lastName, item.getAge());
            return null;
        }

        return transformedPerson;
    }
}
