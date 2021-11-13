package com.samy.service.app.model.request;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class ActuacionBody implements Serializable {

    private static final long serialVersionUID = -5890965693608456213L;

    @JsonProperty("id_actuacion")
    @Schema(title = "Id Actuacion", example = "32a18b9b-0f5d-4ec1-82b9-33eb573cd362")
	private String idActuacion;

	@NotNull
	@JsonProperty("fecha_actuacion")
	@Schema(title = "Fecha Actuacion", example = "2021-10-22", type = "LocalDate", required = true)
	private LocalDate fechaActuacion;

	@Schema(title = "Descripcion", example = "Descripcon de Actuacion")
	private String descripcion;

	@Valid
	@NotNull
	@Size(min = 1)
	@Schema(minLength = 1)
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
