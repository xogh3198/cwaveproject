package com.example.demo.controller;

import com.example.demo.service.KafkaAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    private final KafkaAdminService kafkaAdminService;
    private static final String PENDING_TOPIC = "reservation.pending";

    public QueueController(KafkaAdminService kafkaAdminService) {
        this.kafkaAdminService = kafkaAdminService;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getQueueStatus() {
        try {
            long lag = kafkaAdminService.getConsumerGroupLag(PENDING_TOPIC);
            Map<String, Long> response = new HashMap<>();
            response.put("queueSize", lag);
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body("대기열 상태를 조회하는 중 오류가 발생했습니다.");
        }
    }
}