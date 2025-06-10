package com.nhnacademy.exam.hotel.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

	private final HttpStatus status;

	public ApplicationException(HttpStatus status) {
		super();
		this.status = status;
	}

	public ApplicationException(HttpStatus status, Throwable cause) {
		super(cause);
		this.status = status;
	}

	public ApplicationException(HttpStatus HttpStatus, String customMessage) {
		super(customMessage);
		this.status = HttpStatus;
	}

	public ApplicationException(HttpStatus HttpStatus, String customMessage, Throwable cause) {
		super(customMessage, cause);
		this.status = HttpStatus;
	}

	public int getCode() {
		return status.value();
	}

	public HttpStatus getHttpStatus() {
		return status;
	}

}