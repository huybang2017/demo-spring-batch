package com.example.springbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBatchApplication {

    public static void main(String[] args) {
        // Chạy bình thường để có thể truy cập H2 Console
        SpringApplication.run(SpringBatchApplication.class, args);
    }
}