package com.samy.service.app.exception;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
	
	@JsonInclude(Include.NON_NULL)
	private String message;
	
	@JsonInclude(Include.NON_NULL)
	private List<ErrorDetail> details;
	
}