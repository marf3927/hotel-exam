package com.nhnacademy.exam.hotel.repository;

import java.time.LocalDate;

public interface ReservationRepositoryQueryDsl {
	long countOverlappingReservationsForUserQueryDsl(Long userId, LocalDate checkInDate, LocalDate checkOutDate);

}
