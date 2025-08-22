package com.example.demo.domain;

import java.io.Serializable; // Serializable 인터페이스 임포트
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "schedules")
@Getter @Setter
@NoArgsConstructor
public class Schedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 어노테이션을 추가하여 Schedule -> Performance 순환 참조를 끊습니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    @JsonIgnore 
    private Performance performance;

    @Column(name = "schedule_time", nullable = false)
    private LocalDateTime scheduleTime;

    @Column(name = "remaining_seats", nullable = false)
    private int remainingSeats;
}