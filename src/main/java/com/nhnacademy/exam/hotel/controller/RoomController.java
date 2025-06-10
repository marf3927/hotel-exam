package com.nhnacademy.exam.hotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.exam.hotel.dto.CreateRoomResponse;
import com.nhnacademy.exam.hotel.dto.RoomRequest;
import com.nhnacademy.exam.hotel.dto.RoomResponse;
import com.nhnacademy.exam.hotel.service.ReservationService;
import com.nhnacademy.exam.hotel.service.RoomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
public class RoomController {
	private final RoomService roomService;
	private final ReservationService reservationService;

	@GetMapping("")
	public List<RoomResponse> getRoomsByHotelId(@PathVariable Long hotelId) {
		return roomService.getRoomsByHotelId(hotelId);
	}

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public CreateRoomResponse createRoom(
		@PathVariable Long hotelId,
		@Valid @RequestBody RoomRequest roomRequest) {
		return roomService.createRoom(hotelId, roomRequest);
	}

}