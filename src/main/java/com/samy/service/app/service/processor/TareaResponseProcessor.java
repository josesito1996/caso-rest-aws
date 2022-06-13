package com.samy.service.app.service.processor;

import static com.samy.service.app.util.Utils.convertActualZone;
import static com.samy.service.app.util.Utils.fechaFormateada;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.model.Actuacion;
import com.samy.service.app.model.Caso;
import com.samy.service.app.model.Tarea;
import com.samy.service.app.model.request.TareaArchivoBody;
import com.samy.service.app.model.request.TareaBody;
import com.samy.service.app.model.response.NotificacionesVencimientosResponse;
import com.samy.service.app.model.response.SaveTareaResponse;
import com.samy.service.app.repo.CasoRepo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class TareaResponseProcessor {

	private CasoRequestBuilder builder;
	
	private CasoRepo casoRepo;
	
	public SaveTareaResponse registraTareaResponse(TareaBody request, String idActuacion, Caso caso) {
		Caso casoRegistrado = casoRepo.save(builder.transformTarea(caso, request, idActuacion));
		List<Actuacion> actuaciones = casoRegistrado.getActuaciones().stream()
				.filter(item -> item.getIdActuacion().equals(idActuacion)).collect(Collectors.toList());
		List<Tarea> tareas = actuaciones.get(0).getTareas();
		int tareasSize = tareas.size();
		Tarea tarea = tareas.get(tareasSize - 1);
		return SaveTareaResponse.builder().idTarea(tarea.getIdTarea()).tipoTarea(tarea.getTipoTarea().getNombreTipo())
				.descripcion(tarea.getDenominacion())
				.destinatario(
						request.getEquipos().stream().map(item -> item.getDestinatario()).collect(Collectors.toList()))
				.correo(request.getEquipos().stream().map(item -> item.getCorreo()).collect(Collectors.toList()))
				.recordatorio(request.getRecordatorio().getDia()).mensaje(request.getMensaje())
				.fechaRegistro(fechaFormateada(tarea.getFechaRegistro()))
				.fechaVencimiento(fechaFormateada(tarea.getFechaVencimiento())).build();
	}
	
	public SaveTareaResponse transformResponse(Tarea tarea) {
		return SaveTareaResponse.builder().idTarea(tarea.getIdTarea()).tipoTarea(tarea.getTipoTarea().getNombreTipo())
				.descripcion(tarea.getDenominacion())
				.destinatario(tarea.getEquipos().stream().map(item -> item.getNombre()).collect(Collectors.toList()))
				.correo(tarea.getEquipos().stream().map(item -> item.getCorreo()).collect(Collectors.toList()))
				.recordatorio(tarea.getRecordatorio().getDia()).mensaje(tarea.getMensaje())
				.fechaRegistro(fechaFormateada(tarea.getFechaRegistro()))
				.fechaVencimiento(fechaFormateada(tarea.getFechaVencimiento())).build();
	}
	
	public Map<String, Object> transformMapTarea(Caso caso, TareaArchivoBody tareaArchivoBody) {
		Map<String, Object> newMap = new HashMap<String, Object>();
		List<Actuacion> actuaciones = caso.getActuaciones().stream()
				.filter(actu -> actu.getIdActuacion().equals(tareaArchivoBody.getId_actuacion()))
				.collect(Collectors.toList());
		if (!actuaciones.isEmpty()) {
			List<Tarea> tareas = actuaciones.get(0).getTareas().stream()
					.filter(tarea -> tarea.getIdTarea().equals(tareaArchivoBody.getId_tarea()))
					.collect(Collectors.toList());
			if (!tareas.isEmpty()) {
				List<ArchivoAdjunto> archivos = tareas.get(0).getArchivos();
				int indice = archivos.size() > 0 ? archivos.size() - 1 : 0;
				newMap.put("id", tareaArchivoBody.getId_tarea());
				newMap.put("archivos", archivos(Arrays.asList(archivos.get(indice))));
			}
		}
		return newMap;
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
	
	public List<NotificacionesVencimientosResponse> test(List<Caso> casos, boolean isProximos) {
		List<Caso> listaCaso = casos;
		List<NotificacionesVencimientosResponse> notiVenci = new ArrayList<NotificacionesVencimientosResponse>();
		for (Caso caso : listaCaso) {
			List<Actuacion> actuaciones = caso.getActuaciones();
			for (Actuacion actuacion : actuaciones) {
				List<Tarea> tareas = actuacion.getTareas();
				for (Tarea tarea : tareas) {
					LocalDate fechaVencimiento = tarea.getFechaVencimiento().toLocalDate();
					LocalDate fechaActual = convertActualZone(LocalDate.now());
					// LocalDate fechaAumentada = fechaActual.plusDays(diasPlazoVencimiento);
					/**
					 * Validar este tema
					 */
					if (isProximos) {
						if (fechaVencimiento.isAfter(fechaActual) || fechaVencimiento.isEqual(fechaActual)) {
							notiVenci.add(NotificacionesVencimientosResponse.builder().idCaso(caso.getId())
									.idActuacion(actuacion.getIdActuacion()).idTarea(tarea.getIdTarea())
									.fechaVencimiento(fechaFormateada(tarea.getFechaVencimiento()))
									.fechaVenc(fechaVencimiento).nombreCaso(caso.getDescripcionCaso())
									.descripcion(getObject(tarea)).build());
						}
					} else {
						if (fechaVencimiento.isBefore(fechaActual.minusDays(1))) {
							notiVenci.add(NotificacionesVencimientosResponse.builder().idCaso(caso.getId())
									.idActuacion(actuacion.getIdActuacion()).idTarea(tarea.getIdTarea())
									.fechaVencimiento(fechaFormateada(tarea.getFechaVencimiento()))
									.fechaVenc(fechaVencimiento).nombreCaso(caso.getDescripcionCaso())
									.descripcion(getObject(tarea)).build());
						}
					}
				}
			}
		}
		return notiVenci.stream().sorted(Comparator.comparing(NotificacionesVencimientosResponse::getFechaVenc))
				.collect(Collectors.toList());
	}
	private Map<String, Object> getObject(Tarea tarea) {
		Map<String, Object> lista = new HashMap<String, Object>();
		lista.put("cabecera", "Tarea por vencer");
		lista.put("contenido", tarea.getDenominacion());
		return lista;
	}
}
