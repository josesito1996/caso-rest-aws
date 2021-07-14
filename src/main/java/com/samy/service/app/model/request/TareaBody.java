package com.samy.service.app.model.request;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.external.EquipoDto;

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
public class TareaBody {

	@JsonProperty("id_tarea")
	private String idTarea;
	
	@NotNull
	@NotEmpty
	private String denominacion;

	@NotNull
	@JsonProperty("fecha_vencimiento")
	private LocalDate fechaVencimiento;
	
	@Valid
	private EquipoDto equipo;
	
	@Valid
	private List<ArchivoAdjunto> archivos;
	
	@NotNull
	private Boolean estado;
	
}
