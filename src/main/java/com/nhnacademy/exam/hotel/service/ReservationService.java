package com.nhnacademy.exam.hotel.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.nhnacademy.exam.hotel.domain.DailyRoomProduct;
import com.nhnacademy.exam.hotel.domain.Reservation;
import com.nhnacademy.exam.hotel.domain.ReservationStatus;
import com.nhnacademy.exam.hotel.domain.Room;
import com.nhnacademy.exam.hotel.dto.CreateReservationRequest;
import com.nhnacademy.exam.hotel.dto.ReservationResponse;
import com.nhnacademy.exam.hotel.exception.ApplicationException;
import com.nhnacademy.exam.hotel.repository.DailyRoomProductsRepository;
import com.nhnacademy.exam.hotel.repository.ReservationRepository;
import com.nhnacademy.exam.hotel.repository.RoomRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final RoomRepository roomRepository;
	private final DailyRoomProductsRepository dailyRoomProductsRepository;

	@Transactional
	public ReservationResponse createReservation(Long userId, Long roomId, CreateReservationRequest request) {
		LocalDate checkInDate = request.getCheckInDate();
		LocalDate checkOutDate = request.getCheckOutDate();

		long existingReservations = reservationRepository
			.countOverlappingReservationsForUserQueryDsl(userId, checkInDate, checkOutDate);

		if (existingReservations >= 3) {
			throw new ApplicationException(HttpStatus.BAD_REQUEST,
				"한 사용자는 하루에 최대 3개의 객실만 예약할 수 있습니다.");
		}

		Room room = roomRepository.findById(roomId)
			.orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "방을 찾을 수 없습니다 : " + roomId));

		if (request.getPax() > room.getCapacity()) {
			throw new ApplicationException(HttpStatus.BAD_REQUEST, "예약 인원(" + request.getPax()
				+ ")이 수용 인원(" + room.getCapacity() + ")보다 많습니다.");
		}

		LocalDate stayEndDate = checkOutDate.minusDays(1);
		List<DailyRoomProduct> products = dailyRoomProductsRepository
			.findByRoom_roomIdAndStayDateBetween(roomId, checkInDate, stayEndDate);

		long requiredDays = checkOutDate.toEpochDay() - stayEndDate.toEpochDay();
		if (products.size() != requiredDays) {
			throw new IllegalStateException("요청하신 날짜에 해당 객실을 이용할 수 없습니다.");
		}

		BigDecimal totalPrice = BigDecimal.ZERO;
		for (DailyRoomProduct product : products) {
			if (product.getReservation() != null) {
				throw new ApplicationException(HttpStatus.BAD_REQUEST, "이미 예약된 날짜입니다 : " + product.getStayDate());
			}
			totalPrice = totalPrice.add(
				product.isPeakSeason() ? room.getPeakSeasonPrice() : room.getPrice()
			);
		}

		Reservation reservation = Reservation.builder()
			.userId(userId)
			.room(room)
			.checkInDate(checkInDate)
			.checkOutDate(checkOutDate)
			.pax(request.getPax())
			.totalPrice(totalPrice)
			.status(ReservationStatus.CONFIRMED)
			.build();

		Reservation savedReservation = reservationRepository.save(reservation);

		for (DailyRoomProduct product : products) {
			product.setReservation(savedReservation);
			dailyRoomProductsRepository.save(product);
		}

		return ReservationResponse.fromEntity(savedReservation);
	}
}
