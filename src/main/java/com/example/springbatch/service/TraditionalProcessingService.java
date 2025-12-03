package com.example.springbatch.service;

import com.example.springbatch.model.Person;
import com.example.springbatch.model.ProcessingResult;
import com.example.springbatch.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// Service xử lý CSV theo cách TRUYỀN THỐNG (KHÔNG dùng Spring Batch)
// Để so sánh với Spring Batch về:
// - Performance
// - Memory usage
// - Error handling
// - Transaction management

@Service
public class TraditionalProcessingService {

    private static final Logger log = LoggerFactory.getLogger(TraditionalProcessingService.class);

    @Autowired
    private PersonRepository personRepository;

    @Transactional
    public ProcessingResult processTraditionalWay(String fileName) throws IOException {
        long startTime = System.currentTimeMillis();
        log.info("=== BẮT ĐẦU XỬ LÝ TRUYỀN THỐNG ===");

        // Clear old data
        personRepository.deleteAll();

        List<Person> allPersons = new ArrayList<>();
        int totalRead = 0;
        int totalProcessed = 0;
        int totalSkipped = 0;

        // Đọc TOÀN BỘ file vào memory
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(fileName).getInputStream()))) {

            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                totalRead++;
                String[] fields = line.split(",");

                if (fields.length >= 5) {
                    try {
                        String firstName = fields[0].toUpperCase();
                        String lastName = fields[1].toUpperCase();
                        String email = fields[2].toLowerCase();
                        Integer age = Integer.parseInt(fields[3]);
                        Double salary = Double.parseDouble(fields[4]) * 1.10;

                        if (age < 18) {
                            totalSkipped++;
                            log.warn("Bỏ qua: {} {} (tuổi: {})", firstName, lastName, age);
                            continue;
                        }

                        if (totalRead % 25 == 0) {
                            Thread.sleep(5);
                        }

                        Person person = new Person(firstName, lastName, email, age, salary);
                        allPersons.add(person);
                        totalProcessed++;

                    } catch (Exception e) {
                        log.error("Lỗi xử lý dòng {}: {}", totalRead, e.getMessage());
                    }
                }
            }
        }

        // Lưu TOÀN BỘ vào database một lúc
        log.info("Lưu {} records vào database...", allPersons.size());
        personRepository.saveAll(allPersons);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Tính memory usage
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        String memoryUsage = usedMemory + " MB";

        ProcessingResult result = new ProcessingResult(
                "TRADITIONAL", totalRead, totalProcessed, totalSkipped, duration, memoryUsage);

        log.info("=== KẾT THÚC XỬ LÝ TRUYỀN THỐNG ===");
        log.info("Thời gian: {} ms", duration);
        log.info("Đọc: {}, Xử lý: {}, Bỏ qua: {}", totalRead, totalProcessed, totalSkipped);

        return result;
    }
}
