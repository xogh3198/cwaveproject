package com.example.demo.domain;


import java.util.concurrent.atomic.AtomicInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Data
@EqualsAndHashCode
@NoArgsConstructor
@Getter @Setter @AllArgsConstructor
public class Performance {
    private Long id;
    private String name;
    private String description;
    private AtomicInteger remainingSeats; // 동시성 제어를 위해 AtomicInteger 사용
}