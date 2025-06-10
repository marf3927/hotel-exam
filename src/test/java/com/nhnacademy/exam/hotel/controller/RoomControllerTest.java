package com.nhnacademy.exam.hotel.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.exam.hotel.common.SecurityConfig;
import com.nhnacademy.exam.hotel.dto.CreateRoomResponse;
import com.nhnacademy.exam.hotel.dto.RoomRequest;
import com.nhnacademy.exam.hotel.dto.RoomResponse;
import com.nhnacademy.exam.hotel.service.ReservationService;
import com.nhnacademy.exam.hotel.service.RoomService;
import com.nhnacademy.exam.hotel.util.JwtUtil;

@WebMvcTest(RoomController.class)
@Import(SecurityConfig.class)
class RoomControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private RoomService roomService;

	@MockBean
	private ReservationService reservationService;

	@MockBean
	private JwtUtil jwtUtil;

	private RoomResponse roomResponse;
	private RoomRequest roomRequest;
	private CreateRoomResponse createRoomResponse;

	@BeforeEach
	void setUp() {
		roomResponse = RoomResponse.builder()
			.id("1")
			.name("Deluxe Room")
			.capacity(2)
			.floor(10)
			.hasBathtub(true)
			.viewType("OCEAN")
			.createdAt("2024-05-26 10:00:00")
			.build();

		roomRequest = new RoomRequest();
		roomRequest.setName("New Suite");
		roomRequest.setCapacity((byte)4);
		roomRequest.setFloor((byte)15);
		roomRequest.setHasBathtub(true);
		roomRequest.setViewType("CITY");
		roomRequest.setPrice(new BigDecimal("300.50"));
		roomRequest.setPeakSeasonPrice(new BigDecimal("450.75"));

		createRoomResponse = CreateRoomResponse.builder()
			.id(String.valueOf(2L))
			.build();
	}

	@Test
	@DisplayName("호텔 ID로 객실 목록 조회 성공")
	void getRoomsByHotelId_ShouldReturnRoomList() throws Exception {
		Long hotelId = 1L;
		List<RoomResponse> rooms = Collections.singletonList(roomResponse);
		given(roomService.getRoomsByHotelId(hotelId)).willReturn(rooms);

		mockMvc.perform(get("/hotels/{hotelId}/rooms", hotelId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].name", is(roomResponse.getName())));
	}

	@Test
	@DisplayName("새 객실 생성 성공")
	void createRoom_WithValidRequest_ShouldReturnCreatedResponse() throws Exception {
		Long hotelId = 1L;

		given(roomService.createRoom(anyLong(), org.mockito.ArgumentMatchers.any(RoomRequest.class)))
			.willReturn(createRoomResponse);

		mockMvc.perform(post("/hotels/{hotelId}/rooms", hotelId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(roomRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id", is(createRoomResponse.getId())));
	}

	@Test
	@DisplayName("새 객실 생성 시 유효성 검사 실패 (필수 필드 누락)")
	void createRoom_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
		Long hotelId = 1L;
		RoomRequest invalidRequest = new RoomRequest();
		mockMvc.perform(post("/hotels/{hotelId}/rooms", hotelId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
			.andExpect(status().isBadRequest());
	}
}