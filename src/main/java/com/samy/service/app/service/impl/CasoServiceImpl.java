package com.samy.service.app.service.impl;

import static com.samy.service.app.service.impl.ServiceUtils.cantidadDocumentos;
import static com.samy.service.app.service.impl.ServiceUtils.cantidadTareasAVencerDelCaso;
import static com.samy.service.app.service.impl.ServiceUtils.cantidadTareasDelCasoGeneral;
import static com.samy.service.app.service.impl.ServiceUtils.cantidadTareasPendientesGeneral;
import static com.samy.service.app.service.impl.ServiceUtils.etapaActuacion;
import static com.samy.service.app.service.impl.ServiceUtils.fechaActuacion;
import static com.samy.service.app.service.impl.ServiceUtils.funcionario;
import static com.samy.service.app.service.impl.ServiceUtils.nroOrdenEtapaActuacion;
import static com.samy.service.app.service.impl.ServiceUtils.siguienteVencimientoDelCaso;
import static com.samy.service.app.service.impl.ServiceUtils.tipoActuacion;
import static com.samy.service.app.service.impl.ServiceUtils.totalCasosPorEstado;
import static com.samy.service.app.util.Contants.diasPlazoVencimiento;
import static com.samy.service.app.util.Contants.fechaActual;
import static com.samy.service.app.util.ListUtils.orderByDesc;
import static com.samy.service.app.util.Utils.añoFecha;
import static com.samy.service.app.util.Utils.diaFecha;
import static com.samy.service.app.util.Utils.fechaFormateada;
import static com.samy.service.app.util.Utils.formatMoney;
import static com.samy.service.app.util.Utils.getExtension;
import static com.samy.service.app.util.Utils.getPorcentaje;
import static com.samy.service.app.util.Utils.mesFecha;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samy.service.app.aws.EtapaPojo;
import com.samy.service.app.aws.ExternalDbAws;
import com.samy.service.app.aws.MateriaPojo;
import com.samy.service.app.exception.BadRequestException;
import com.samy.service.app.exception.NotFoundException;
import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.external.MateriasDto;
import com.samy.service.app.model.Actuacion;
import com.samy.service.app.model.Caso;
import com.samy.service.app.model.Tarea;
import com.samy.service.app.model.request.ActuacionBody;
import com.samy.service.app.model.request.CasoBody;
import com.samy.service.app.model.request.TareaArchivoBody;
import com.samy.service.app.model.request.TareaBody;
import com.samy.service.app.model.request.TareaCambioEstadoBody;
import com.samy.service.app.model.response.ActuacionResponse;
import com.samy.service.app.model.response.CriticidadCasosResponse;
import com.samy.service.app.model.response.DetailCaseResponse;
import com.samy.service.app.model.response.DetalleActuacionResponse;
import com.samy.service.app.model.response.HomeCaseResponse;
import com.samy.service.app.model.response.MainActuacionResponse;
import com.samy.service.app.model.response.MiCarteraResponse;
import com.samy.service.app.model.response.NotificacionesVencimientosResponse;
import com.samy.service.app.repo.CasoRepo;
import com.samy.service.app.repo.GenericRepo;
import com.samy.service.app.service.CasoService;
import com.samy.service.app.util.ListPagination;

@Service
public class CasoServiceImpl extends CrudImpl<Caso, String> implements CasoService {

    @Autowired
    private CasoRepo repo;

    @Autowired
    private CasoRequestBuilder builder;

    @Autowired
    private ExternalDbAws materiaAws;

    @Override
    protected GenericRepo<Caso, String> getRepo() {

        return repo;
    }

    /**
     * Metodo que sirve para visualizar el caso por ID
     */
    @Override
    public Caso verPodId(String id) {
        Optional<Caso> optional = verPorId(id);
        if (!optional.isPresent()) {
            throw new NotFoundException("El Caso con el ID : " + id + "no existe");
        }
        return optional.isPresent() ? optional.get() : new Caso();
    }

    /**
     * Metodo que registra el caso.
     */
    @Override
    public Caso registrarCaso(CasoBody request) {
        return registrar(builder.transformFromBody(request));
    }

    /**
     * Metodo que registra la actuacion.
     */
    @Transactional
    @Override
    public Map<String, Object> registrarActuacion(ActuacionBody request, String idCaso) {
        Caso caso = verPodId(idCaso);
        if (caso == null) {
            throw new NotFoundException("El Caso con el ID : " + idCaso + "no existe");
        }
        return transformMap(registrar(builder.transformActuacion(caso, request)));
    }

