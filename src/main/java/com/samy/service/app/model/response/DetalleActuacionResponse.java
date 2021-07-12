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
public class DetalleActuacionResponse implements Serializable {
	
	private static final long serialVersionUID = 2662422364406203058L;

	private String idTarea;
	
	private String nombreTarea;

	private Integer cantidadDocumentos;

	private String equipo;

	private String fechaRegistro;

	private String fechaVencimiento;

	private Boolean estado;

}
