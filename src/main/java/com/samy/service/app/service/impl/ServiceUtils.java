package com.samy.service.app.service.impl;

import static com.samy.service.app.util.Contants.diasPlazoVencimiento;
import static com.samy.service.app.util.Contants.fechaActual;
import static com.samy.service.app.util.Utils.fechaFormateada;

import java.time.LocalDate;
import java.util.List;

import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.external.FuncionarioDto;
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
     * Nro Orden etapa de la Actuacion
     * 
     * @param actuaciones
     * @return
     */
    public static Integer nroOrdenEtapaActuacion(List<Actuacion> actuaciones) {
        return actuaciones.isEmpty() ? 0
                : actuaciones.get(actuaciones.size() - 1).getEtapa().getNroOrden();
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
    public static FuncionarioDto funcionario(List<Actuacion> actuaciones) {
        if (actuaciones.isEmpty()) {
            return new FuncionarioDto();
        }
        int actuacionesSize = actuaciones.size();
        Actuacion actuacion = actuaciones.get(actuacionesSize - 1);
        int funcionariosSize = actuacion.getFuncionario().size();
        FuncionarioDto funcionario = actuacion.getFuncionario().get(0);
        return funcionariosSize > 0
                ? new FuncionarioDto(funcionario.getId(), funcionario.getDatosFuncionario())
                : new FuncionarioDto();
    }

    /**
     * Contador de tareas General del Caso
     * 
     * @param caso
     * @return
     */
    public static Integer cantidadTareasDelCasoGeneral(Caso caso) {
        int contadorTareas = 0;
        for (Actuacion actuacion : caso.getActuaciones()) {
            for (@SuppressWarnings("unused")
            Tarea tarea : actuacion.getTareas()) {
                contadorTareas++;
            }
        }
        return contadorTareas;
    }

    /**
     * Cantidad de documentos icluidas actuaciones y tareas del caso.
     * 
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
     * 
     * @param caso
     * @return
     */
    public static Integer totalTareasDelCaso(Caso caso, Boolean estado) {
        int contadorPendientes = 0;
        for (Actuacion actuacion : caso.getActuaciones()) {
            for (Tarea tarea : actuacion.getTareas()) {
                contadorPendientes = contadorPendientes + (tarea.getEstado() == estado ? 0 : 1);
            }
        }
        return contadorPendientes;
    }

    /**
     * Cuenta la cantidad de tareas realizadas(true) o pendientes (false) de manera
     * individual.
     * 
     * @param tareas
     * @param estado
     * @return
     */
    public static Integer cantidadTareasIndividualPorEstado(List<Tarea> tareas, Boolean estado) {
        Long cantidad = tareas.stream().filter(tarea -> estado).count();
        return cantidad.intValue();
    }

    /**
     * CAntidad de tareas a vencer del Caso.
     * 
     * @param caso
     * @return
     */
    public static Integer cantidadTareasAVencerDelCaso(Caso caso) {
        int contadorTareasAVencer = 0;
        for (Actuacion actuacion : caso.getActuaciones()) {
            for (Tarea tarea : actuacion.getTareas()) {
                LocalDate fechaVencimiento = tarea.getFechaVencimiento().toLocalDate();
                if (fechaVencimiento.isAfter(fechaActual)
                        && fechaVencimiento.isBefore(fechaActual.plusDays(diasPlazoVencimiento))) {
                    contadorTareasAVencer++;
                }
            }
        }
        return contadorTareasAVencer;
    }

    /**
     * Muestra la fecha de vencimiento de la ultima tarea activa.
     * 
     * @param actuaciones
     * @return
     */
    public static String siguienteVencimientoDelCaso(List<Actuacion> actuaciones) {
        if (!actuaciones.isEmpty()) {
            int cantidadActuaciones = actuaciones.size();
            Actuacion ultimaActuacion = actuaciones.get(cantidadActuaciones - 1);
            List<Tarea> tareas = ultimaActuacion.getTareas();
            if (!tareas.isEmpty()) {
                int cantidadTareas = tareas.size();
                Tarea tarea = tareas.get(cantidadTareas - 1);
                return fechaFormateada(tarea.getFechaVencimiento());
            }
        }
        return " --- ";
    }

    /**
     * Contador de casos por estado. (ACtivos o inactivos)
     * 
     * @param casos
     * @param estado
     * @return
     */
    public static int totalCasosPorEstado(List<Caso> casos, Boolean estado) {
        return (int) casos.stream().filter(caso -> caso.getEstadoCaso() == estado).count();
    }

    /**
     * Total de actuaciones completadas de los casos.
     * 
     * @param casos
     * @return
     */
    public static int totalActuacionesCompletadasGeneral(List<Caso> casos, Boolean estado) {
        int totalCompletas = 0;
        for (Caso caso : casos) {
            int cantidad = 0;
            for (Actuacion actuacion : caso.getActuaciones()) {
                cantidad = cantidad + (int) actuacion.getTareas().stream()
                        .filter(tarea -> tarea.getEstado() == estado).count();
                if (cantidad > 0) {
                    totalCompletas++;
                }
            }
        }
        return totalCompletas;
    }

    /**
     * Contador de total de documentos Existentes en todos los casos
     * 
     * @param casos
     * @return
     */
    public static int totalDocumentosGenerales(List<Caso> casos) {
        int contadorTotal = 0;
        for (Caso caso : casos) {
            int contadorPorCaso = cantidadDocumentosGenerales(caso);
            contadorTotal = contadorTotal + contadorPorCaso;
        }
        return contadorTotal;
    }

    /**
     * Total de documentos segun el estado de todos los casos
     * 
     * @param casos
     * @param estado
     * @return
     */
    public static int totalDocumentosPendientes(List<Caso> casos, Boolean estado) {
        int contador = 0;
        for (Caso caso : casos) {
            for (Actuacion actuacion : caso.getActuaciones()) {
                for (Tarea tarea : actuacion.getTareas()) {
                    if (tarea.getEstado() == estado) {
                        contador++;
                    }
                }
            }
        }
        return contador;
    }

    /**
     * Contador del total General de tareas de todos los casos por estado.
     * 
     * @param casos
     * @param estado
     * @return
     */
    public static int totalTareasGeneralPorEstado(List<Caso> casos, Boolean estado) {
        int totalGeneral = 0;
        for (Caso caso : casos) {
            int total = totalTareasDelCaso(caso, estado);
            totalGeneral = totalGeneral + total;
        }
        return totalGeneral;
    }

    /**
     * Total de tareas por vencer de todos los casos.
     * 
     * @return
     */
    public static int totalTareasPorVencerCasos(List<Caso> casos) {
        int contadorTotal = 0;
        for (Caso caso : casos) {
            int totalTareaCaso = cantidadTareasAVencerDelCaso(caso);
            contadorTotal = contadorTotal + totalTareaCaso;
        }
        return contadorTotal;
    }

}
