package com.nhnacademy.exam.hotel.repository;

import java.time.LocalDate;

import com.nhnacademy.exam.hotel.domain.QReservation;
import com.nhnacademy.exam.hotel.domain.ReservationStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReservationRepositoryQueryDslImpl implements ReservationRepositoryQueryDsl {
	private final JPAQueryFactory queryFactory;

	@Override
	public long countOverlappingReservationsForUserQueryDsl(Long userId, LocalDate checkInDate,
		LocalDate checkOutDate) {
		QReservation reservation = QReservation.reservation;

		Long count = queryFactory
			.select(reservation.count())
			.from(reservation)
			.where(
				reservation.userId.eq(userId),
				reservation.status.eq(ReservationStatus.CONFIRMED),
				reservation.checkInDate.lt(checkOutDate),
				reservation.checkOutDate.gt(checkInDate)
			)
			.fetchOne();

		return count != null ? count : 0L;
	}
}