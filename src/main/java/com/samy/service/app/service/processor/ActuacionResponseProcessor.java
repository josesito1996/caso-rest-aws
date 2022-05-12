package com.samy.service.app.service.processor;

import static com.samy.service.app.util.Utils.esNumero;
import static com.samy.service.app.util.Utils.fechaFormateada;
import static com.samy.service.app.util.Utils.fechaFormateadaOther;
import static com.samy.service.app.util.Utils.transformToLocalTime;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.samy.service.app.aws.ExternalDbAws;
import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.external.FuncionarioDto;
import com.samy.service.app.model.Actuacion;
import com.samy.service.app.model.Caso;
import com.samy.service.app.model.Tarea;
import com.samy.service.app.model.response.ActuacionResponseX2;
import com.samy.service.app.model.response.ActuacionResponseX3;
import com.samy.service.app.model.response.DocumentoAnexoResponse;
import com.samy.service.samiprimary.service.model.InspectorResponse;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ActuacionResponseProcessor {

	private ExternalDbAws externalAws;

	public ActuacionResponseX3 transformActuacionResponseX3(Actuacion actuacion) {
		List<ArchivoAdjunto> archivos = actuacion.getArchivos();
		String registradoPor = "";
		if (actuacion.getRegistradoPor() != null) {
			registradoPor = externalAws.getUser(actuacion.getRegistradoPor()).getDatosUsuario();
		}
		String nombreArchivo = "";
		if (!archivos.isEmpty()) {
			for (ArchivoAdjunto adj : archivos) {
				if (adj.isEsPrincipal()) {
					nombreArchivo = adj.getNombreArchivo();
					break;
				}
			}
		}
		return ActuacionResponseX3.builder().idActuacion(actuacion.getIdActuacion()).documentoPrincipal(nombreArchivo)
				.fechaRegistro(fechaFormateadaOther(transformToLocalTime(actuacion.getFechaActuacion(),
						actuacion.getFechaRegistro().toLocalTime())))
				.fechaActuacion(fechaFormateada(actuacion.getFechaActuacion()))
				.nombreActuacion(actuacion.getDescripcion()).descripcion(actuacion.getDescripcionAux())
				.subidoPor(registradoPor)
				.anexos((int) actuacion.getArchivos().stream().filter(item -> !item.isEsPrincipal()).count())
				.tipoActuacion(actuacion.getTipoActuacion().getNombreTipoActuacion())
				.funcionarios(
						transformListFuncionarioMap(actuacion.getFuncionario(), actuacion.getEtapa().getNombreEtapa()))
				.vencimientos(transformListVencimientoMap(actuacion.getTareas()))// Asumo que son de
																					// las
																					// tareas.
				.documentosAnexos(transformDocumentosAnexos(actuacion.getArchivos())).build();
	}

	private List<Map<String, Object>> transformListFuncionarioMap(List<FuncionarioDto> funcionarios, String etapa) {
		return funcionarios.stream().map(item -> transformFuncionarioMap(item, etapa)).collect(Collectors.toList());
	}

	private Map<String, Object> transformFuncionarioMap(FuncionarioDto dto, String etapa) {
		log.info("CasoServiceImpl.transformFuncionarioMap {}", dto);
		InspectorResponse inspectorResponse;
		if (esNumero(dto.getId())) {
			inspectorResponse = new InspectorResponse();
			inspectorResponse.setId(dto.getId());
			inspectorResponse.setNombresApellidos(dto.getDatosFuncionario());
		} else {
			inspectorResponse = externalAws.tableInspector(dto.getId());
		}
		Map<String, Object> mapFuncionario = new HashMap<>();
		mapFuncionario.put("idFuncionario", inspectorResponse.getId());
		mapFuncionario.put("nombre", inspectorResponse.getNombresApellidos());
		mapFuncionario.put("cargo", inspectorResponse.getCargo());
		mapFuncionario.put("etapa", etapa);
		return mapFuncionario;
	}

	private List<Map<String, Object>> transformListVencimientoMap(List<Tarea> tareas) {
		return tareas.stream().filter(tarea -> !tarea.isEliminado())
				.sorted(Comparator.comparing(Tarea::getFechaVencimiento)).map(this::transformVencimientoMap)
				.collect(Collectors.toList());
	}

	private Map<String, Object> transformVencimientoMap(Tarea dto) {
		Map<String, Object> mapFuncionario = new HashMap<String, Object>();
		mapFuncionario.put("id", dto.getIdTarea());
		mapFuncionario.put("fecha", fechaFormateada(dto.getFechaVencimiento()));
		mapFuncionario.put("descripcion", dto.getDenominacion());
		return mapFuncionario;
	}

	private List<DocumentoAnexoResponse> transformDocumentosAnexos(List<ArchivoAdjunto> archivos) {
		return archivos.stream().map(this::transfomrDocumentoAnexoResponse)
				.sorted(Comparator.comparing(DocumentoAnexoResponse::isEsPrincipal).reversed())
				.collect(Collectors.toList());
	}

	private DocumentoAnexoResponse transfomrDocumentoAnexoResponse(ArchivoAdjunto archivo) {
		String fechaRegistro = archivo.getFechaRegistro() != null ? fechaFormateada(archivo.getFechaRegistro()) : null;
		return DocumentoAnexoResponse.builder().idArchivo(archivo.getId()).type(archivo.getTipoArchivo())
				.nombreArchivo(archivo.getNombreArchivo()).tamaño(archivo.getTamaño()).fechaRegistro(fechaRegistro)
				.esPrincipal(archivo.isEsPrincipal()).url(archivo.getUrl()).build();
	}
	
	public ActuacionResponseX2 transformMap(Caso caso) {
		List<Actuacion> actuaciones = caso.getActuaciones();
		int ultimoItem = actuaciones.isEmpty() ? 0 : actuaciones.size() - 1;
		return ActuacionResponseX2.builder().id(actuaciones.get(ultimoItem).getIdActuacion())
				.archivos(archivos(actuaciones.get(ultimoItem).getArchivos())).build();
	}
	
	private List<ArchivoAdjunto> archivos(List<ArchivoAdjunto> archivos) {
		return archivos.stream().map(this::transform).collect(Collectors.toList());
	}

	private ArchivoAdjunto transform(ArchivoAdjunto archivo) {
		return ArchivoAdjunto.builder()
				// .id(archivo.getId().concat(getExtension(archivo.getNombreArchivo())))
				.id(archivo.getId()).nombreArchivo(archivo.getNombreArchivo()).tipoArchivo(archivo.getTipoArchivo())
				.build();
	}

}
