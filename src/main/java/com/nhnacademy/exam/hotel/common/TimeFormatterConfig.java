package com.nhnacademy.exam.hotel.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.nhnacademy.exam.hotel.formatter.DevTimeFormatter;
import com.nhnacademy.exam.hotel.formatter.RealTimeFormatter;
import com.nhnacademy.exam.hotel.formatter.TimeFormatter;

@Configuration
public class TimeFormatterConfig {
	@Bean
	@Profile("real")
	public TimeFormatter realTimeFormatter() {
		return new RealTimeFormatter();
	}

	@Bean
	@Profile("!real")
	public TimeFormatter devTimeFormatter() {
		return new DevTimeFormatter();
	}
}