    private Map<String, Object> transformMap(Caso caso) {
        Map<String, Object> newMap = new HashMap<String, Object>();
        List<Actuacion> actuaciones = caso.getActuaciones();
        int ultimoItem = actuaciones.isEmpty() ? 0 : actuaciones.size() - 1;
        newMap.put("id", actuaciones.get(ultimoItem).getIdActuacion());
        newMap.put("archivos", archivos(actuaciones.get(ultimoItem).getArchivos()));
        return newMap;
    }

    private Map<String, Object> transformMapTarea(Caso caso, TareaArchivoBody tareaArchivoBody) {
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
                .id(archivo.getId().concat(getExtension(archivo.getNombreArchivo())))
                .nombreArchivo(archivo.getNombreArchivo()).tipoArchivo(archivo.getTipoArchivo())
                .build();
    }

    /**
     * Metodo que registra la tarea.
     */
    @Override
    public Caso registrarTarea(TareaBody request, String idActuacion, String idCaso) {
        Caso caso = verPodId(idCaso);
        if (caso == null) {
            throw new NotFoundException("El Caso con el ID : " + idCaso + " no existe");
        }
        return registrar(builder.transformTarea(caso, request, idActuacion));
    }

    /**
     * Metodo que registra el archivo de una tarea ya registrada previamente.
     */
    @Override
    public Map<String, Object> registrarArchivoTarea(TareaArchivoBody tareaArchivoBody) {
        Caso caso = verPodId(tareaArchivoBody.getId_caso());
        return transformMapTarea(registrar(builder.transformUpdateTarea(caso, tareaArchivoBody)),
                tareaArchivoBody);
    }

    /**
     * Metodo que cambia es estado de una Tarea
     */
    @Override
    public Boolean cambiarEstadoTarea(TareaCambioEstadoBody tareaCambioEstadoBody) {
        Caso caso = verPodId(tareaCambioEstadoBody.getId_caso());
        return registrar(builder.transformCambioEstadoTarea(caso, tareaCambioEstadoBody))
                .getId() != null;
    }

    /**
     * Metodo que lista los cassos correspondientes a un usuario de la BD.
     */
    @Override
    public List<Caso> listarCasosPorUserName(String userName) {
        return repo.findByUsuario(userName);
    }

    /**
     * Metodo que lista Casos por nombre de usuario y FEcha de inicio como
     * parametros.
     */
    @Override
    public List<Caso> listarCasosPorUserNameYFechaInicio(String userName, LocalDate fechaInicio) {
        return repo.findByUsuarioAndFechaInicio(userName, fechaInicio);
    }

    /**
     * MEtodo que lista los casos por nombre de usuario, pero para poder verlo en el
     * Front.
     */
    @Override
    public List<HomeCaseResponse> listadoDeCasosPorUserName(String userName, Integer pageNumber,
            Integer pageSize) {
        List<Caso> lista = listarCasosPorUserName(userName);
        return ListPagination.getPage(
                orderByDesc(
                        lista.stream().map(this::transformToHomeCase).collect(Collectors.toList())),
                pageNumber, pageSize);
    }

    /**
     * Metodo que muestra el detalle de un caso en especifico
     */
    @Override
    public DetailCaseResponse mostratDetalleDelCasoPorId(String idCaso) {
        return transformFromCaso(verPodId(idCaso));
    }

    /**
     * Metodo que lista las Notificacion y Vencimientos
     */
    @Override
    public List<NotificacionesVencimientosResponse> listarNotificacionesVencimientos(
            String userName) {
        /*
         * return listarCasosPorUserName(userName).stream()
         * .map(this::transformNotificacionesVencimientosResponse)
         * .collect(Collectors.toList());
         */
        return test(listarCasosPorUserName(userName));
    }

    /**
     * Metodo que lista las actuaciones correspondientes a un solo Caso.
     */
    @Override
    public List<MainActuacionResponse> listarActuacionesPorCaso(String idCaso) {
        return transformMainActuacion(verPodId(idCaso).getActuaciones());
    }

    @Override
    public MiCarteraResponse verCarteraResponse(String userName) {
        List<Caso> casos = listarCasosPorUserName(userName);
        int totalCasos = casos.size();
        int totalCasosActivos = totalCasosPorEstado(casos, true);
        return MiCarteraResponse.builder().hasta(fechaFormateada(fechaActual))
                .casosActivos(String.valueOf(totalCasosActivos))
                .casosRegistrados(String.valueOf(totalCasos)).etapas(transformToMap(casos)).build();
    }

