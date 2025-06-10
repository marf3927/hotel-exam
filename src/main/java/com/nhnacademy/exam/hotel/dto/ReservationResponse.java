package com.nhnacademy.exam.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.nhnacademy.exam.hotel.domain.Reservation;
import com.nhnacademy.exam.hotel.domain.ReservationStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationResponse {
	private Long reservationId;
	private Long userId;
	private Long roomId;
	private String roomName;
	private LocalDate checkInDate;
	private LocalDate checkOutDate;
	private Integer pax;
	private BigDecimal totalPrice;
	private ReservationStatus status;

	@Builder
	private ReservationResponse(Long reservationId, Long userId, Long roomId, String roomName,
		LocalDate checkInDate, LocalDate checkOutDate, Integer pax,
		BigDecimal totalPrice, ReservationStatus status) {
		this.reservationId = reservationId;
		this.userId = userId;
		this.roomId = roomId;
		this.roomName = roomName;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
		this.pax = pax;
		this.totalPrice = totalPrice;
		this.status = status;
	}

	public static ReservationResponse fromEntity(Reservation reservation) {
		if (reservation == null) {
			return null;
		}
		return ReservationResponse.builder()
			.reservationId(reservation.getReservationId())
			.userId(reservation.getUserId())
			.roomId(reservation.getRoom().getRoomId())
			.roomName(reservation.getRoom().getName())
			.checkInDate(reservation.getCheckInDate())
			.checkOutDate(reservation.getCheckOutDate())
			.pax(reservation.getPax())
			.totalPrice(reservation.getTotalPrice())
			.status(reservation.getStatus())
			.build();
	}
}