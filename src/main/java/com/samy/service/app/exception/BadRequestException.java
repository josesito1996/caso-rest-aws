package com.samy.service.app.exception;

public class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = 8835406841348110836L;

	
	public BadRequestException(String msg) {
		super(msg);
	}
}
