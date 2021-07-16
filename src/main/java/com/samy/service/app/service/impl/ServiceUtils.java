package com.samy.service.app.service.impl;

import static com.samy.service.app.util.Utils.fechaFormateada;

import java.util.List;

import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.model.Actuacion;
import com.samy.service.app.model.Caso;
import com.samy.service.app.model.Tarea;

public class ServiceUtils {

    /**
     * Cantidad de documentos de la ultima actuacion.
     * 
     * @param actuaciones
     * @return
     */
    public static Integer cantidadDocumentos(List<Actuacion> actuaciones) {
        if (actuaciones == null) {
            return 0;
        }
        if (actuaciones.isEmpty()) {
            return 0;
        }
        List<ArchivoAdjunto> archivos = actuaciones.get(actuaciones.size() - 1).getArchivos();
        return archivos != null ? archivos.size() : 0;
    }

    /**
     * Ultima etapa de la Actuacion
     * 
     * @param actuaciones
     * @return
     */
    public static String etapaActuacion(List<Actuacion> actuaciones) {
        return actuaciones.isEmpty() ? " --- "
                : actuaciones.get(actuaciones.size() - 1).getEtapa().getNombreEtapa();
    }

    /**
     * Ultima fecha de Actuacion
     * 
     * @param actuaciones
     * @return
     */
    public static String fechaActuacion(List<Actuacion> actuaciones) {
        return actuaciones.isEmpty() ? " --- "
                : fechaFormateada(actuaciones.get(actuaciones.size() - 1).getFechaActuacion());
    }

    /**
     * Ultima Tipo de Actuacion
     * 
     * @param actuaciones
     * @return
     */
    public static String tipoActuacion(List<Actuacion> actuaciones) {
        if (actuaciones == null) {
            return " --- ";
        }
        return actuaciones.isEmpty() ? " --- "
                : actuaciones.get(actuaciones.size() - 1).getTipoActuacion()
                        .getNombreTipoActuacion();
    }

    /**
     * Nombre de Funcionario de ultima actuacion.
     * 
     * @param actuaciones
     * @return
     */
    public static String funcionario(List<Actuacion> actuaciones) {
        if (actuaciones.isEmpty()) {
            return " --- ";
        }
        int actuacionesSize = actuaciones.size();
        Actuacion actuacion = actuaciones.get(actuacionesSize - 1);
        int funcionariosSize = actuacion.getFuncionario().size();
        return funcionariosSize > 0
                ? actuacion.getFuncionario().get(funcionariosSize - 1).getDatosFuncionario()
                : " --- ";
    }
    
    /**
     * Cantidad de documentos icluidas actuaciones y tareas del caso.
     * @param caso
     * @return
     */
    public static Integer cantidadDocumentosGenerales(Caso caso) {
        int contadorDocActuacion = 0;
        int contadorDocTareas = 0;
        for (Actuacion actuacion : caso.getActuaciones()) {
            contadorDocActuacion = contadorDocActuacion + actuacion.getArchivos().size();
            for (Tarea tarea : actuacion.getTareas()) {
                contadorDocTareas = contadorDocTareas + tarea.getArchivos().size();
            }
        }
        return contadorDocActuacion + contadorDocTareas;
    }
    
    /**
     * Cantidad de tareas pendienes de todas las actuaciones del caso.
     * @param caso
     * @return
     */
    public static Integer cantidadTareasPendientesGeneral(Caso caso) {
        int contadorPendientes = 0;
        for (Actuacion actuacion : caso.getActuaciones()) {
            for (Tarea tarea : actuacion.getTareas()) {
                contadorPendientes = contadorPendientes + (tarea.getEstado() ? 0 : 1);
            }
        }
        return contadorPendientes;
    }
    
    /**
     * Cuenta la cantidad de tareas realizadas(true) o pendientes (false) de manera individual.
     * @param tareas
     * @param estado
     * @return
     */
    public static Integer cantidadTareasIndividualPorEstado(List<Tarea> tareas, Boolean estado) {
        Long cantidad = tareas.stream().filter(tarea -> estado).count();
        return cantidad.intValue();
    }

}
