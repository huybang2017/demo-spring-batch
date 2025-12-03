package com.example.springbatch.model;

/**
 * Model class để lưu kết quả xử lý batch
 * Dùng chung cho cả Traditional và Spring Batch processing
 */
public class ProcessingResult {
    private String method;
    private int totalRead;
    private int totalProcessed;
    private int totalSkipped;
    private long durationMs;
    private String memoryUsage;

    /**
     * Constructor
     * 
     * @param method         Tên phương pháp ("TRADITIONAL" hoặc "SPRING BATCH")
     * @param totalRead      Tổng số dòng đã đọc từ file
     * @param totalProcessed Tổng số dòng đã xử lý thành công
     * @param totalSkipped   Tổng số dòng bị bỏ qua (filter)
     * @param durationMs     Thời gian xử lý (milliseconds)
     */
    public ProcessingResult(String method, int totalRead, int totalProcessed,
            int totalSkipped, long durationMs) {
        this.method = method;
        this.totalRead = totalRead;
        this.totalProcessed = totalProcessed;
        this.totalSkipped = totalSkipped;
        this.durationMs = durationMs;

        // Tính memory usage
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        this.memoryUsage = usedMemory + " MB";
    }

    /**
     * Constructor đầy đủ với memory usage đã tính sẵn
     */
    public ProcessingResult(String method, int totalRead, int totalProcessed,
            int totalSkipped, long durationMs, String memoryUsage) {
        this.method = method;
        this.totalRead = totalRead;
        this.totalProcessed = totalProcessed;
        this.totalSkipped = totalSkipped;
        this.durationMs = durationMs;
        this.memoryUsage = memoryUsage;
    }

    // Getters
    public String getMethod() {
        return method;
    }

    public int getTotalRead() {
        return totalRead;
    }

    public int getTotalProcessed() {
        return totalProcessed;
    }

    public int getTotalSkipped() {
        return totalSkipped;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public String getMemoryUsage() {
        return memoryUsage;
    }

    // Setters (optional, for flexibility)
    public void setMethod(String method) {
        this.method = method;
    }

    public void setTotalRead(int totalRead) {
        this.totalRead = totalRead;
    }

    public void setTotalProcessed(int totalProcessed) {
        this.totalProcessed = totalProcessed;
    }

    public void setTotalSkipped(int totalSkipped) {
        this.totalSkipped = totalSkipped;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public void setMemoryUsage(String memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    @Override
    public String toString() {
        return String.format(
                "ProcessingResult{method='%s', read=%d, processed=%d, skipped=%d, duration=%dms, memory=%s}",
                method, totalRead, totalProcessed, totalSkipped, durationMs, memoryUsage);
    }
}
