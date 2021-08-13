package com.samy.service.app.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.samy.service.app.aws.MateriaPojo;
import com.samy.service.app.external.SubMateriaDto;

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
public class DetailCaseResponse {
	private String idCaso;
	
	private String nombreCaso;
	
	@JsonInclude(Include.NON_NULL)
	private String descripcion;
	
	private String fechaCreacion;
	
	private String ordenInspeccion;
	
	private String tipoActuacion;
	
	private Integer cantidadDocumentos;
	
	private String funcionario;
	
	private List<MateriaPojo> materias;
	
	private List<SubMateriaDto> subMaterias;
}
