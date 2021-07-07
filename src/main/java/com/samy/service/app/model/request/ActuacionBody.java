package com.samy.service.app.model.request;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.samy.service.app.external.ArchivoAdjunto;

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

	@NotNull
	@NotEmpty
	private String descripcion;

	@Valid
	@Size(min = 1)
	private List<ReactSelectRequest> funcionarios;

	@JsonProperty("tipo_actuacion")
	private ReactSelectRequest tipoActuacion;

	@Valid
	private List<ArchivoAdjunto> archivos;

	@Valid
	private List<TareaBody> tareas;

	@Valid
	private ReactSelectRequest etapa;

}
