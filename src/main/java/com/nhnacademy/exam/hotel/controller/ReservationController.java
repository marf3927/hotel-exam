package com.nhnacademy.exam.hotel.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.exam.hotel.dto.CreateReservationRequest;
import com.nhnacademy.exam.hotel.dto.ReservationResponse;
import com.nhnacademy.exam.hotel.exception.ApplicationException;
import com.nhnacademy.exam.hotel.service.ReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
	private final ReservationService reservationService;

	@PostMapping("/{roomId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ReservationResponse reservation(@PathVariable Long roomId,
		@Valid @RequestBody CreateReservationRequest createRoomResponse,
		@AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails != null) {
			long userId = Long.parseLong(userDetails.getUsername());
			if (UserAuthzValidator.isValid(userId)) {
				return reservationService.createReservation(userId, roomId, createRoomResponse);
			}
		}
		throw new ApplicationException(HttpStatus.FORBIDDEN, "권한이 없습니다. ");
	}
}
