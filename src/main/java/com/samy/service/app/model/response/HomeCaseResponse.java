package com.samy.service.app.model.response;

import java.io.Serializable;

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
public class HomeCaseResponse implements Serializable {
	
	private static final long serialVersionUID = -101992654946709540L;
	
	private String fechaInicio;
	
	private String etapaActuacion;
	
	private String riesgo;
	
	private String nombreCaso;
	
	private String ordenInspeccion;
	
	private String utltimaActuacion;
	
	private String tipoActuacion;
	
	private Integer totalTareas;
	
	private Integer tareasPendientes;
	
	private Integer aVencer;

}
