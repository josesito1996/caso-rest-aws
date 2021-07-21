package com.samy.service.app.model.response;

import java.io.Serializable;
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
public class ActuacionResponse implements Serializable {

	private static final long serialVersionUID = -1054177119541119588L;

	private Boolean isOpen;
	
	private String idActuacion;
	
	private String dia;
	
	private String mes;
	
	private String tipo;
	
	private String etapa;
	
	private String descripcionActuacion;
	
	private Integer totalDocumentosActuacion;
	
	private Integer totalDocumentosTareas;
	
	private Integer totalTareasRealizadas;
	
	private List<DetalleActuacionResponse> detalles;
	
}
