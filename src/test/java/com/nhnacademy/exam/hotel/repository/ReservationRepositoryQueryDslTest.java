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
class ReservationRepositoryQueryDslTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private ReservationRepository reservationRepository;

	@TestConfiguration
	static class TestConfig {
		@Bean
		public JPAQueryFactory jpaQueryFactory(EntityManager em) {
			return new JPAQueryFactory(em);
		}
	}

	@BeforeEach
	void setUp() {
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

		Reservation r1 = new Reservation(userId, room, d1, d2, 2, BigDecimal.ZERO, ReservationStatus.CONFIRMED);
		Reservation r2 = new Reservation(userId, room, d3, d4, 2, BigDecimal.ZERO, ReservationStatus.CONFIRMED);

		entityManager.persist(r1);
		entityManager.persist(r2);
		entityManager.flush();
	}

	@Test
	@DisplayName("특정 사용자의 겹치는 예약 개수 조회 (ReservationRepository)")
	void countOverlappingReservationsForUserQueryDsl_Through_ReservationRepository() {
		Long userId = 1L;
		LocalDate checkIn = LocalDate.of(2025, 7, 11);
		LocalDate checkOut = LocalDate.of(2025, 7, 13);

		long count = reservationRepository.countOverlappingReservationsForUserQueryDsl(userId, checkIn, checkOut);

		assertThat(count).isEqualTo(2);
	}
}