package com.nhnacademy.exam.hotel.dto;

import com.nhnacademy.exam.hotel.domain.Room;
import com.nhnacademy.exam.hotel.formatter.TimeFormatter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoomResponse {
	private String id;
	private String name;
	private int capacity;
	private int floor;
	private boolean hasBathtub;
	private String viewType;
	private String createdAt;

	public static RoomResponse fromEntity(Room room, TimeFormatter formatter) {
		return RoomResponse.builder()
			.id(String.valueOf(room.getRoomId()))
			.name(room.getName())
			.capacity(room.getCapacity())
			.floor(room.getFloor())
			.hasBathtub(room.isBathtubFlag())
			.viewType(room.getViewType().getParameter())
			.createdAt(formatter.convert(room.getCreatedAt()))
			.build();
	}
}
