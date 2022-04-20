package com.samy.service.app.restTemplate.model;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class ColaboradorPojo implements Serializable {

	private static final long serialVersionUID = 2803839972544976507L;
	
private String idColaborador;
	
	private String userName;
	
	private String nombres;

	private String apellidos;
	
	private String correo;
	
	private String password;

	private String empresa;
	
	private String idUsuario;
	
	private boolean estado;
	
	private boolean eliminado;
	
    private boolean validado;
	
	private LocalDateTime fechaRegistro;
	
	private boolean passwordChanged;
}
