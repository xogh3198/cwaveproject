package com.example.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor // <<<--- 기본 생성자 추가
public class Reservation {
    private Long id;
    private Long performanceId;
    private String seatNumber;
    private LocalDateTime reservationTime;

    // 커스텀 생성자 (기존)
    // public Reservation(Long performanceId, String seatNumber) {
    //     this.performanceId = performanceId;
    //     this.seatNumber = seatNumber;
    //     this.reservationTime = LocalDateTime.now();
    // }
    public Reservation(Long id,Long performanceId, String seatNumber) {
        this.id = id;
        this.performanceId = performanceId;
        this.seatNumber = seatNumber;
        this.reservationTime = LocalDateTime.now();
    }
//     public Reservation(Long id) {
//     this.id = id;
// }
    public Reservation(Long id, Long performanceId, String seatNumber, LocalDateTime reservationTime) {
        this.id = id;
        this.performanceId = performanceId;
        this.seatNumber = seatNumber;
        this.reservationTime = reservationTime;
    }
}