package com.samy.service.app.service.impl;

import static com.samy.service.app.service.impl.ServiceUtils.cantidadDocumentosGenerales;
import static com.samy.service.app.service.impl.ServiceUtils.cantidadTareasIndividualPorEstado;
import static com.samy.service.app.service.impl.ServiceUtils.cantidadTareasPendientesGeneral;
import static com.samy.service.app.util.Utils.fechaFormateada;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.model.Actuacion;
import com.samy.service.app.model.Caso;
import com.samy.service.app.model.Tarea;
import com.samy.service.app.model.response.ActuacionDetalleFileResponse;
import com.samy.service.app.model.response.ActuacionFileResponse;
import com.samy.service.app.model.response.ActuacionPrincipalResponse;
import com.samy.service.app.model.response.DocumentoDetalleResponse;
import com.samy.service.app.model.response.TareaDetalleResponse;
import com.samy.service.app.service.ActuacionFilesService;
import com.samy.service.app.service.CasoService;

@Service
public class ActuacionFileServiceImpl implements ActuacionFilesService {

    @Autowired
    private CasoService service;

    @Override
    public ActuacionFileResponse listarActuacionesConArchivos(String idCaso) {
        return transformCaso(service.verPodId(idCaso));
    }

    private ActuacionFileResponse transformCaso(Caso caso) {
        return ActuacionFileResponse.builder().totalActuaciones(caso.getActuaciones().size())
                .totalDocumentosActuacion(cantidadDocumentosGenerales(caso))
                .totalPendientes(cantidadTareasPendientesGeneral(caso))
                .actuaciones(transformActuaciones(caso.getActuaciones())).build();
    }

    private List<ActuacionDetalleFileResponse> transformActuaciones(List<Actuacion> actuaciones) {
        List<ActuacionDetalleFileResponse> lista = new ArrayList<ActuacionDetalleFileResponse>();
        int contador = 0;
        for (Actuacion actuacion : actuaciones) {
            ActuacionDetalleFileResponse actuacionDetalleFileResponse = transformActuacionDetalleFileResponse(
                    actuacion);
            actuacionDetalleFileResponse.setIsOpen(contador == 0);
            lista.add(actuacionDetalleFileResponse);
            contador ++;
        }
        return lista;
    }

    private ActuacionDetalleFileResponse transformActuacionDetalleFileResponse(
            Actuacion actuacion) {
        return ActuacionDetalleFileResponse.builder()
                .idActuacion(actuacion.getIdActuacion())
                .fechaRegistro(fechaFormateada(actuacion.getFechaActuacion()))
                .tipoActuacion(actuacion.getTipoActuacion().getNombreTipoActuacion())
                .totalTareasActuacion(actuacion.getTareas().size())
                .totalTareasRealizadas(
                        cantidadTareasIndividualPorEstado(actuacion.getTareas(), true))
                .totalDocumentosPendientes(
                        cantidadTareasIndividualPorEstado(actuacion.getTareas(), false))
                .subidoPor(null)
                .actuacionPrincipal(ActuacionPrincipalResponse.builder()
                        .denominacion(actuacion.getDescripcion())
                        .documentos(transformDocuments(actuacion.getArchivos(),
                                fechaFormateada(actuacion.getFechaRegistro())))
                        .build())
                .tareas(transformTareas(actuacion.getTareas())).build();
    }

    private List<DocumentoDetalleResponse> transformDocuments(List<ArchivoAdjunto> archivos,
            String fechaActuacion) {
        return archivos.stream()
                .map(item -> transformDocumentoDetalleResponse(item, fechaActuacion))
                .collect(Collectors.toList());
    }

    private DocumentoDetalleResponse transformDocumentoDetalleResponse(
            ArchivoAdjunto archivoAdjunto, String fechaActuacion) {
        return DocumentoDetalleResponse.builder().id(archivoAdjunto.getId())
                .fechaRegistro(fechaActuacion).nombreArchivo(archivoAdjunto.getNombreArchivo())
                .type(archivoAdjunto.getTipoArchivo()).url("").build();
    }

    private List<TareaDetalleResponse> transformTareas(List<Tarea> tareas) {
        return tareas.stream().map(this::transformFromTarea).collect(Collectors.toList());
    }

    private TareaDetalleResponse transformFromTarea(Tarea tarea) {
        return TareaDetalleResponse.builder().id(tarea.getIdTarea())
                .denominacion(tarea.getDenominacion())
                .fechaVencimiento(fechaFormateada(tarea.getFechaVencimiento()))
                .documentos(transformDocumentoDetalleResponse(tarea.getArchivos(),
                        fechaFormateada(tarea.getFechaRegistro())))
                .build();
    }

    private List<DocumentoDetalleResponse> transformDocumentoDetalleResponse(
            List<ArchivoAdjunto> archivos, String fechaRegistro) {
        return archivos.stream().map(item -> transformDocumentoDetalle(item, fechaRegistro))
                .collect(Collectors.toList());
    }

    private DocumentoDetalleResponse transformDocumentoDetalle(ArchivoAdjunto archivo,
            String fechaRegistro) {
        return DocumentoDetalleResponse.builder().id(archivo.getId())
                .nombreArchivo(archivo.getNombreArchivo()).fechaRegistro(fechaRegistro)
                .type(archivo.getTipoArchivo()).url("").build();
    }
}