    @Override
    public CriticidadCasosResponse verCriticidadResponse(String userName) {
        List<Caso> casos = listarCasosPorUserName(userName);
        BigDecimal suma = casos.stream().map(item -> item.getMultaPotencial())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CriticidadCasosResponse.builder().total(formatMoney(suma.doubleValue()))
                .detalles(transformToMapCritidicidad(casos, suma.doubleValue())).build();
    }

    private List<Map<String, Object>> transformToMapCritidicidad(List<Caso> casos, double suma) {
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

    private List<Map<String, Object>> transformToMap(List<Caso> casos) {
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        Map<String, Object> mapa;
        for (EtapaPojo etapa : materiaAws.getTableEtapa().stream()
                .sorted(Comparator.comparing(EtapaPojo::getNroOrden))
                .collect(Collectors.toList())) {
            mapa = new HashMap<String, Object>();
            String idEtapa = etapa.getId_etapa();
            int contadorCasos = cuentaCasos(idEtapa, casos);
            mapa.put("nombreEtapa", etapa.getNombreEtapa());
            mapa.put("cantidad", contadorCasos);
            mapa.put("porcentaje", getPorcentaje(contadorCasos, casos.size()));
            listMap.add(mapa);
        }
        return listMap;
    }

    private int cuentaCasos(String idEtapa, List<Caso> casos) {
        int contadorCasos = 0;
        for (Caso caso : casos) {
            int contadorActuaciones = 0;
            for (Actuacion actuacion : caso.getActuaciones()) {
                if (actuacion.getEtapa().getId().equals(idEtapa)) {
                    contadorActuaciones++;
                }
            }
            if (contadorActuaciones > 0) {
                contadorCasos++;
            }
        }
        return contadorCasos;
    };

    private List<MainActuacionResponse> transformMainActuacion(List<Actuacion> actuaciones) {
        List<MainActuacionResponse> mainActuaciones = new ArrayList<MainActuacionResponse>();
        Map<Object, List<Actuacion>> actuMap = actuaciones.stream().collect(
                Collectors.groupingBy(actuacion -> añoFecha(actuacion.getFechaActuacion())));
        for (Map.Entry<Object, List<Actuacion>> entry : actuMap.entrySet()) {
            mainActuaciones.add(new MainActuacionResponse(String.valueOf(entry.getKey()),
                    getListActuacionResponse(entry.getValue())));
        }
        return mainActuaciones;
    }

    public List<ActuacionResponse> getListActuacionResponse(List<Actuacion> actuaciones) {
        List<ActuacionResponse> lista = new ArrayList<ActuacionResponse>();
        int contador = 0;
        for (Actuacion actuacion : actuaciones) {
            ActuacionResponse actuacionResponse = transformDetalle(actuacion);
            actuacionResponse.setIsOpen(contador == 0);
            lista.add(actuacionResponse);
            contador++;
        }
        return lista;
    }

    private ActuacionResponse transformDetalle(Actuacion actuacion) {
        return ActuacionResponse.builder().idActuacion(actuacion.getIdActuacion())
                .dia(diaFecha(actuacion.getFechaActuacion()))
                .mes(mesFecha(actuacion.getFechaActuacion()))
                .tipo(actuacion.getTipoActuacion().getNombreTipoActuacion())
                .etapa(actuacion.getEtapa().getNombreEtapa())
                .descripcionActuacion(actuacion.getDescripcion())
                .totalDocumentosActuacion(
                        actuacion.getArchivos() == null ? 0 : actuacion.getArchivos().size())
                .totalDocumentosTareas(countDocumentosDeTareas(actuacion.getTareas()))
                .totalTareasRealizadas(countTareasRealizadas(actuacion.getTareas()).intValue())
                .detalles(transformDetalleActuacionResponse(actuacion.getTareas())).build();
    }

    private Long countTareasRealizadas(List<Tarea> tareas) {
        return tareas.stream().filter(estado -> estado.getEstado()).count();
    }

    private Integer countDocumentosDeTareas(List<Tarea> tareas) {
        int contador = 0;
        for (Tarea tarea : tareas) {
            contador = contador + (tarea.getArchivos() == null ? 0 : tarea.getArchivos().size());
        }
        return contador;
    }

    private List<DetalleActuacionResponse> transformDetalleActuacionResponse(List<Tarea> tareas) {
        List<DetalleActuacionResponse> detalle = new ArrayList<DetalleActuacionResponse>();
        for (Tarea tare : tareas) {
            detalle.add(transformFromTarea(tare));
        }
        return detalle;
    }

    private DetalleActuacionResponse transformFromTarea(Tarea tarea) {
        return DetalleActuacionResponse.builder().idTarea(tarea.getIdTarea())
                .nombreTarea(tarea.getDenominacion())
                .cantidadDocumentos(tarea == null || tarea.getArchivos() == null ? 0
                        : tarea.getArchivos().size())
                .equipos(builder.getEquiposString(tarea.getEquipos()))
                .fechaRegistro(fechaFormateada(tarea.getFechaRegistro()))
                .fechaVencimiento(fechaFormateada(tarea.getFechaVencimiento()))
                .estado(tarea.getEstado()).build();
    }

    private DetailCaseResponse transformFromCaso(Caso caso) {
        return DetailCaseResponse.builder().idCaso(caso.getId())
                .nombreCaso(caso.getDescripcionCaso()).descripcion(caso.getDescripcionAdicional())
                .fechaCreacion(fechaFormateada(caso.getFechaInicio()))
                .ordenInspeccion(caso.getOrdenInspeccion())
                .materias(transformToDto(caso.getMaterias()))
                .tipoActuacion(tipoActuacion(caso.getActuaciones()))
                .cantidadDocumentos(cantidadDocumentos(caso.getActuaciones()))
                .funcionario(funcionario(caso.getActuaciones())).build();
    }

    private List<MateriaPojo> transformToDto(List<MateriasDto> materias) {
        List<MateriaPojo> materiasBd = new ArrayList<MateriaPojo>();
        for (MateriasDto materia : materias) {
            materiasBd.add(materiaAws.getTable(materia.getId()));
        }
        return materiasBd;
    }

    private HomeCaseResponse transformToHomeCase(Caso caso) {
        return HomeCaseResponse.builder().idCaso(caso.getId())
                .fechaInicio(fechaFormateada(caso.getFechaInicio()))
                .nroOrden(nroOrdenEtapaActuacion(caso.getActuaciones()))
                .etapaActuacion(etapaActuacion(caso.getActuaciones())).riesgo(null)
                .nombreCaso(caso.getDescripcionCaso()).ordenInspeccion(caso.getOrdenInspeccion())
                .utltimaActuacion(fechaActuacion(caso.getActuaciones()))
                .tipoActuacion(tipoActuacion(caso.getActuaciones()))
                .totalTareas(cantidadTareasDelCasoGeneral(caso))
                .tareasPendientes(cantidadTareasPendientesGeneral(caso))
                .aVencer(cantidadTareasAVencerDelCaso(caso))
                .siguienteVencimiento(siguienteVencimientoDelCaso(caso.getActuaciones()))
                .iconoCampana(0).build();
    }

    public int getIndexActuacion(String idActuacion, List<Actuacion> actuaciones) {
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

    public int getIndexTarea(String idTarea, List<Tarea> tareas) {
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

    private List<NotificacionesVencimientosResponse> test(List<Caso> casos) {
        List<Caso> listaCaso = casos;
        List<NotificacionesVencimientosResponse> notiVenci = new ArrayList<NotificacionesVencimientosResponse>();
        for (Caso caso : listaCaso) {
            List<Actuacion> actuaciones = caso.getActuaciones();
            for (Actuacion actuacion : actuaciones) {
                List<Tarea> tareas = actuacion.getTareas();
                for (Tarea tarea : tareas) {
                    LocalDate fechaVencimiento = tarea.getFechaVencimiento().toLocalDate();
                    LocalDate fechaAumentada = fechaActual.plusDays(diasPlazoVencimiento);
                    if (fechaVencimiento.isAfter(fechaActual)
                            && fechaVencimiento.isBefore(fechaAumentada)) {
                        notiVenci.add(NotificacionesVencimientosResponse.builder()
                                .idCaso(caso.getId()).idActuacion(actuacion.getIdActuacion())
                                .idTarea(tarea.getIdTarea())
                                .fechaVencimiento(fechaFormateada(tarea.getFechaVencimiento()))
                                .nombreCaso(caso.getDescripcionCaso()).descripcion(getObject(tarea))
                                .build());
                    }
                }
            }
        }
        return notiVenci.stream()
                .sorted(Comparator
                        .comparing(NotificacionesVencimientosResponse::getFechaVencimiento))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getObject(Tarea tarea) {
        Map<String, Object> lista = new HashMap<String, Object>();
        lista.put("cabecera", "Tarea por vencer");
        lista.put("contenido", tarea.getDenominacion());
        return lista;
    }
}
