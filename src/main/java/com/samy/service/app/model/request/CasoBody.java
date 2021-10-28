package com.samy.service.app.model.request;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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
public class CasoBody {

	@JsonProperty("id_caso")
	private String idCaso;

	@NotNull
	@JsonProperty("descripcion_caso")
	private String descripcionCaso;

	@NotNull
	private LocalDate fechaInicio;

	@NotNull
	@NotEmpty
	@JsonProperty("orden_inspeccion")
	private String ordenInspeccion;

	@Size(min = 1)
	@JsonProperty("inspector_trabajo")
	@Valid
	private List<ReactSelectRequest> inspectorTrabajo;

	@JsonProperty("inspector_auxiliar")
	@Valid
	private List<ReactSelectRequest> inspectorAuxiliar;

	@NotNull
	@Size(min = 1)
	private List<String> materias;

	@Valid
	private List<ActuacionBody> actuacionBody;

	@NotNull
	@NotEmpty
	private String usuario;

	@NotNull
	private Boolean estado;
	
	private String intendencia;
	
	private String empresa;
	
	@JsonProperty("origen_inspeccion")
	private ReactSelectRequest origenInspeccion;
	
	@JsonProperty("trabajadores_involucrados")
	private Integer trabajadoresInvolucrados;
	
	@JsonProperty("sedes_involucradas")
	private String sedesInvolucradas;
	
	@JsonProperty("resumen_caso")
	private String resumenCaso;

}
