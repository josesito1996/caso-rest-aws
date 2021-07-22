package com.samy.service.app.service.impl;

import static com.samy.service.app.util.Utils.uuidGenerado;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.samy.service.app.exception.BadRequestException;
import com.samy.service.app.exception.NotFoundException;
import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.external.EquipoDto;
import com.samy.service.app.external.EtapaDto;
import com.samy.service.app.external.FuncionarioDto;
import com.samy.service.app.external.InspectorDto;
import com.samy.service.app.external.MateriasDto;
import com.samy.service.app.external.TipoActuacionDto;
import com.samy.service.app.model.Actuacion;
import com.samy.service.app.model.Caso;
import com.samy.service.app.model.Personal;
import com.samy.service.app.model.Tarea;
import com.samy.service.app.model.request.ActuacionBody;
import com.samy.service.app.model.request.CasoBody;
import com.samy.service.app.model.request.EquipoBody;
import com.samy.service.app.model.request.ReactSelectRequest;
import com.samy.service.app.model.request.TareaArchivoBody;
import com.samy.service.app.model.request.TareaBody;
import com.samy.service.app.model.request.TareaCambioEstadoBody;
import com.samy.service.app.service.PersonalService;
import com.samy.service.app.util.Utils;

@Component
public class CasoRequestBuilder {

    @Autowired
    private FileServiceImpl fileService;

    @Autowired
    private FileResquestBuilder fileBuilder;

    @Autowired
    private PersonalService personalService;

    public Caso transformActuacion(Caso caso, ActuacionBody request) {
        List<Actuacion> actuaciones = caso.getActuaciones();
        if (actuaciones.size() > 0) {
            actuaciones.add(transformActuacionFromBody(request));
        } else {
            actuaciones = Arrays.asList(transformActuacionFromBody(request));
        }
        caso.setActuaciones(actuaciones);
        return caso;
    }

    private Actuacion transformActuacionFromBody(ActuacionBody actuacionBody) {
        Actuacion actuacion = new Actuacion();
        actuacion.setIdActuacion(uuidGenerado());
        actuacion.setFechaActuacion(actuacionBody.getFechaActuacion());
        actuacion.setFechaRegistro(LocalDateTime.now());
        actuacion.setDescripcion(actuacionBody.getDescripcion());
        actuacion.setFuncionario(transformToFuncionarioDto(actuacionBody.getFuncionarios()));
        actuacion.setTipoActuacion(toTipoActuacion(actuacionBody.getTipoActuacion()));
        actuacion.setEtapa(toEtapaDto(actuacionBody.getEtapa()));
        actuacion.setArchivos(
                fileService.uploadFile(fileBuilder.getFiles(actuacionBody.getArchivos())));
        actuacion.setTareas(new ArrayList<Tarea>());
        return actuacion;
    }

