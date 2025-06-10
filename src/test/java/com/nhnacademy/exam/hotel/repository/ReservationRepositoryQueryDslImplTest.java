package com.nhnacademy.exam.hotel.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.nhnacademy.exam.hotel.domain.Hotel;
import com.nhnacademy.exam.hotel.domain.Reservation;
import com.nhnacademy.exam.hotel.domain.ReservationStatus;
import com.nhnacademy.exam.hotel.domain.Room;
import com.nhnacademy.exam.hotel.domain.ViewType;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@DataJpaTest
class ReservationRepositoryQueryDslImplTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private JPAQueryFactory queryFactory; // 주입 받음

	private ReservationRepositoryQueryDslImpl reservationRepositoryQueryDsl;

	@TestConfiguration
	static class TestConfig {
		@Bean
		public JPAQueryFactory jpaQueryFactory(EntityManager em) {
			return new JPAQueryFactory(em);
		}
	}

	@BeforeEach
	void setUp() {
		reservationRepositoryQueryDsl = new ReservationRepositoryQueryDslImpl(queryFactory); // 주입 받은 Factory 사용

		Hotel hotel = new Hotel(1L, "Test Hotel", LocalDateTime.now(), LocalDateTime.now());
		entityManager.persist(hotel);

		Room room = Room.builder()
			.hotel(hotel).name("Room1").capacity((byte)2).floor((byte)1)
			.bathtubFlag(false).viewType(ViewType.CITY)
			.price(BigDecimal.TEN).peakSeasonPrice(BigDecimal.TEN)
			.build();
		entityManager.persist(room);

		Long userId = 1L;
		LocalDate d1 = LocalDate.of(2025, 7, 10);
		LocalDate d2 = LocalDate.of(2025, 7, 12);
		LocalDate d3 = LocalDate.of(2025, 7, 11);
		LocalDate d4 = LocalDate.of(2025, 7, 13);
		LocalDate d5 = LocalDate.of(2025, 7, 15);
		LocalDate d6 = LocalDate.of(2025, 7, 17);

		Reservation r1 = new Reservation(userId, room, d1, d2, 2, BigDecimal.ZERO, ReservationStatus.CONFIRMED);
		Reservation r2 = new Reservation(userId, room, d3, d4, 2, BigDecimal.ZERO, ReservationStatus.CONFIRMED);
		Reservation r3 = new Reservation(userId, room, d5, d6, 2, BigDecimal.ZERO, ReservationStatus.CONFIRMED);
		Reservation r4 = new Reservation(userId, room, d1, d2, 2, BigDecimal.ZERO, ReservationStatus.CANCELED);
		Reservation r5 = new Reservation(2L, room, d1, d2, 2, BigDecimal.ZERO, ReservationStatus.CONFIRMED);

		entityManager.persist(r1);
		entityManager.persist(r2);
		entityManager.persist(r3);
		entityManager.persist(r4);
		entityManager.persist(r5);
		entityManager.flush();
	}

	@Test
	@DisplayName("특정 사용자의 겹치는 예약 개수 조회 (QueryDSL Impl)")
	void countOverlappingReservationsForUserQueryDsl() {
		Long userId = 1L;
		LocalDate checkIn = LocalDate.of(2025, 7, 11);
		LocalDate checkOut = LocalDate.of(2025, 7, 13);

		long count = reservationRepositoryQueryDsl.countOverlappingReservationsForUserQueryDsl(userId, checkIn,
			checkOut);

		assertThat(count).isEqualTo(2);
	}

	@Test
	@DisplayName("겹치는 예약 없는 경우 조회 (QueryDSL Impl)")
	void countOverlappingReservationsForUserQueryDsl_NoOverlap() {
		Long userId = 1L;
		LocalDate checkIn = LocalDate.of(2025, 8, 1);
		LocalDate checkOut = LocalDate.of(2025, 8, 3);

		long count = reservationRepositoryQueryDsl.countOverlappingReservationsForUserQueryDsl(userId, checkIn,
			checkOut);

		assertThat(count).isEqualTo(0);
	}
}