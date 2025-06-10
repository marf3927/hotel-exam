package com.nhnacademy.exam.hotel.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReservationRequest {
	@NotNull()
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate checkInDate;

	@NotNull()
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate checkOutDate;

	@NotNull()
	@Min(value = 1)
	private Integer pax;

	@AssertTrue()
	@JsonIgnore
	public boolean isCheckOutAfterCheckIn() {
		if (checkInDate == null || checkOutDate == null)
			return true;
		return checkOutDate.isAfter(checkInDate);
	}
}
