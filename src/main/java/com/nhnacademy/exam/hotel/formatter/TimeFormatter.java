package com.nhnacademy.exam.hotel.formatter;

import java.time.LocalDateTime;

public interface TimeFormatter {
    String convert(LocalDateTime localDateTime);
}
