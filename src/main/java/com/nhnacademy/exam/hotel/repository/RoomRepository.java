package com.nhnacademy.exam.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.exam.hotel.domain.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
	public List<Room> findAllByHotel_HotelId(long hotelId);
}
