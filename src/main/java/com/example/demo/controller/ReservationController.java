// src/main/java/com/example/demo/controller/ReservationController.java
package com.example.demo.controller;

import com.example.demo.domain.Reservation;
import com.example.demo.service.ReservationService;
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
    public ResponseEntity<Reservation> getReservationDetails(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}