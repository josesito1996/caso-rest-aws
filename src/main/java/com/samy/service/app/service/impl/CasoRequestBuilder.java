package com.samy.service.app.service.impl;

import static com.samy.service.app.util.Utils.uuidGenerado;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.samy.service.app.external.InspectorDto;
import com.samy.service.app.external.MateriasDto;
import com.samy.service.app.model.Actuacion;
import com.samy.service.app.model.Caso;
import com.samy.service.app.model.Tarea;
import com.samy.service.app.model.request.ActuacionBody;
import com.samy.service.app.model.request.CasoBody;
import com.samy.service.app.model.request.ReactSelectRequest;
import com.samy.service.app.model.request.TareaBody;
import com.samy.service.app.util.Utils;

@Component
public class CasoRequestBuilder {

	public Caso transformFromNewCaso(Caso caso, ActuacionBody request) {
		List<Actuacion> actuaciones = caso.getActuaciones();

		if (actuaciones.size() > 0) {
			actuaciones.add(transformActuacionFromBody(request));
		} else {
			actuaciones = Arrays.asList(transformActuacionFromBody(request));
		}
		caso.setActuaciones(actuaciones);
		return caso;
	}

	public Caso transformFromNewActuacion(Caso caso, TareaBody request, String idActuacion) {
		List<Actuacion> actuaciones = caso.getActuaciones();
		Integer index = 0;
		for (int i = 0; i <= actuaciones.size(); i++) {
			if (actuaciones.get(i).getIdActuacion().equals(idActuacion)) {
				index = i;
				break;
			}
		}
		Actuacion actuacionFound = actuaciones.get(index);
		List<Tarea> tareas = actuacionFound.getTareas();
		if (tareas.isEmpty()) {
			tareas = Arrays.asList(transformTareaFromBody(request));
		} else {
			tareas.add(transformTareaFromBody(request));
		}
		caso.getActuaciones().get(index).setTareas(tareas);
		return caso;
	}

	public Caso transformFromBody(CasoBody request) {
		Caso caso = new Caso();
		caso.setId(request.getIdCaso());
		caso.setFechaInicio(request.getFechaInicio());
		caso.setOrdenInspeccion(request.getOrdenInspeccion());
		caso.setInspectorTrabajo(getInspectorDto(request.getInspectorTrabajo()));
		caso.setInspectorAuxiliar(getInspectorDto(request.getInspectorAuxiliar()));
		caso.setMaterias(getMateriasDto(request.getMaterias()));
		caso.setDescripcionCaso(request.getDescripcionCaso());
		if (request.getIdCaso() == null) {
			caso.setRegistro(LocalDateTime.now());
		}
		caso.setEstadoCaso(request.getEstado());
		caso.setActuaciones(transformListActuacionFromBody(request.getActuacionBody()));
		caso.setUsuario(request.getUsuario());
		return caso;
	}

	private List<InspectorDto> getInspectorDto(List<ReactSelectRequest> listReact) {
		return listReact.stream().map(this::transformFromReact).collect(Collectors.toList());
	}

	private InspectorDto transformFromReact(ReactSelectRequest reactSelectRequest) {
		return new InspectorDto(reactSelectRequest.getValue(), reactSelectRequest.getLabel());
	}

	private List<MateriasDto> getMateriasDto(List<String> materias) {
		return materias.stream().map(item -> Utils.convertFromString(item, MateriasDto.class))
				.collect(Collectors.toList());
	}

	private List<Actuacion> transformListActuacionFromBody(List<ActuacionBody> actuaciones) {
		if (actuaciones == null)
			return new ArrayList<>();
		return actuaciones.stream().map(this::transformActuacionFromBody).collect(Collectors.toList());
	}

	private Actuacion transformActuacionFromBody(ActuacionBody actuacionBody) {
		if (actuacionBody == null) {
			return new Actuacion();
		}
		Actuacion actuacion = new Actuacion();
		String idActuacion = actuacionBody.getIdActuacion();
		actuacion.setIdActuacion(idActuacion == null ? uuidGenerado() : idActuacion);
		actuacion.setFechaActuacion(actuacionBody.getFechaActuacion());
		if (actuacionBody.getIdActuacion() == null) {
			actuacion.setFechaRegistro(LocalDateTime.now());
		}
		actuacion.setDescripcion(actuacionBody.getDescripcion());
		actuacion.setFuncionario(actuacionBody.getFuncionario());
		actuacion.setTipoActuacion(actuacionBody.getTipoActuacion());
		actuacion.setEtapa(actuacionBody.getEtapa());
		actuacion.setArchivos(actuacionBody.getArchivos());
		actuacion.setTareas(transformListTareaFromBody(actuacionBody.getTareas()));
		return actuacion;
	}

	private List<Tarea> transformListTareaFromBody(List<TareaBody> tareas) {
		if (tareas == null)
			return new ArrayList<>();
		return tareas.stream().map(this::transformTareaFromBody).collect(Collectors.toList());
	}

	private Tarea transformTareaFromBody(TareaBody tareaBody) {
		Tarea tarea = new Tarea();
		if (tareaBody.getIdTarea() == null) {
			tarea.setFechaRegistro(LocalDateTime.now());
		}
		String idTarea = tareaBody.getIdTarea();
		tarea.setIdTarea(idTarea == null ? uuidGenerado() : idTarea);
		tarea.setDenominacion(tareaBody.getDenominacion());
		tarea.setFechaVencimiento(tareaBody.getFechaVencimiento());
		tarea.setEquipo(tareaBody.getEquipo());
		tarea.setArchivos(tareaBody.getArchivos());
		tarea.setEstado(tareaBody.getEstado());
		return tarea;
	}
}
