package com.nhnacademy.exam.hotel.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

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
class ReservationRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private ReservationRepository reservationRepository;

	private Room room;
	private Long userId = 1L;

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

		room = Room.builder()
			.hotel(hotel).name("Room1").capacity((byte)2).floor((byte)1)
			.bathtubFlag(false).viewType(ViewType.CITY)
			.price(BigDecimal.TEN).peakSeasonPrice(BigDecimal.TEN)
			.build();
		entityManager.persist(room);
	}

	@Test
	@DisplayName("예약 저장 및 ID로 조회")
	void testSaveAndFindById() {
		LocalDate checkIn = LocalDate.of(2025, 8, 1);
		LocalDate checkOut = LocalDate.of(2025, 8, 3);
		Reservation reservation = new Reservation(userId, room, checkIn, checkOut, 2, new BigDecimal("200.00"),
			ReservationStatus.CONFIRMED);

		Reservation savedReservation = reservationRepository.save(reservation);
		entityManager.flush();
		entityManager.clear();

		Optional<Reservation> foundReservationOpt = reservationRepository.findById(savedReservation.getReservationId());

		assertThat(foundReservationOpt).isPresent();
		Reservation foundReservation = foundReservationOpt.get();
		assertThat(foundReservation.getReservationId()).isEqualTo(savedReservation.getReservationId());
		assertThat(foundReservation.getUserId()).isEqualTo(userId);
		assertThat(foundReservation.getCheckInDate()).isEqualTo(checkIn);
		assertThat(foundReservation.getRoom().getRoomId()).isEqualTo(room.getRoomId());
	}
}