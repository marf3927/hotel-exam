package com.nhnacademy.exam.hotel.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.nhnacademy.exam.hotel.common.ViewTypeConverter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"hotel", "reservations", "dailyRoomProducts"})
@EqualsAndHashCode(of = "roomId")
@Entity
@Table(name = "rooms")
public class Room {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "room_id")
	private Long roomId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "hotel_id", nullable = false)
	private Hotel hotel;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "capacity", nullable = false)
	private Byte capacity;

	@Column(name = "floor", nullable = false)
	private Byte floor;

	@Column(name = "bathtub_flag", nullable = false)
	private boolean bathtubFlag;

	@Enumerated(EnumType.ORDINAL)
	@Convert(converter = ViewTypeConverter.class)
	@Column(name = "view_type", nullable = false)
	private ViewType viewType;

	@Column(name = "price", nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@Column(name = "peak_season_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal peakSeasonPrice;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Reservation> reservations = new ArrayList<>();

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<DailyRoomProduct> dailyRoomProducts = new ArrayList<>();

	@Builder
	public Room(Hotel hotel, String name, Byte capacity, Byte floor, boolean bathtubFlag, ViewType viewType,
		BigDecimal price, BigDecimal peakSeasonPrice
	) {
		this.hotel = hotel;
		this.name = name;
		this.capacity = capacity;
		this.floor = floor;
		this.bathtubFlag = bathtubFlag;
		this.viewType = viewType;
		this.price = price;
		this.peakSeasonPrice = peakSeasonPrice;
	}
}