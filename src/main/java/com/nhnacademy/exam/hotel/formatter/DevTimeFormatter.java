package com.nhnacademy.exam.hotel.formatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DevTimeFormatter implements TimeFormatter {
	public String convert(LocalDateTime localDateTime) {
		return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
}
