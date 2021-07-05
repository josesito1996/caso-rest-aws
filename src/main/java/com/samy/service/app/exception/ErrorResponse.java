package com.samy.service.app.exception;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ErrorResponse {

	private Integer statusCode;
	
	private LocalDateTime timeStamp;
	
	private String message;
	
	private List<ErrorDetail> details;
	
}