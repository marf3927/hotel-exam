package com.nhnacademy.exam.hotel.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.nhnacademy.exam.hotel.domain.Hotel;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@DataJpaTest
class HotelRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private HotelRepository hotelRepository;

	@TestConfiguration
	static class TestConfig {
		@Bean
		public JPAQueryFactory jpaQueryFactory(EntityManager em) {
			return new JPAQueryFactory(em);
		}
	}

	@Test
	@DisplayName("호텔 저장 및 ID로 조회")
	void testSaveAndFindByHotelId() {
		Hotel hotel = new Hotel(1L, "Grand Hotel", LocalDateTime.now(), LocalDateTime.now());
		Hotel savedHotel = hotelRepository.save(hotel);
		entityManager.flush();
		entityManager.clear();

		Optional<Hotel> foundHotelOpt = hotelRepository.findByHotelId(savedHotel.getHotelId());

		assertThat(foundHotelOpt).isPresent();
		Hotel foundHotel = foundHotelOpt.get();
		assertThat(foundHotel.getHotelId()).isEqualTo(savedHotel.getHotelId());
		assertThat(foundHotel.getName()).isEqualTo("Grand Hotel");
	}

	@Test
	@DisplayName("존재하지 않는 호텔 ID로 조회")
	void testFindByHotelId_NotFound() {
		Optional<Hotel> foundHotelOpt = hotelRepository.findByHotelId(999L);

		assertThat(foundHotelOpt).isNotPresent();
	}
}