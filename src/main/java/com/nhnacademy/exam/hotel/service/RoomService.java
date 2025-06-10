package com.nhnacademy.exam.hotel.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.nhnacademy.exam.hotel.domain.Hotel;
import com.nhnacademy.exam.hotel.domain.Room;
import com.nhnacademy.exam.hotel.domain.ViewType;
import com.nhnacademy.exam.hotel.dto.CreateRoomResponse;
import com.nhnacademy.exam.hotel.dto.RoomRequest;
import com.nhnacademy.exam.hotel.dto.RoomResponse;
import com.nhnacademy.exam.hotel.exception.ApplicationException;
import com.nhnacademy.exam.hotel.formatter.TimeFormatter;
import com.nhnacademy.exam.hotel.repository.HotelRepository;
import com.nhnacademy.exam.hotel.repository.RoomRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {

	private final RoomRepository roomRepository;
	private final HotelRepository hotelRepository;
	private final TimeFormatter timeFormatter;

	// RoomResponse 클래스는 Room Entity 객체를 클라이언트에게 응답하기 위한 DTO 입니다.
	// 객실 정보 조회 API 명세서의 Response 양식을 보시고 적절한 형태로 RoomResponse 클래스를 만들어주세요.
	// JSON message 의 viewType 속성은 미리 제공한 ViewType enum의 parameter 값을 사용해야 합니다.
	// Hint. javax.persistence.AttributeConverter 인터페이스와 @Convert 애너테이션을 사용하면 됩니다.

	@Transactional
	public List<RoomResponse> getRoomsByHotelId(Long hotelId) {
		List<Room> rooms = roomRepository.findAllByHotel_HotelId(hotelId);
		return rooms.stream().map(room -> RoomResponse.fromEntity(room, timeFormatter))
			.toList();
	}

	@Transactional
	public CreateRoomResponse createRoom(Long hotelId, RoomRequest roomRequest) {
		Hotel hotel = hotelRepository.findByHotelId(hotelId)
			.orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "호텔을 찾을 수 없습니다 : " + hotelId));

		Room room = Room.builder()
			.hotel(hotel)
			.name(roomRequest.getName())
			.capacity(roomRequest.getCapacity())
			.floor(roomRequest.getFloor())
			.bathtubFlag(roomRequest.isHasBathtub())
			.viewType(ViewType.fromParameter(roomRequest.getViewType()))
			.price(roomRequest.getPrice())
			.peakSeasonPrice(roomRequest.getPeakSeasonPrice())
			.build();
		roomRepository.save(room);

		return CreateRoomResponse.builder().id(String.valueOf(room.getRoomId())).build();
	}
}
