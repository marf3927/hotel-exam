package com.nhnacademy.exam.hotel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.exam.hotel.domain.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
	public Optional<Hotel> findByHotelId(long hotelId);
}
