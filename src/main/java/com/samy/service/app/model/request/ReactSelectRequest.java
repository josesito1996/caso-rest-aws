package com.samy.service.app.model.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
@ToString
public class ReactSelectRequest {
	@NotEmpty(message = "no debe estar vacio")
	@NotNull(message = "no debe ser nulo")
	private String value;
	
	@NotEmpty(message = "no debe estar vacio")
	@NotNull(message = "no debe ser nulo")
	private String label;
	
	private String campoAux;
}
