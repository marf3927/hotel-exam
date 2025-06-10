package com.nhnacademy.exam.hotel.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.exam.hotel.common.SecurityConfig;
import com.nhnacademy.exam.hotel.domain.ReservationStatus;
import com.nhnacademy.exam.hotel.dto.CreateReservationRequest;
import com.nhnacademy.exam.hotel.dto.ReservationResponse;
import com.nhnacademy.exam.hotel.service.ReservationService;
import com.nhnacademy.exam.hotel.util.JwtUtil;

@WebMvcTest(ReservationController.class)
@Import(SecurityConfig.class)
class ReservationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ReservationService reservationService;

	@MockBean
	private JwtUtil jwtUtil;

	private MockedStatic<UserAuthzValidator> mockedUserAuthzValidator;

	@BeforeEach
	void setUp() {
		mockedUserAuthzValidator = mockStatic(UserAuthzValidator.class);
	}

	@AfterEach
	void tearDown() {
		mockedUserAuthzValidator.close();
	}

	@Test
	@DisplayName("예약 성공 - 유효한 요청 및 인증된 사용자")
	@WithMockUser(username = "100")
	void reservation_Success() throws Exception {
		Long roomId = 1L;
		Long userId = 100L;
		CreateReservationRequest request = new CreateReservationRequest();
		request.setCheckInDate(LocalDate.now().plusDays(10));
		request.setCheckOutDate(LocalDate.now().plusDays(12));
		request.setPax(2);

		ReservationResponse responseDto = ReservationResponse.builder()
			.reservationId(100L)
			.userId(userId)
			.roomId(roomId)
			.roomName("Standard Room")
			.checkInDate(request.getCheckInDate())
			.checkOutDate(request.getCheckOutDate())
			.pax(request.getPax())
			.totalPrice(new BigDecimal("300.00"))
			.status(ReservationStatus.CONFIRMED)
			.build();

		mockedUserAuthzValidator.when(() -> UserAuthzValidator.isValid(userId)).thenReturn(true);
		when(reservationService.createReservation(eq(userId), eq(roomId), any(CreateReservationRequest.class)))
			.thenReturn(responseDto);

		mockMvc.perform(post("/reservations/{roomId}", roomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.reservationId").value(responseDto.getReservationId()))
			.andExpect(jsonPath("$.userId").value(responseDto.getUserId()))
			.andDo(print());
	}

	@Test
	@DisplayName("예약 실패 - 인증되지 않은 사용자")
	void reservation_Fail_NoUserDetails() throws Exception {
		Long roomId = 1L;
		CreateReservationRequest request = new CreateReservationRequest();
		request.setCheckInDate(LocalDate.now().plusDays(10));
		request.setCheckOutDate(LocalDate.now().plusDays(12));
		request.setPax(2);

		mockMvc.perform(post("/reservations/{roomId}", roomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isForbidden())
			.andDo(print());
	}

	@Test
	@DisplayName("예약 실패 - 권한 없는 사용자 (UserAuthzValidator false)")
	@WithMockUser(username = "1")
	void reservation_Fail_UnauthorizedUser() throws Exception {
		Long roomId = 1L;
		Long userId = 1L;
		CreateReservationRequest request = new CreateReservationRequest();
		request.setCheckInDate(LocalDate.now().plusDays(10));
		request.setCheckOutDate(LocalDate.now().plusDays(12));
		request.setPax(2);

		mockedUserAuthzValidator.when(() -> UserAuthzValidator.isValid(userId)).thenReturn(false);

		mockMvc.perform(post("/reservations/{roomId}", roomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isForbidden())
			.andDo(print());
	}

	@Test
	@DisplayName("예약 실패 - 유효성 검사 실패 (체크아웃 < 체크인)")
	@WithMockUser(username = "1")
	void reservation_Fail_Validation_CheckOutBeforeCheckIn() throws Exception {
		Long roomId = 1L;
		Long userId = 1L;
		CreateReservationRequest request = new CreateReservationRequest();
		request.setCheckInDate(LocalDate.now().plusDays(12));
		request.setCheckOutDate(LocalDate.now().plusDays(10));
		request.setPax(2);

		mockedUserAuthzValidator.when(() -> UserAuthzValidator.isValid(userId)).thenReturn(true);

		mockMvc.perform(post("/reservations/{roomId}", roomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}

	@Test
	@DisplayName("예약 실패 - 유효성 검사 실패 (인원 수 < 1)")
	@WithMockUser(username = "1")
	void reservation_Fail_Validation_InvalidPax() throws Exception {
		Long roomId = 1L;
		Long userId = 1L;
		CreateReservationRequest request = new CreateReservationRequest();
		request.setCheckInDate(LocalDate.now().plusDays(10));
		request.setCheckOutDate(LocalDate.now().plusDays(12));
		request.setPax(0);

		mockedUserAuthzValidator.when(() -> UserAuthzValidator.isValid(userId)).thenReturn(true);

		mockMvc.perform(post("/reservations/{roomId}", roomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}

	@Test
	@DisplayName("예약 실패 - 유효성 검사 실패 (체크인 날짜 null)")
	@WithMockUser(username = "1")
	void reservation_Fail_Validation_NullCheckInDate() throws Exception {
		Long roomId = 1L;
		Long userId = 1L;
		CreateReservationRequest request = new CreateReservationRequest();
		request.setCheckInDate(null);
		request.setCheckOutDate(LocalDate.now().plusDays(12));
		request.setPax(2);

		mockedUserAuthzValidator.when(() -> UserAuthzValidator.isValid(userId)).thenReturn(true);

		mockMvc.perform(post("/reservations/{roomId}", roomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
}