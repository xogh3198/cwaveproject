// src/main/java/com/example/demo/controller/ReservationController.java
package com.example.demo.controller;

import com.example.demo.domain.Reservation;
import com.example.demo.service.ReservationService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // <<<--- 예매 ID로 예매 내역을 조회하는 API --->>>
    @GetMapping("/{id}")
    // public ResponseEntity<Reservation> getReservationDetails(@PathVariable Long id) {
    //     return reservationService.getReservationById(id)
    //             .map(ResponseEntity::ok)
    //             .orElse(ResponseEntity.notFound().build());
    // }
    public ResponseEntity<Reservation> getReservationDetails(@PathVariable Long id) {
        // 이 로직이 올바르게 Reservation 객체를 JSON으로 반환하는지 확인
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }
}