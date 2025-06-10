package com.nhnacademy.exam.hotel.controller;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserAuthzValidatorTest {

	@Test
	@DisplayName("유효한 사용자 ID (100) 검증 성공")
	void isValid_WithValidUserId_ShouldReturnTrue() {
		Long userId = 100L;

		Boolean result = UserAuthzValidator.isValid(userId);

		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("유효하지 않은 사용자 ID (99) 검증 실패")
	void isValid_WithInvalidUserId_ShouldReturnFalse() {
		Long userId = 999L;

		Boolean result = UserAuthzValidator.isValid(userId);

		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("Null 사용자 ID 검증 실패")
	void isValid_WithNullUserId_ShouldReturnFalse() {
		Long userId = null;

		Boolean result = UserAuthzValidator.isValid(userId);

		assertThat(result).isFalse();
	}
}