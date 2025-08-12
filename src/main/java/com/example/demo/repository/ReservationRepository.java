package com.example.demo.repository;

import com.example.demo.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Reservation 엔티티와 기본 키 타입(Long)을 지정
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}