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

//@ToString
//@Data
//@EqualsAndHashCode
//@NoArgsConstructor
@Getter @Setter //@AllArgsConstructor
public class Performance {
    private Long id;
    private String name;
    private String description;
    private Map<String, AtomicInteger> remainingSeatsBySchedule;
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