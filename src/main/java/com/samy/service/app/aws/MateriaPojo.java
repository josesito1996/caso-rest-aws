package com.samy.service.app.aws;

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
public class MateriaPojo {

	@JsonProperty("id_materia")
	private String idMateria;

	private String nombreMateria;

	private String color;

	private String icono;

	private Integer estado;

}
