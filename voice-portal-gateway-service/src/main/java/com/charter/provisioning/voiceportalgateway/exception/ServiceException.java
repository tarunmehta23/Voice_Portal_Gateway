package com.charter.provisioning.voiceportalgateway.exception;

import org.springframework.http.HttpStatus;

public class ServiceException extends RuntimeException {

	private int httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();

	public int getHttpStatus() {
		return httpStatus;
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(int httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
