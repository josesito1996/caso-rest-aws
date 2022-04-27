package com.samy.service.app.model.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.samy.service.samiprimary.service.model.ReactSelect;

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
	
	private Map<String, Object> estadoCaso;
	
	private String etapa;
	
	private String fechaCreacion;
	
	private String ordenInspeccion;
	
	private String tipoActuacion;
	
	private Integer cantidadDocumentos;
	
	private List<FuncionarioResponse> funcionarios;
	
	@JsonInclude(Include.NON_NULL)
	private Integer trabajadoresInvolucrados;
	
	@JsonInclude(Include.NON_NULL)
	private ReactSelect origen;
	
	@JsonInclude(Include.NON_NULL)
	private Double sumaMultaPotencial;
	
	@JsonInclude(Include.NON_NULL)
	private Double sumaProvision;
	
	@JsonInclude(Include.NON_NULL)
	private ReactSelect riesgo;
	
	private List<MateriaResponse> materiasResponse;
	
	private Integer totalMaterias;
	
	private Integer totalSubMaterias;
	
	private String region;
	
	private String userName;
	
	private String datosUsuario;
	
	private boolean statusCase;
}
