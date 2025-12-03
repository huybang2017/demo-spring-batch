package com.example.springbatch.controller;

import com.example.springbatch.model.ProcessingResult;
import com.example.springbatch.service.BatchProcessingService;
import com.example.springbatch.service.TraditionalProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller để demo và so sánh giữa:
 * 1. Xử lý truyền thống (không dùng Spring Batch)
 * 2. Xử lý với Spring Batch
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @Autowired
    private TraditionalProcessingService traditionalService;

    @Autowired
    private BatchProcessingService batchService;

    /**
     * Demo xử lý TRUYỀN THỐNG (không dùng Spring Batch)
     * http://localhost:8080/api/demo/traditional
     */
    @GetMapping("/traditional")
    public Map<String, Object> runTraditionalProcessing() {
        Map<String, Object> response = new HashMap<>();

        try {
            ProcessingResult result = traditionalService
                    .processTraditionalWay("input-data.csv");

            response.put("success", true);
            response.put("method", "TRADITIONAL PROCESSING");
            response.put("result", result);
            response.put("note", "Xử lý truyền thống - Load toàn bộ file vào memory");

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }

        return response;
    }

    /**
     * Demo xử lý với SPRING BATCH
     * http://localhost:8080/api/demo/batch
     */
    @GetMapping("/batch")
    public Map<String, Object> runBatchProcessing() {
        Map<String, Object> response = new HashMap<>();

        try {
            ProcessingResult result = batchService.processBatchWay();

            response.put("success", true);
            response.put("method", "SPRING BATCH PROCESSING");
            response.put("result", result);
            response.put("note", "Xử lý với Spring Batch - Chunk processing, transaction management");

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }

        return response;
    }

    /**
     * So sánh cả 2 phương pháp
     * http://localhost:8080/api/demo/compare
     */
    @GetMapping("/compare")
    public Map<String, Object> compareProcessing() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Chạy Traditional
            ProcessingResult traditionalResult = traditionalService
                    .processTraditionalWay("input-data.csv");

            Thread.sleep(1000); // Đợi 1 giây để tách biệt

            // Chạy Spring Batch
            ProcessingResult batchResult = batchService.processBatchWay();

            response.put("success", true);
            response.put("traditional", traditionalResult);
            response.put("springBatch", batchResult);
            response.put("comparison", createComparison(traditionalResult, batchResult));

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }

        return response;
    }

    /**
     * Thông tin hướng dẫn
     */
    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("title", "Spring Batch Demo - So sánh 2 phương pháp xử lý");
        info.put("endpoints", Map.of(
                "/api/demo/traditional", "Chạy xử lý truyền thống (không dùng Spring Batch)",
                "/api/demo/batch", "Chạy xử lý với Spring Batch",
                "/api/demo/compare", "So sánh cả 2 phương pháp",
                "/api/persons", "Xem dữ liệu trong database"));
        info.put("benefits", Map.of(
                "Chunk Processing", "Spring Batch xử lý theo chunk (10 items/lần) thay vì load hết vào memory",
                "Transaction Management",
                "Mỗi chunk là 1 transaction riêng, lỗi ở chunk này không ảnh hưởng chunk khác",
                "Restart/Retry", "Spring Batch có cơ chế restart job từ điểm lỗi, retry khi có lỗi tạm thời",
                "Monitoring", "Spring Batch lưu metadata trong database để theo dõi tiến độ",
                "Scalability", "Dễ dàng scale với parallel processing, partitioning"));
        return info;
    }

    private Map<String, String> createComparison(
            ProcessingResult traditional,
            ProcessingResult batch) {

        Map<String, String> comparison = new HashMap<>();

        long timeDiff = traditional.getDurationMs() - batch.getDurationMs();
        String timeComparison = timeDiff > 0
                ? "Spring Batch nhanh hơn " + timeDiff + "ms"
                : "Traditional nhanh hơn " + Math.abs(timeDiff) + "ms";

        comparison.put("time", timeComparison);
        comparison.put("traditional_time", traditional.getDurationMs() + "ms");
        comparison.put("batch_time", batch.getDurationMs() + "ms");
        comparison.put("traditional_memory", traditional.getMemoryUsage());
        comparison.put("batch_memory", batch.getMemoryUsage());

        return comparison;
    }
}
