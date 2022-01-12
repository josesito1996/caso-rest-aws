package com.samy.service.app.model.request;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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
public class CasoBody implements Serializable {

    private static final long serialVersionUID = 6616461711649616822L;

    @JsonProperty("id_caso")
    @Schema(title = "ID Caso", example = "5703e7f4-a1b9-4060-acd4-928a5dfd0423")
	private String idCaso;

	@NotNull
	@JsonProperty("descripcion_caso")
	@Schema(title = "Descripcion caso", example = "CASO DE PRUEBA V3")
	private String descripcionCaso;

	@NotNull
	@Schema(title = "Fecha Inicio", example = "2021-10-18")
	private LocalDate fechaInicio;

	@NotNull
	@NotEmpty
	@JsonProperty("orden_inspeccion")
	@Schema(title = "Orden Inspeccion", example = "323123")
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
	@Schema(title = "Materias", example = "[{\"id\":\"f9cb1e90-aed9-4183-9c6f-ac9ab68a990e\",\"nombre_materia\":\"Seguridad social\",\"color\":\"skyblue\",\"icono\":\"iconoSeguridadSocial\"}]")
	private List<String> materias;

	@Valid
	private List<ActuacionBody> actuacionBody;

	@NotNull
	@NotEmpty
	@Schema(title = "Usuario", example = "campos@gmail.com")
	private String usuario;

	@NotNull
	@Schema(title = "Estado", example = "true", description = "Valida si el caso esta activo o no")
	private Boolean estado;
	
	@Schema(title = "Intendencia", example = "Intendencia de ejemplo")
	private ReactSelectRequest intendencia;
	
	@Schema(title = "Empresa", example = "INDRA COMPANY")
	private String empresa;
	
	@JsonProperty("origen_inspeccion")
	private ReactSelectRequest origenInspeccion;
	
	@JsonProperty("trabajadores_involucrados")
	@Schema(title = "Trabajadores involucrados", example = "15")
	private Integer trabajadoresInvolucrados;
	
	@JsonProperty("sedes_involucradas")
	@Schema(title = "Sedes involucradas", example = "Sede Lima CEntro")
	private String sedesInvolucradas;
	
	@JsonProperty("resumen_caso")
	@Schema(title = "Resumen Caso", example = "Este es un resument para el caso CASO DE PRUEBA V3")
	private String resumenCaso;

}
