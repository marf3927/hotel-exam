package com.nhnacademy.exam.hotel.dto;

import java.math.BigDecimal;

import com.nhnacademy.exam.hotel.domain.ViewType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomRequest {
	@NotNull
	private String name;

	@Min(1)
	private byte capacity;

	@Min(0)
	@NotNull
	private byte floor;

	@NotNull
	private boolean hasBathtub;

	@NotNull
	private ViewType viewType;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false)
	private BigDecimal price;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false)
	private BigDecimal peakSeasonPrice;
}