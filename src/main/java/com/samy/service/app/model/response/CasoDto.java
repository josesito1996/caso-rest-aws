package com.samy.service.app.model.response;

import java.io.Serializable;
import java.time.LocalDate;
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
@Setter
@NoArgsConstructor
@ToString
public class CasoDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3902276766243277971L;

	private String idCaso;
	
	private String empresa;
	
	private String nombreCaso;
	
	private Integer trabajadoresAfectados;
	
	private String mesCaso;
	
	private LocalDate fechaRegistro;
	
	private Double multaPotencial;
	
	private Double provision;
	
	private Boolean estado;
	
	private String intendencia;
	
	private List<String> idMaterias;
	
}
