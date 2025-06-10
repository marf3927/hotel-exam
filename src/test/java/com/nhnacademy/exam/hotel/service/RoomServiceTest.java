package com.nhnacademy.exam.hotel.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.nhnacademy.exam.hotel.domain.Hotel;
import com.nhnacademy.exam.hotel.domain.Room;
import com.nhnacademy.exam.hotel.domain.ViewType;
import com.nhnacademy.exam.hotel.dto.CreateRoomResponse;
import com.nhnacademy.exam.hotel.dto.RoomRequest;
import com.nhnacademy.exam.hotel.dto.RoomResponse;
import com.nhnacademy.exam.hotel.exception.ApplicationException;
import com.nhnacademy.exam.hotel.formatter.TimeFormatter;
import com.nhnacademy.exam.hotel.repository.HotelRepository;
import com.nhnacademy.exam.hotel.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

	@InjectMocks
	private RoomService roomService;

	@Mock
	private RoomRepository roomRepository;

	@Mock
	private HotelRepository hotelRepository;

	@Mock
	private TimeFormatter timeFormatter;

	private MockedStatic<ViewType> mockedViewType;
	private ViewType mockCityView;
	private Hotel hotel;
	private Long hotelId = 1L;

	@BeforeEach
	void setUp() {
		hotel = new Hotel(hotelId, "Test Hotel", LocalDateTime.now(), LocalDateTime.now());

		mockedViewType = mockStatic(ViewType.class);
		mockCityView = mock(ViewType.class);

		lenient().when(ViewType.fromParameter("city_view")).thenReturn(mockCityView);
		lenient().when(mockCityView.getParameter()).thenReturn("city_view");
		lenient().when(timeFormatter.convert(any(LocalDateTime.class))).thenReturn("2025-05-26 10:00:00");
	}

	@AfterEach
	void tearDown() {
		mockedViewType.close();
	}

	private Room createMockRoom(Long roomId, String name, Hotel hotel, ViewType viewType) {
		Room room = Room.builder()
			.hotel(hotel)
			.name(name)
			.capacity((byte)2)
			.floor((byte)10)
			.bathtubFlag(true)
			.viewType(viewType)
			.price(new BigDecimal("100.00"))
			.peakSeasonPrice(new BigDecimal("150.00"))
			.build();
		room.setRoomId(roomId);
		room.setCreatedAt(LocalDateTime.now());
		room.setUpdatedAt(LocalDateTime.now());
		return room;
	}

	@Test
	@DisplayName("호텔 ID로 객실 목록 조회 성공")
	void getRoomsByHotelId_Success() {
		Room room1 = createMockRoom(101L, "Room 101", hotel, mockCityView);
		Room room2 = createMockRoom(102L, "Room 102", hotel, mockCityView);
		List<Room> rooms = List.of(room1, room2);

		when(roomRepository.findAllByHotel_HotelId(hotelId)).thenReturn(rooms);

		List<RoomResponse> responses = roomService.getRoomsByHotelId(hotelId);

		assertNotNull(responses);
		assertEquals(2, responses.size());
		assertEquals("101", responses.get(0).getId());
		assertEquals("Room 101", responses.get(0).getName());
		assertEquals("city_view", responses.get(0).getViewType());
		assertEquals("2025-05-26 10:00:00", responses.get(0).getCreatedAt());
		verify(roomRepository, times(1)).findAllByHotel_HotelId(hotelId);
		verify(timeFormatter, times(2)).convert(any(LocalDateTime.class));
	}

	@Test
	@DisplayName("호텔 ID로 객실 목록 조회 - 결과 없음")
	void getRoomsByHotelId_Empty() {
		when(roomRepository.findAllByHotel_HotelId(hotelId)).thenReturn(Collections.emptyList());

		List<RoomResponse> responses = roomService.getRoomsByHotelId(hotelId);

		assertNotNull(responses);
		assertTrue(responses.isEmpty());
		verify(roomRepository, times(1)).findAllByHotel_HotelId(hotelId);
	}

	@Test
	@DisplayName("객실 생성 성공")
	void createRoom_Success() {
		RoomRequest request = new RoomRequest();
		request.setName("New Room");
		request.setCapacity((byte)3);
		request.setFloor((byte)15);
		request.setHasBathtub(false);
		request.setViewType("city_view");
		request.setPrice(new BigDecimal("250.00"));
		request.setPeakSeasonPrice(new BigDecimal("350.00"));

		when(hotelRepository.findByHotelId(hotelId)).thenReturn(Optional.of(hotel));
		when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
			Room savedRoom = invocation.getArgument(0);
			savedRoom.setRoomId(501L);
			return savedRoom;
		});

		CreateRoomResponse response = roomService.createRoom(hotelId, request);

		assertNotNull(response);
		assertEquals("501", response.getId());
		verify(hotelRepository, times(1)).findByHotelId(hotelId);
		verify(roomRepository, times(1)).save(any(Room.class));
		mockedViewType.verify(() -> ViewType.fromParameter("city_view"), times(1));
	}

	@Test
	@DisplayName("객실 생성 실패 - 호텔 없음")
	void createRoom_Fail_HotelNotFound() {
		RoomRequest request = new RoomRequest();
		request.setName("New Room");
		request.setViewType("city_view");

		when(hotelRepository.findByHotelId(hotelId)).thenReturn(Optional.empty());

		ApplicationException exception = assertThrows(ApplicationException.class, () ->
			roomService.createRoom(hotelId, request)
		);

		assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
		assertTrue(exception.getMessage().contains("호텔을 찾을 수 없습니다"));
		verify(roomRepository, never()).save(any());
	}

	@Test
	@DisplayName("객실 생성 실패 - 잘못된 ViewType")
	void createRoom_Fail_InvalidViewType() {
		RoomRequest request = new RoomRequest();
		request.setName("New Room");
		request.setViewType("invalid_view");

		when(hotelRepository.findByHotelId(hotelId)).thenReturn(Optional.of(hotel));
		mockedViewType.when(() -> ViewType.fromParameter("invalid_view"))
			.thenThrow(new IllegalArgumentException("Invalid view type"));

		assertThrows(IllegalArgumentException.class, () ->
			roomService.createRoom(hotelId, request)
		);

		verify(roomRepository, never()).save(any());
	}
}