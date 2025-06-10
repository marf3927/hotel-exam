package com.nhnacademy.exam.hotel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.exam.hotel.domain.DailyRoomProduct;

public interface DailyRoomProductsRepository
	extends JpaRepository<DailyRoomProduct, Long> {
	List<DailyRoomProduct> findByRoom_roomIdAndStayDateBetween(Long roomId, LocalDate startDate, LocalDate endDate);
}
