package com.samy.service.app.model.request;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class ActuacionBody {
	@JsonProperty("id_actuacion")
	private String idActuacion;

	@NotNull
	@JsonProperty("fecha_actuacion")
	private LocalDate fechaActuacion;

	private String descripcion;

	@Valid
	@NotNull
	@Size(min = 1)
	private List<ReactSelectRequest> funcionarios;

	@JsonProperty("tipo_actuacion")
	@NotNull
	private ReactSelectRequest tipoActuacion;
	
	@JsonProperty("estado_caso")
	@NotNull
	private ReactSelectRequest estadoCaso;

	@Valid
	@NotNull
	private List<ArchivoBody> archivos;

	@Valid
	private List<TareaBody> tareas;

	@NotNull
	private ReactSelectRequest etapa;

}