    @Transactional
    public Caso transformTarea(Caso caso, TareaBody request, String idActuacion) {
        List<Actuacion> actuaciones = caso.getActuaciones();
        if (actuaciones.isEmpty()) {
            throw new BadRequestException("Este caso no tiene actuaciones registradas");
        }
        Integer index = 0;
        for (int i = 0; i < actuaciones.size(); i++) {
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

    public Caso transformUpdateTarea(Caso caso, TareaArchivoBody tareaArchivoBody) {
        List<Actuacion> actuaciones = caso.getActuaciones();
        int indexActuacion = getIndexActuacion(tareaArchivoBody.getId_actuacion(), actuaciones);

        List<Tarea> tareas = actuaciones.get(indexActuacion).getTareas();
        int indexTarea = getIndexTarea(tareaArchivoBody.getId_tarea(), tareas);

        List<ArchivoAdjunto> archivos = tareas.get(indexTarea).getArchivos();
        if (archivos.isEmpty()) {
            archivos = fileService.uploadFile(fileBuilder.getFiles(tareaArchivoBody.getArchivos()));
        } else {
            List<ArchivoAdjunto> archivosAux = fileService
                    .uploadFile(fileBuilder.getFiles(tareaArchivoBody.getArchivos()));
            for (ArchivoAdjunto archivo : archivosAux) {
                archivos.add(archivo);
            }
        }
        tareas.get(indexTarea).setArchivos(archivos);
        actuaciones.get(indexActuacion).setTareas(tareas);
        caso.setActuaciones(actuaciones);
        return caso;
    }

    public Caso transformCambioEstadoTarea(Caso caso, TareaCambioEstadoBody tareaCambioEstadoBody) {
        List<Actuacion> actuaciones = caso.getActuaciones();
        int indexActuacion = getIndexActuacion(tareaCambioEstadoBody.getId_actuacion(),
                actuaciones);

        List<Tarea> tareas = actuaciones.get(indexActuacion).getTareas();
        int indexTarea = getIndexTarea(tareaCambioEstadoBody.getId_tarea(), tareas);

        tareas.get(indexTarea).setEstado(tareaCambioEstadoBody.getEstado());
        actuaciones.get(indexActuacion).setTareas(tareas);
        caso.setActuaciones(actuaciones);
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
        caso.setRegistro(LocalDateTime.now());
        caso.setEstadoCaso(request.getEstado());
        caso.setActuaciones(new ArrayList<Actuacion>());
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

    private TipoActuacionDto toTipoActuacion(ReactSelectRequest reactSelectRequest) {
        return new TipoActuacionDto(reactSelectRequest.getValue(), reactSelectRequest.getLabel());
    }

    private EtapaDto toEtapaDto(ReactSelectRequest reactSelectRequest) {
        EtapaDto etapaDto = null;
        switch (reactSelectRequest.getLabel()) {
        case "Instruccion":
            etapaDto = new EtapaDto(reactSelectRequest.getValue(), reactSelectRequest.getLabel(),
                    2);
            break;
        case "Investigacion":
            etapaDto = new EtapaDto(reactSelectRequest.getValue(), reactSelectRequest.getLabel(),
                    1);
            break;
        case "Sancionadora":
            etapaDto = new EtapaDto(reactSelectRequest.getValue(), reactSelectRequest.getLabel(),
                    3);
            break;
        default:
            break;
        }
        return etapaDto;
    }

    private List<FuncionarioDto> transformToFuncionarioDto(
            List<ReactSelectRequest> reactSelectRequests) {
        return reactSelectRequests.stream().map(this::transformDto).collect(Collectors.toList());
    }

    private FuncionarioDto transformDto(ReactSelectRequest reactSelectRequest) {
        return new FuncionarioDto(reactSelectRequest.getValue(), reactSelectRequest.getLabel());
    }

    private Tarea transformTareaFromBody(TareaBody tareaBody) {
        Tarea tarea = new Tarea();
        tarea.setFechaRegistro(LocalDateTime.now());
        tarea.setIdTarea(uuidGenerado());
        tarea.setDenominacion(tareaBody.getDenominacion());
        tarea.setFechaVencimiento(
                LocalDateTime.of(tareaBody.getFechaVencimiento(), LocalTime.now()));
        tarea.setEquipos(getEquipos(tareaBody.getEquipos()));
        tarea.setMensaje(tareaBody.getMensaje());
        tarea.setEstado(tareaBody.getEstado());
        tarea.setArchivos(new ArrayList<ArchivoAdjunto>());
        return tarea;
    }

    private List<EquipoDto> getEquipos(List<EquipoBody> equipoBodies) {
        return equipoBodies.stream().map(this::transformEquipoBody).collect(Collectors.toList());
    }

    private EquipoDto transformEquipoBody(EquipoBody equipoBody) {
        return EquipoDto.builder()
                .idEquipo(personalService.registrarPersonal(
                        new Personal(null, equipoBody.getDestinatario(), equipoBody.getCorreo()))
                        .getIdPersonal())
                .build();
    }

    public List<String> getEquiposString(List<EquipoDto> equipos) {
        return personalService.listarPersonal(equipos);
    }

    private int getIndexActuacion(String idActuacion, List<Actuacion> actuaciones) {
        if (actuaciones.isEmpty()) {
            throw new BadRequestException("Este caso no tiene actuaciones registradas");
        }
        List<Actuacion> actuacionAux = actuaciones.stream()
                .filter(item -> item.getIdActuacion().equals(idActuacion))
                .collect(Collectors.toList());
        if (actuacionAux.isEmpty()) {
            throw new NotFoundException("El id : " + idActuacion + " no se encuentra registrado");
        }
        return actuaciones.indexOf(actuacionAux.get(0));
    }

    private int getIndexTarea(String idTarea, List<Tarea> tareas) {
        if (tareas.isEmpty()) {
            throw new BadRequestException("Esta actuacion no tiene tareas registradas");
        }
        List<Tarea> tareaAux = tareas.stream().filter(item -> item.getIdTarea().equals(idTarea))
                .collect(Collectors.toList());
        if (tareaAux.isEmpty()) {
            throw new NotFoundException(
                    "El id de tarea : " + idTarea + " no se encuentra registrado");
        }
        return tareas.indexOf(tareaAux.get(0));
    }

}
