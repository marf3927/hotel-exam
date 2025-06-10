package com.nhnacademy.exam.hotel.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.nhnacademy.exam.hotel.domain.DailyRoomProduct;
import com.nhnacademy.exam.hotel.domain.Hotel;
import com.nhnacademy.exam.hotel.domain.Room;
import com.nhnacademy.exam.hotel.domain.ViewType;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@DataJpaTest
class DailyRoomProductsRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private DailyRoomProductsRepository dailyRoomProductsRepository;

	private Room room;
	private LocalDate date1;
	private LocalDate date2;
	private LocalDate date3;

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
			.hotel(hotel)
			.name("Test Room")
			.capacity((byte)2)
			.floor((byte)1)
			.bathtubFlag(true)
			.viewType(ViewType.CITY)
			.price(new BigDecimal("100.00"))
			.peakSeasonPrice(new BigDecimal("150.00"))
			.build();
		entityManager.persist(room);

		date1 = LocalDate.of(2025, 7, 10);
		date2 = LocalDate.of(2025, 7, 11);
		date3 = LocalDate.of(2025, 7, 12);

		DailyRoomProduct p1 = new DailyRoomProduct(null, room, date1, false, null, LocalDateTime.now(),
			LocalDateTime.now());
		DailyRoomProduct p2 = new DailyRoomProduct(null, room, date2, false, null, LocalDateTime.now(),
			LocalDateTime.now());
		DailyRoomProduct p3 = new DailyRoomProduct(null, room, date3, true, null, LocalDateTime.now(),
			LocalDateTime.now());

		entityManager.persist(p1);
		entityManager.persist(p2);
		entityManager.persist(p3);
		entityManager.flush();
	}

	@Test
	@DisplayName("특정 방과 날짜 범위로 DailyRoomProduct 조회")
	void findByRoom_roomIdAndStayDateBetween() {
		List<DailyRoomProduct> products = dailyRoomProductsRepository.findByRoom_roomIdAndStayDateBetween(
			room.getRoomId(), date1, date2);

		assertThat(products).isNotNull();
		assertThat(products).hasSize(2);
		assertThat(products).extracting(DailyRoomProduct::getStayDate).containsExactlyInAnyOrder(date1, date2);
	}

	@Test
	@DisplayName("특정 방과 날짜 범위로 조회 - 결과 없음")
	void findByRoom_roomIdAndStayDateBetween_NotFound() {
		List<DailyRoomProduct> products = dailyRoomProductsRepository.findByRoom_roomIdAndStayDateBetween(
			room.getRoomId(), date3.plusDays(1), date3.plusDays(2));

		assertThat(products).isNotNull();
		assertThat(products).isEmpty();
	}
}