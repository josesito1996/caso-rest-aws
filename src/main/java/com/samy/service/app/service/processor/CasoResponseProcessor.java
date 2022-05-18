package com.samy.service.app.service.processor;

import static com.samy.service.app.service.impl.ServiceUtils.cantidadDocumentos;
import static com.samy.service.app.service.impl.ServiceUtils.estaVencido;
import static com.samy.service.app.service.impl.ServiceUtils.etapaActuacion;
import static com.samy.service.app.service.impl.ServiceUtils.fechaActuacion;
import static com.samy.service.app.service.impl.ServiceUtils.nroOrdenEtapaActuacion;
import static com.samy.service.app.service.impl.ServiceUtils.siguienteVencimientoDelCaso;
import static com.samy.service.app.service.impl.ServiceUtils.tipoActuacion;
import static com.samy.service.app.util.Utils.fechaFormateada;
import static com.samy.service.app.util.Utils.formatMoney;
import static com.samy.service.app.util.Utils.getPorcentaje;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.samy.service.app.aws.ExternalDbAws;
import com.samy.service.app.external.EstadoCasoDto;
import com.samy.service.app.external.FuncionarioDto;
import com.samy.service.app.external.MateriaDto;
import com.samy.service.app.model.Actuacion;
import com.samy.service.app.model.Caso;
import com.samy.service.app.model.DynamoBodyGenerico;
import com.samy.service.app.model.response.DetailCaseResponse;
import com.samy.service.app.model.response.FuncionarioResponse;
import com.samy.service.app.model.response.HomeCaseResponse;
import com.samy.service.app.model.response.MateriaResponse;
import com.samy.service.app.model.response.SubMateriaResponse;
import com.samy.service.app.util.Contants;
import com.samy.service.samiprimary.service.model.AnalisisRiesgo;
import com.samy.service.samiprimary.service.model.InfraccionItem;
import com.samy.service.samiprimary.service.model.InspectorResponse;
import com.samy.service.samiprimary.service.model.ReactSelect;
import com.samy.service.samiusers.service.model.UserResponseBody;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class CasoResponseProcessor {

	private ExternalDbAws externalAws;
	
	public HomeCaseResponse transformToHomeCase(Caso caso) {
		String nombreEmpresa = caso.getEmpresas().stream().map(DynamoBodyGenerico::getLabel).findFirst().orElse("");
		AnalisisRiesgo analisis = externalAws.tableInfraccion(caso.getId()).stream()
				.reduce((first, second) -> second).orElse(new AnalisisRiesgo());
		log.info("AnalisisPojo {}", analisis);
		String nivelRiesgo = analisis.getIdAnalisis() != null ? analisis.getNivelRiesgo().getLabel() : "";
		String colorRiesgo = Contants.mapRiesgo.get(nivelRiesgo);
		String siguienteVencimiento = siguienteVencimientoDelCaso(caso.getActuaciones());
		return HomeCaseResponse.builder().idCaso(caso.getId()).idCaso(caso.getId()).nombreEmpresa(nombreEmpresa)
				.nombreCaso(caso.getDescripcionCaso()).nroOrdenInspeccion(caso.getOrdenInspeccion())
				.utltimaActuacion(fechaActuacion(caso.getActuaciones()))
				.tipoActuacion(tipoActuacion(caso.getActuaciones()))
				.nroEtapa(nroOrdenEtapaActuacion(caso.getActuaciones()))
				.etapaActuacion(etapaActuacion(caso.getActuaciones())).riesgo(nivelRiesgo).colorRiesgo(colorRiesgo)
				.siguienteVencimiento(siguienteVencimiento).estaVencido(estaVencido(siguienteVencimiento)).build();
	}
	
	public DetailCaseResponse transformFromCaso(Caso caso) {
		UserResponseBody usuario = externalAws.getUser(caso.getUsuario());
		List<String> idMaterias = caso.getMaterias().stream().map(MateriaDto::getId).collect(Collectors.toList());
		AnalisisRiesgo mapInfraccion = externalAws.tableInfraccion(caso.getId()).stream()
				.sorted(Comparator.comparing(AnalisisRiesgo::getFechaRegistro)).reduce((first, second) -> second)
				.orElse(AnalisisRiesgo.builder().infracciones(new ArrayList<>()).build());
		Integer totalMaterias = 0;
		Integer totalSubMaterias = 0;
		// List<MateriaResponse> materias =
		// materiasResponseBuild(transformToDto(caso.getMaterias()),
		// subMateriasBuild(caso.getMaterias()));
		List<MateriaResponse> materias = materias(mapInfraccion.getInfracciones());
		List<String> idMateriasV2 = materias.stream().map(MateriaResponse::getIdMateria).collect(Collectors.toList());
		List<String> unionId = Stream.concat(idMaterias.stream(), idMateriasV2.stream()).distinct()
				.collect(Collectors.toList());
		List<MateriaResponse> materiasNew = new ArrayList<>();
		for (String id : unionId) {
			MateriaResponse materiaResponse = materias.stream().filter(mat -> mat.getIdMateria().equals(id)).findFirst()
					.orElse(new MateriaResponse());
			if (materiaResponse.getIdMateria() != null) {
				materiasNew.add(materiaResponse);
			} else {
				com.samy.service.samiprimary.service.model.MateriaResponse matResponse = externalAws.getTable(id);
				materiasNew.add(MateriaResponse.builder().idMateria(matResponse.getIdMateria())
						.nombreMateria(matResponse.getNombreMateria()).color(matResponse.getColor())
						.icono(matResponse.getIcono()).subMaterias(new ArrayList<>()).build());
			}
		}
		totalMaterias = materiasNew.size();
		for (MateriaResponse response : materiasNew) {
			totalSubMaterias = totalSubMaterias + response.getSubMaterias().size();
		}
		List<Actuacion> actuaciones = caso.getActuaciones();
		int sizeActuaciones = actuaciones.size();
		String etapaActuacion = actuaciones.isEmpty() ? ""
				: actuaciones.get(sizeActuaciones - 1).getEtapa().getNombreEtapa();
		Map<String, Object> mapEstado = new HashMap<String, Object>();
		EstadoCasoDto estado = actuaciones.isEmpty() ? EstadoCasoDto.builder().build()
				: actuaciones.get(sizeActuaciones - 1).getEstadoCaso();
		mapEstado.put("numero", estado.getOrden());
		mapEstado.put("estadoCaso", estado.getNombreEstado());

		return DetailCaseResponse.builder().idCaso(caso.getId()).nombreCaso(caso.getDescripcionCaso())
				.descripcion(caso.getDescripcionAdicional()).fechaCreacion(fechaFormateada(caso.getFechaInicio()))
				.ordenInspeccion(caso.getOrdenInspeccion()).tipoActuacion(tipoActuacion(caso.getActuaciones()))
				.cantidadDocumentos(cantidadDocumentos(caso.getActuaciones()))
				.funcionarios(funcionariosResponseList(caso))
				.trabajadoresInvolucrados(mapInfraccion.getCantidadInvolucrados())
				.sumaMultaPotencial(mapInfraccion.getInfracciones().stream()
						.mapToDouble(InfraccionItem::getMultaPotencial).sum())
				.sumaProvision(mapInfraccion.getSumaProvision()).riesgo(mapInfraccion.getNivelRiesgo())
				.origen(mapInfraccion.getOrigenCaso()).materiasResponse(materiasNew).totalMaterias(totalMaterias)
				.totalSubMaterias(totalSubMaterias).etapa(etapaActuacion).estadoCaso(mapEstado)
				.region(caso.getIntendencias().stream().findFirst().orElse(new DynamoBodyGenerico()).getLabel())
				.userName(caso.getUsuario()).datosUsuario(usuario.getDatosUsuario()).statusCase(caso.getEstadoCaso())
				.build();
	}
	
	private List<MateriaResponse> materias(List<InfraccionItem> items) {
		if (items == null) {
			return new ArrayList<>();
		}
		List<MateriaResponse> materias = new ArrayList<>();
		for (InfraccionItem item : items) {
			com.samy.service.samiprimary.service.model.MateriaResponse matResponse = externalAws
					.getTable(item.getMateria().getValue());
			ReactSelect materia = item.getMateria();
			ReactSelect subMateria = item.getSubMaterias();
			materias.add(MateriaResponse
					.builder().idMateria(materia.getValue()).color(matResponse.getColor()).icono(matResponse.getIcono())
					.nombreMateria(materia.getLabel()).subMaterias(Arrays.asList(SubMateriaResponse.builder()
							.idSubMateria(subMateria.getValue()).nombreSubMAteria(subMateria.getLabel()).build()))
					.build());
		}
		List<MateriaResponse> newMaterias = new ArrayList<>();
		for (MateriaResponse mat : materias) {
			List<SubMateriaResponse> subMaterias = new ArrayList<>();
			if (!validateMat(newMaterias, mat.getIdMateria())) {
				for (SubMateriaResponse subMateria : mat.getSubMaterias()) {
					if (!validateSub(subMaterias, subMateria.getIdSubMateria())) {
						subMaterias.add(subMateria);
					}
				}
				mat.setSubMaterias(subMaterias);
				newMaterias.add(mat);
			} else {
				int index = 0;
				for (int i = 0; i <= newMaterias.size(); i++) {
					if (newMaterias.get(i).getIdMateria().equals(mat.getIdMateria())) {
						index = i;
						break;
					}
				}
				List<SubMateriaResponse> lisAux = newMaterias.get(index).getSubMaterias();
				for (SubMateriaResponse subMateria : mat.getSubMaterias()) {
					if (!validateSub(lisAux, subMateria.getIdSubMateria())) {
						lisAux.add(subMateria);
					}
				}
				mat.setSubMaterias(lisAux);
				newMaterias.set(index, mat);
			}
		}
		return newMaterias;
	}
	private List<FuncionarioResponse> funcionariosResponseList(Caso caso) {
		List<Actuacion> actuaciones = caso.getActuaciones();
		List<FuncionarioResponse> funcionarios = new ArrayList<>();
		actuaciones.sort(Comparator.comparing(Actuacion::getFechaRegistro).reversed());
		for (Actuacion actuacion : actuaciones) {
			for (FuncionarioDto func : actuacion.getFuncionario()) {

				List<FuncionarioResponse> funcis = funcionarios.stream()
						.filter(item -> item.getIdFuncionario().equals(func.getId())).collect(Collectors.toList());
				log.info("Funcis {}", funcionarios);
				InspectorResponse inspectorResponse = externalAws.tableInspector(func.getId());
				if (inspectorResponse.getId() == null) {
					inspectorResponse.setId(func.getId());
					inspectorResponse.setNombresApellidos(func.getDatosFuncionario());
				}
				if (!funcis.isEmpty()) {
					funcionarios.add(FuncionarioResponse.builder().idFuncionario(inspectorResponse.getId())
							.nombreFuncionario(inspectorResponse.getNombresApellidos()).cargo(inspectorResponse.getCargo())
							.etapaActuacion(actuacion.getEtapa().getNombreEtapa()).build());
				} else {
					funcionarios.add(FuncionarioResponse.builder().idFuncionario(func.getId())
							.nombreFuncionario(inspectorResponse.getNombresApellidos()).cargo(inspectorResponse.getCargo())
							.etapaActuacion(actuacion.getEtapa().getNombreEtapa()).build());
				}
			}
		}
		return funcionarios.stream().distinct().collect(Collectors.toList());
	}
	
	private boolean validateMat(List<MateriaResponse> materias, String idMateria) {
		long countItems = materias.stream().filter(item -> item.getIdMateria().equals(idMateria)).count();
		return countItems > 0;
	}
	
	private boolean validateSub(List<SubMateriaResponse> subMaterias, String idSubMateria) {
		long countItems = subMaterias.stream().filter(item -> item.getIdSubMateria().equals(idSubMateria)).count();
		return countItems > 0;
	}
	
	public List<Map<String, Object>> transformToMapCritidicidad(List<Caso> casos, double suma) {
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapa;
		for (Caso caso : casos) {
			mapa = new HashMap<String, Object>();
			mapa.put("nombreCaso", caso.getDescripcionCaso());
			mapa.put("montoMulta", formatMoney(caso.getMultaPotencial().doubleValue()));
			mapa.put("porcentaje", getPorcentaje(caso.getMultaPotencial().doubleValue(), suma));
			listMap.add(mapa);
		}
		return listMap;
	}
}
