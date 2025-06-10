package com.nhnacademy.exam.hotel.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(ApplicationException.class)
	protected ResponseEntity<?> handleApplicationException(ApplicationException ex) {
		HttpStatus httpStatus = ex.getHttpStatus();
		String message = ex.getMessage();

		if (message == null || message.isBlank()) {
			if (httpStatus.is4xxClientError())
				message = "잘못된 요청입니다.";
			if (httpStatus.is5xxServerError()) {
				message = "시스템에서 에러가 발생했습니다.";
			}
		}

		Map<String, String> response = new HashMap<>();
		response.put("errorMessage", message);

		return new ResponseEntity<>(response, httpStatus);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		String errorMessage = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.findFirst()
			.map(FieldError::getDefaultMessage)
			.orElse("입력값이 올바르지 않습니다.");

		Map<String, String> response = new HashMap<>();
		response.put("errorMessage", errorMessage);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	protected ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		log.error("HttpMessageNotReadableException", ex);
		Map<String, String> response = new HashMap<>();
		response.put("errorMessage", "잘못된 요청 형식입니다. 요청 본문의 데이터 타입이나 구조를 확인해주세요.");

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
}