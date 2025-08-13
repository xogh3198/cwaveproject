package com.example.demo.domain;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공연 정보")
@Getter @Setter
public class Performance {
    @Schema(description = "공연 ID", example = "1")
    private Long id;
    
    @Schema(description = "공연 이름", example = "오페라의 유령")
    private String name;
    
    @Schema(description = "공연 설명", example = "세계적으로 유명한 뮤지컬 오페라의 유령")
    private String description;
    
    @Schema(description = "일정별 잔여 좌석 수", example = "{\"2024-12-25 19:00\": 50, \"2024-12-26 19:00\": 30}")
    private Map<String, AtomicInteger> remainingSeatsBySchedule;
    
    @Schema(description = "전체 잔여 좌석 수", example = "80")
    private AtomicInteger remainingSeats; // 동시성 제어를 위해 AtomicInteger 사용

    public Performance(Long id, String name, String description, Map<String, Integer> scheduleWithSeats) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.remainingSeatsBySchedule = new ConcurrentHashMap<>();
        scheduleWithSeats.forEach((schedule, seats) -> {
            this.remainingSeatsBySchedule.put(schedule, new AtomicInteger(seats));
        });
}
}