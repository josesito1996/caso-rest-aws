package com.samy.service.app.restTemplate.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
public class UsuarioPojo implements Serializable {

	private static final long serialVersionUID = 4353553981840824765L;

	private String idUsuario;

	private String nombres;

	private String apellidos;

	private String nombreUsuario;

	private String correo;

	private String contrasena;
	
	private List<Colaborador> colaboradores;

	private Boolean terminos;

	private String empresa;

	private LocalDateTime fechaCreacion;

	private Boolean estado;

	private Boolean eliminado;

	private boolean validado;

	/**
	 * Tipo: usuario -> usuario normal colaborador -> colaborador
	 * 
	 */
	private String tipo;
}
