package com.samy.service.app.model.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@ToString
public class CasosConRiesgoResponse implements Serializable {

	private static final long serialVersionUID = 7303071594238267378L;

	private String nombreCaso;
	
	private String multaPotencial;
	
	private String provisiones;
	
	private String color;
	
}
