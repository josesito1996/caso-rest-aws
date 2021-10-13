package com.samy.service.app.service.impl;

import static com.samy.service.app.util.ListUtils.listArchivoAdjunto;
import static com.samy.service.app.util.Utils.uuidGenerado;

import java.math.BigDecimal;
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
import com.samy.service.app.external.EstadoCasoDto;
import com.samy.service.app.external.EtapaDto;
import com.samy.service.app.external.FuncionarioDto;
import com.samy.service.app.external.InspectorDto;
import com.samy.service.app.external.MateriaDto;
import com.samy.service.app.external.SubMateriaDto;
import com.samy.service.app.external.TipoActuacionDto;
import com.samy.service.app.external.TipoTarea;
import com.samy.service.app.model.Actuacion;
import com.samy.service.app.model.Caso;
import com.samy.service.app.model.Personal;
import com.samy.service.app.model.Tarea;
import com.samy.service.app.model.request.ActuacionBody;
import com.samy.service.app.model.request.ArchivoBody;
import com.samy.service.app.model.request.CasoBody;
import com.samy.service.app.model.request.EquipoBody;
import com.samy.service.app.model.request.MateriaRequest;
import com.samy.service.app.model.request.ReactSelectRequest;
import com.samy.service.app.model.request.SubMateriaCheck;
import com.samy.service.app.model.request.TareaArchivoBody;
import com.samy.service.app.model.request.TareaBody;
import com.samy.service.app.model.request.TareaCambioEstadoBody;
import com.samy.service.app.service.PersonalService;
import com.samy.service.app.util.Utils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CasoRequestBuilder {

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
      log.info("...."+ actuacionBody.getEtapa());
        Actuacion actuacion = new Actuacion();
        actuacion.setIdActuacion(uuidGenerado());
        actuacion.setFechaActuacion(actuacionBody.getFechaActuacion());
        actuacion.setFechaRegistro(LocalDateTime.now());
        actuacion.setDescripcion(actuacionBody.getDescripcion());
        actuacion.setFuncionario(transformToFuncionarioDto(actuacionBody.getFuncionarios()));
        actuacion.setTipoActuacion(toTipoActuacion(actuacionBody.getTipoActuacion()));
        actuacion.setEtapa(toEtapaDto(actuacionBody.getEtapa()));
        actuacion.setArchivos(listArchivoAdjunto(actuacionBody.getArchivos()));
        actuacion.setEstadoCaso(
                EstadoCasoDto.builder().idEstadoCaso(actuacionBody.getEstadoCaso().getValue())
                        .nombreEstado(actuacionBody.getEstadoCaso().getLabel()).build());
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
            request.setIdTarea(uuidGenerado());
            tareas = Arrays.asList(transformTareaFromBody(request));
        } else {
            if (request.getIdTarea() != null) {
                int indice = tareas.indexOf(tareas.stream()
                        .filter(item -> item.getIdTarea().equals(request.getIdTarea()))
                        .collect(Collectors.toList()).get(0));
                tareas.set(indice, transformTareaFromBody(request));
            } else {
                request.setIdTarea(uuidGenerado());
                request.setEliminado(false);
                tareas.add(transformTareaFromBody(request));
            }
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
            //////////
            archivos = listArchivoAdjunto(tareaArchivoBody.getArchivos());
            // archivos =
            // fileService.uploadFile(fileBuilder.getFiles(tareaArchivoBody.getArchivos()));
        } else {
            // List<ArchivoAdjunto> archivosAux = fileService
            // .uploadFile(fileBuilder.getFiles(tareaArchivoBody.getArchivos()));
            for (ArchivoAdjunto archivo : listArchivoAdjunto(tareaArchivoBody.getArchivos())) {
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

        tareas.get(indexTarea).setEstado(tareaCambioEstadoBody.isEstado());
        actuaciones.get(indexActuacion).setTareas(tareas);
        caso.setActuaciones(actuaciones);
        return caso;
    }

    public Caso transformFromBody(CasoBody request) {
        Caso caso = new Caso();
        caso.setId(request.getIdCaso());
        caso.setFechaInicio(request.getFechaInicio());
        caso.setOrdenInspeccion(request.getOrdenInspeccion());
        caso.setMultaPotencial(BigDecimal.valueOf(Math.random() * 10000));// VAlor aleatorio
        caso.setInspectorTrabajo(getInspectorDto(request.getInspectorTrabajo()));
        caso.setInspectorAuxiliar(getInspectorDto(request.getInspectorAuxiliar()));
        caso.setMaterias(getMateriaDto(request.getMaterias()));
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

    private List<MateriaDto> getMateriaDto(List<String> materias) {
        return materias.stream().map(item -> Utils.convertFromString(item, MateriaDto.class))
                .map(this::initSubMateria).collect(Collectors.toList());
    }

    private MateriaDto initSubMateria(MateriaDto materia) {
        materia.setSubMaterias(new ArrayList<SubMateriaDto>());
        return materia;
    }

    private TipoActuacionDto toTipoActuacion(ReactSelectRequest reactSelectRequest) {
        return new TipoActuacionDto(reactSelectRequest.getValue(), reactSelectRequest.getLabel());
    }

    private EtapaDto toEtapaDto(ReactSelectRequest reactSelectRequest) {
        EtapaDto etapaDto = null;
        switch (reactSelectRequest.getLabel()) {
        case "Instrucción":
            etapaDto = new EtapaDto(reactSelectRequest.getValue(), reactSelectRequest.getLabel(),
                    2);
            break;
        case "Investigación":
            etapaDto = new EtapaDto(reactSelectRequest.getValue(), reactSelectRequest.getLabel(),
                    1);
            break;
        case "Sancionador":
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
        tarea.setTipoTarea(transformTipoTarea(tareaBody.getTipoTarea()));
        tarea.setDenominacion(tareaBody.getDenominacion());
        tarea.setFechaRegistro(LocalDateTime.now());
        tarea.setIdTarea(tareaBody.getIdTarea());
        tarea.setRecordatorio(tareaBody.getRecordatorio());
        tarea.setFechaVencimiento(
                LocalDateTime.of(tareaBody.getFechaVencimiento(), LocalTime.now()));
        tarea.setEliminado(tareaBody.isEliminado());
        tarea.setEstado(tareaBody.isEstado());
        if (tareaBody.getTipoTarea().getLabel().equals("Solicitud")) {
            tarea.setMensaje(tareaBody.getMensaje());
            if (tareaBody.getEquipos() == null) {
                throw new BadRequestException("Se debe registrar destinatarios");
            }
            tarea.setEquipos(getEquipos(tareaBody.getEquipos()));   
        } else {
            tarea.setEquipos(new ArrayList<EquipoDto>());
        }
        if (tareaBody.getIdTarea() != null && tareaBody.getArchivos() != null) {
            tarea.setArchivos(listArchivoAdjunto(tareaBody.getArchivos()));
        } else {
            tarea.setArchivos(new ArrayList<ArchivoAdjunto>());
        }
        return tarea;
    }

    private TipoTarea transformTipoTarea(ReactSelectRequest reactRequest) {
        return TipoTarea.builder().idTipoTarea(reactRequest.getValue())
                .nombreTipo(reactRequest.getLabel()).build();
    }

    public List<EquipoBody> getEquiposBody(List<EquipoDto> equipoDto) {
        return equipoDto.stream().map(this::transformEquipoDto).collect(Collectors.toList());
    }

    private EquipoBody transformEquipoDto(EquipoDto equipoBody) {
        String idEquipo = equipoBody.getIdEquipo();
        Personal personal = personalService.verUnoPorId(idEquipo);
        return EquipoBody.builder().idEquipo(idEquipo).correo(personal.getCorreo())
                .destinatario(personal.getDatos()).build();
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

    public List<ArchivoBody> listArchivoBody(List<ArchivoAdjunto> archivos) {
        return archivos.stream().map(this::getArchivoBody).collect(Collectors.toList());
    }

    private ArchivoBody getArchivoBody(ArchivoAdjunto body) {
        return ArchivoBody.builder().idArchivo(body.getId()).nombreArchivo(body.getNombreArchivo())
                .tipo(body.getTipoArchivo()).estado(body.isEstado()).build();
    }

    /**
     * Util para registrar la SubMAteria en las materias del Caso.
     * 
     * @param materias
     * @return
     */
    public List<MateriaDto> materiaDtoBuilderList(List<MateriaRequest> materias) {
        return materias.stream().map(this::materiaDtoBuilder).collect(Collectors.toList());
    }

    private MateriaDto materiaDtoBuilder(MateriaRequest request) {
        return MateriaDto.builder().id(request.getIdMateria())
                .subMaterias(subMateriaListBuilder(request)).build();
    }

    private List<SubMateriaDto> subMateriaListBuilder(MateriaRequest request) {
        List<SubMateriaDto> newList = new ArrayList<SubMateriaDto>();
        for (SubMateriaCheck sub : request.getSubMateriasCheck()) {
            newList.add(SubMateriaDto.builder().idSubMateria(sub.getIdSubMateria())
                    .nombreSubMateria(sub.getSubMateria()).idMateria(request.getIdMateria())
                    .build());
        }
        for (ReactSelectRequest react : request.getSubMateriasSelect()) {
            newList.add(SubMateriaDto.builder().idSubMateria(react.getValue())
                    .nombreSubMateria(react.getLabel()).idMateria(request.getIdMateria()).build());
        }
        return newList;
    }
}
