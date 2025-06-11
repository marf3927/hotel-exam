package com.nhnacademy.exam.hotel.common;

import com.nhnacademy.exam.hotel.domain.ViewType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ViewTypeConverter implements AttributeConverter<ViewType, Integer> {
	@Override
	public Integer convertToDatabaseColumn(ViewType viewType) {
		return viewType.getDbValue();
	}

	@Override
	public ViewType convertToEntityAttribute(Integer dbValue) {
		return ViewType.fromDbValue(dbValue);
	}
}
