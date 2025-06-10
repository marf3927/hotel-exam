package com.nhnacademy.exam.hotel.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
import com.nhnacademy.exam.hotel.domain.Room;
import com.nhnacademy.exam.hotel.domain.ViewType;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@DataJpaTest
class RoomRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private RoomRepository roomRepository;

	private Hotel hotel1;
	private Hotel hotel2;

	@TestConfiguration
	static class TestConfig {
		@Bean
		public JPAQueryFactory jpaQueryFactory(EntityManager em) {
			return new JPAQueryFactory(em);
		}
	}

	@BeforeEach
	void setUp() {
		hotel1 = new Hotel(1L, "Test Hotel 1", LocalDateTime.now(), LocalDateTime.now());
		hotel2 = new Hotel(2L, "Test Hotel 2", LocalDateTime.now(), LocalDateTime.now());
		entityManager.persist(hotel1);
		entityManager.persist(hotel2);

		Room room1_1 = Room.builder()
			.hotel(hotel1)
			.name("101")
			.capacity((byte)2)
			.floor((byte)1)
			.bathtubFlag(true)
			.viewType(ViewType.CITY)
			.price(BigDecimal.ONE)
			.peakSeasonPrice(BigDecimal.TEN)
			.build();
		Room room1_2 = Room.builder()
			.hotel(hotel1)
			.name("102")
			.capacity((byte)4)
			.floor((byte)1)
			.bathtubFlag(false)
			.viewType(ViewType.OCEAN)
			.price(BigDecimal.ONE)
			.peakSeasonPrice(BigDecimal.TEN)
			.build();
		Room room2_1 = Room.builder()
			.hotel(hotel2)
			.name("A")
			.capacity((byte)3)
			.floor((byte)5)
			.bathtubFlag(true)
			.viewType(ViewType.MOUNTAIN)
			.price(BigDecimal.ONE)
			.peakSeasonPrice(BigDecimal.TEN)
			.build();

		entityManager.persist(room1_1);
		entityManager.persist(room1_2);
		entityManager.persist(room2_1);
		entityManager.flush();
	}

	@Test
	@DisplayName("호텔 ID로 객실 목록 조회")
	void findAllByHotel_HotelId() {
		List<Room> rooms = roomRepository.findAllByHotel_HotelId(hotel1.getHotelId());

		assertThat(rooms).isNotNull();
		assertThat(rooms).hasSize(2);
		assertThat(rooms).extracting(Room::getName).containsExactlyInAnyOrder("101", "102");
	}

	@Test
	@DisplayName("호텔 ID로 객실 목록 조회 - 결과 없음")
	void findAllByHotel_HotelId_NotFound() {
		List<Room> rooms = roomRepository.findAllByHotel_HotelId(999L);

		assertThat(rooms).isNotNull();
		assertThat(rooms).isEmpty();
	}

	@Test
	@DisplayName("객실 저장 및 ID로 조회")
	void testSaveAndFindById() {
		Room newRoom = Room.builder()
			.hotel(hotel1)
			.name("New")
			.capacity((byte)1)
			.floor((byte)3)
			.bathtubFlag(false)
			.viewType(ViewType.CITY)
			.price(BigDecimal.ONE)
			.peakSeasonPrice(BigDecimal.TEN)
			.build();
		Room savedRoom = roomRepository.save(newRoom);
		entityManager.flush();

		assertThat(savedRoom.getRoomId()).isNotNull();
		Optional<Room> foundRoomOpt = roomRepository.findById(savedRoom.getRoomId());
		assertThat(foundRoomOpt).isPresent();
		assertThat(foundRoomOpt.get().getName()).isEqualTo("New");
	}
}