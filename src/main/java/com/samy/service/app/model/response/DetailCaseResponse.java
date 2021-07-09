package com.samy.service.app.model.response;

import java.util.List;

import com.samy.service.app.aws.MateriaPojo;

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
	private String descripcion;
	private String fechaCreacion;
	private String ordenInspeccion;
	private String tipoActuacion;
	private Integer cantidadDocumentos;
	private String funcionario;
	private List<MateriaPojo> materias;
}
