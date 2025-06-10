package com.nhnacademy.exam.hotel.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.nhnacademy.exam.hotel.domain.DailyRoomProduct;
import com.nhnacademy.exam.hotel.domain.Hotel;
import com.nhnacademy.exam.hotel.domain.Reservation;
import com.nhnacademy.exam.hotel.domain.ReservationStatus;
import com.nhnacademy.exam.hotel.domain.Room;
import com.nhnacademy.exam.hotel.domain.ViewType;
import com.nhnacademy.exam.hotel.dto.CreateReservationRequest;
import com.nhnacademy.exam.hotel.dto.ReservationResponse;
import com.nhnacademy.exam.hotel.exception.ApplicationException;
import com.nhnacademy.exam.hotel.repository.DailyRoomProductsRepository;
import com.nhnacademy.exam.hotel.repository.ReservationRepository;
import com.nhnacademy.exam.hotel.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

	@InjectMocks
	private ReservationService reservationService;

	@Mock
	private ReservationRepository reservationRepository;

	@Mock
	private RoomRepository roomRepository;

	@Mock
	private DailyRoomProductsRepository dailyRoomProductsRepository;

	private Long userId;
	private Long roomId;
	private CreateReservationRequest request;
	private Room room;
	private LocalDate checkInDate;
	private LocalDate checkOutDate;
	private LocalDate stayEndDate;
	private Hotel hotel;

	private List<DailyRoomProduct> createMockProducts(Room room, LocalDate start, LocalDate end, boolean isReserved,
		boolean isPeak) {
		List<DailyRoomProduct> products = new ArrayList<>();
		LocalDate current = start;
		long idCounter = 1;
		Reservation mockReservation = isReserved ? Reservation.builder().build() : null;
		LocalDateTime now = LocalDateTime.now();

		while (current.isBefore(end)) {
			products.add(new DailyRoomProduct(
				idCounter++,
				room,
				current,
				isPeak,
				mockReservation,
				now,
				now
			));
			current = current.plusDays(1);
		}
		return products;
	}

	@BeforeEach
	void setUp() {
		userId = 1L;
		roomId = 101L;
		checkInDate = LocalDate.of(2025, 7, 1);
		checkOutDate = LocalDate.of(2025, 7, 2);
		stayEndDate = checkOutDate.minusDays(1);

		request = new CreateReservationRequest();
		request.setCheckInDate(checkInDate);
		request.setCheckOutDate(checkOutDate);
		request.setPax(2);

		hotel = new Hotel(1L, "Test Hotel", LocalDateTime.now(), LocalDateTime.now());

		room = Room.builder()
			.hotel(hotel)
			.name("Deluxe Room")
			.capacity((byte)3)
			.floor((byte)5)
			.bathtubFlag(true)
			.viewType(ViewType.CITY)
			.price(new BigDecimal("200.00"))
			.peakSeasonPrice(new BigDecimal("300.00"))
			.build();
		room.setRoomId(roomId);
	}

	@Test
	@DisplayName("예약 성공 (1박)")
	void createReservation_Success() {
		List<DailyRoomProduct> mockProducts = createMockProducts(room, checkInDate, checkOutDate, false, false);

		when(reservationRepository.countOverlappingReservationsForUserQueryDsl(userId, checkInDate,
			checkOutDate)).thenReturn(0L);
		when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
		when(dailyRoomProductsRepository.findByRoom_roomIdAndStayDateBetween(roomId, checkInDate,
			stayEndDate)).thenReturn(mockProducts);
		when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
			Reservation r = invocation.getArgument(0);
			return Reservation.builder()
				.userId(r.getUserId())
				.room(r.getRoom())
				.checkInDate(r.getCheckInDate())
				.checkOutDate(r.getCheckOutDate())
				.pax(r.getPax())
				.totalPrice(r.getTotalPrice())
				.status(r.getStatus())
				.build();
		});
		when(dailyRoomProductsRepository.save(any(DailyRoomProduct.class))).thenAnswer(
			invocation -> invocation.getArgument(0));

		ReservationResponse response = reservationService.createReservation(userId, roomId, request);

		assertNotNull(response);
		assertEquals(userId, response.getUserId());
		assertEquals(roomId, response.getRoomId());
		assertEquals(checkInDate, response.getCheckInDate());
		assertEquals(ReservationStatus.CONFIRMED, response.getStatus());
		assertEquals(new BigDecimal("200.00"), response.getTotalPrice());
		verify(reservationRepository, times(1)).save(any(Reservation.class));
		verify(dailyRoomProductsRepository, times(1)).save(any(DailyRoomProduct.class));
	}

	@Test
	@DisplayName("예약 실패 - 최대 예약 개수 초과")
	void createReservation_Fail_MaxReservations() {
		when(reservationRepository.countOverlappingReservationsForUserQueryDsl(userId, checkInDate,
			checkOutDate)).thenReturn(3L);

		ApplicationException exception = assertThrows(ApplicationException.class, () ->
			reservationService.createReservation(userId, roomId, request)
		);
		assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
		assertTrue(exception.getMessage().contains("최대 3개의 객실만 예약"));
		verify(roomRepository, never()).findById(anyLong());
	}

	@Test
	@DisplayName("예약 실패 - 방 없음")
	void createReservation_Fail_RoomNotFound() {
		when(reservationRepository.countOverlappingReservationsForUserQueryDsl(userId, checkInDate,
			checkOutDate)).thenReturn(0L);
		when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

		ApplicationException exception = assertThrows(ApplicationException.class, () ->
			reservationService.createReservation(userId, roomId, request)
		);
		assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
		assertTrue(exception.getMessage().contains("방을 찾을 수 없습니다"));
		verify(dailyRoomProductsRepository, never()).findByRoom_roomIdAndStayDateBetween(anyLong(), any(), any());
	}

	@Test
	@DisplayName("예약 실패 - 수용 인원 초과")
	void createReservation_Fail_PaxExceedsCapacity() {
		request.setPax(4);
		when(reservationRepository.countOverlappingReservationsForUserQueryDsl(userId, checkInDate,
			checkOutDate)).thenReturn(0L);
		when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

		ApplicationException exception = assertThrows(ApplicationException.class, () ->
			reservationService.createReservation(userId, roomId, request)
		);
		assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
		assertTrue(exception.getMessage().contains("예약 인원(4)이 수용 인원(3)보다 많습니다."));
		verify(dailyRoomProductsRepository, never()).findByRoom_roomIdAndStayDateBetween(anyLong(), any(), any());
	}

	@Test
	@DisplayName("예약 실패 - 날짜 불일치 (2박 요청, 서비스 버그로 실패)")
	void createReservation_Fail_DaysMismatch_DueToBug() {
		checkOutDate = LocalDate.of(2025, 7, 3);
		stayEndDate = checkOutDate.minusDays(1);
		request.setCheckOutDate(checkOutDate);

		List<DailyRoomProduct> mockProducts = createMockProducts(room, checkInDate, checkOutDate, false, false);
		assertEquals(2, mockProducts.size());

		when(reservationRepository.countOverlappingReservationsForUserQueryDsl(userId, checkInDate,
			checkOutDate)).thenReturn(0L);
		when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
		when(dailyRoomProductsRepository.findByRoom_roomIdAndStayDateBetween(roomId, checkInDate,
			stayEndDate)).thenReturn(mockProducts);

		IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
			reservationService.createReservation(userId, roomId, request)
		);
		assertTrue(exception.getMessage().contains("요청하신 날짜에 해당 객실을 이용할 수 없습니다."));
		verify(reservationRepository, never()).save(any());
	}

	@Test
	@DisplayName("예약 실패 - 이미 예약됨")
	void createReservation_Fail_AlreadyReserved() {
		List<DailyRoomProduct> mockProducts = createMockProducts(room, checkInDate, checkOutDate, true, false);

		when(reservationRepository.countOverlappingReservationsForUserQueryDsl(userId, checkInDate,
			checkOutDate)).thenReturn(0L);
		when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
		when(dailyRoomProductsRepository.findByRoom_roomIdAndStayDateBetween(roomId, checkInDate,
			stayEndDate)).thenReturn(mockProducts);

		ApplicationException exception = assertThrows(ApplicationException.class, () ->
			reservationService.createReservation(userId, roomId, request)
		);
		assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
		assertTrue(exception.getMessage().contains("이미 예약된 날짜입니다"));
		verify(reservationRepository, never()).save(any());
	}
}