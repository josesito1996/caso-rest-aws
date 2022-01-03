package com.samy.service.app.aws;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

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

	private static final long serialVersionUID = -1197954867654796037L;

	@JsonProperty("id_usuario")
	private String idUsuario;
	
	private String nombres;

	private String apellidos;

	private String nombreUsuario;
	
	private String correo;

	private String contrasena;

	private Boolean terminos;

	private String empresa;
	
	private LocalDateTime fechaCreacion;
	
	private Boolean estado;
	
	private Boolean eliminado;
	
    private Boolean validado;
	
}
