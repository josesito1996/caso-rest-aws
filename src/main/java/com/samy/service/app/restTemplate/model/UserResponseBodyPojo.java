package com.samy.service.app.restTemplate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserResponseBodyPojo {

	private String id;
	
	private String datosUsuario;
	
	private String nombreUsuario;
	
	private String tipo;
	
	private boolean claveCambiada;

}
