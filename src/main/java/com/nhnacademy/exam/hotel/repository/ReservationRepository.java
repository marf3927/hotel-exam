package com.nhnacademy.exam.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.exam.hotel.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryQueryDsl {
}
