package com.samy.service.app.service.impl;

import static com.samy.service.app.service.impl.ServiceUtils.cantidadDocumentos;
import static com.samy.service.app.service.impl.ServiceUtils.cantidadTareasAVencerDelCaso;
import static com.samy.service.app.service.impl.ServiceUtils.cantidadTareasDelCasoGeneral;
import static com.samy.service.app.service.impl.ServiceUtils.etapaActuacion;
import static com.samy.service.app.service.impl.ServiceUtils.fechaActuacion;
import static com.samy.service.app.service.impl.ServiceUtils.funcionario;
import static com.samy.service.app.service.impl.ServiceUtils.nroOrdenEtapaActuacion;
import static com.samy.service.app.service.impl.ServiceUtils.siguienteVencimientoDelCaso;
import static com.samy.service.app.service.impl.ServiceUtils.tipoActuacion;
import static com.samy.service.app.service.impl.ServiceUtils.totalActuacionesCompletadasGeneral;
import static com.samy.service.app.service.impl.ServiceUtils.totalCasosPorEstado;
import static com.samy.service.app.service.impl.ServiceUtils.totalDocumentosGenerales;
import static com.samy.service.app.service.impl.ServiceUtils.totalDocumentosPendientes;
import static com.samy.service.app.service.impl.ServiceUtils.totalTareasDelCaso;
import static com.samy.service.app.service.impl.ServiceUtils.totalTareasGeneralPorEstado;
import static com.samy.service.app.service.impl.ServiceUtils.totalTareasPorVencerCasos;
import static com.samy.service.app.util.Contants.diasPlazoVencimiento;
import static com.samy.service.app.util.Contants.fechaActual;
import static com.samy.service.app.util.ListUtils.orderByDesc;
import static com.samy.service.app.util.Utils.añoFecha;
import static com.samy.service.app.util.Utils.diaFecha;
import static com.samy.service.app.util.Utils.fechaFormateada;
import static com.samy.service.app.util.Utils.fechaFormateadaYYYMMDD;
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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.samy.service.app.aws.EtapaPojo;
import com.samy.service.app.aws.ExternalDbAws;
import com.samy.service.app.aws.MateriaPojo;
import com.samy.service.app.exception.BadRequestException;
import com.samy.service.app.exception.NotFoundException;
import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.external.FuncionarioDto;
import com.samy.service.app.external.MateriaDto;
import com.samy.service.app.external.SubMateriaDto;
import com.samy.service.app.model.Actuacion;
import com.samy.service.app.model.Caso;
import com.samy.service.app.model.Tarea;
import com.samy.service.app.model.request.ActuacionBody;
import com.samy.service.app.model.request.CasoBody;
import com.samy.service.app.model.request.LambdaMailRequestSendgrid;
import com.samy.service.app.model.request.MateriaRequestUpdate;
import com.samy.service.app.model.request.TareaArchivoBody;
import com.samy.service.app.model.request.TareaBody;
import com.samy.service.app.model.request.TareaCambioEstadoBody;
import com.samy.service.app.model.response.ActuacionResponse;
import com.samy.service.app.model.response.ActuacionResponseX2;
import com.samy.service.app.model.response.ActuacionResponseX3;
import com.samy.service.app.model.response.CriticidadCasosResponse;
import com.samy.service.app.model.response.DetailCaseResponse;
import com.samy.service.app.model.response.DetalleActuacionResponse;
import com.samy.service.app.model.response.HomeCaseResponse;
import com.samy.service.app.model.response.MainActuacionResponse;
import com.samy.service.app.model.response.MateriaResponse;
import com.samy.service.app.model.response.MiCarteraResponse;
import com.samy.service.app.model.response.NotificacionesVencimientosResponse;
import com.samy.service.app.model.response.SubMateriaResponse;
import com.samy.service.app.model.response.UpdateTareaResponse;
import com.samy.service.app.repo.CasoRepo;
import com.samy.service.app.repo.GenericRepo;
import com.samy.service.app.service.CasoService;
import com.samy.service.app.service.LambdaService;
import com.samy.service.app.util.LambdaUtils;
import com.samy.service.app.util.ListPagination;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CasoServiceImpl extends CrudImpl<Caso, String> implements CasoService {

    @Autowired
    private CasoRepo repo;

    @Autowired
    private CasoRequestBuilder builder;

    @Autowired
    private ExternalDbAws externalAws;

    @Autowired
    private LambdaService lambdaService;

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
            throw new NotFoundException("El Caso con el ID : " + id + " no existe");
        }
        return optional.get();
    }

    /**
     * Metodo que registra el caso.
     */
    @Transactional
    @Override
    public Caso registrarCaso(CasoBody request) {
        Caso casoRegistrado = registrar(builder.transformFromBody(request));
        if (casoRegistrado != null) {
            try {
                LambdaUtils util = new LambdaUtils(lambdaService);
                String email;
                email = util.mailGeneradoLambda(casoRegistrado);
                casoRegistrado.setEmailGenerado(email);
                return modificar(casoRegistrado);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return casoRegistrado;
    }

    /**
     * Metodo que registra la actuacion.
     */
    @Transactional
    @Override
    public ActuacionResponseX2 registrarActuacion(ActuacionBody request, String idCaso) {
        Caso caso = verPodId(idCaso);
        return transformMap(registrar(builder.transformActuacion(caso, request)));
    }

    private ActuacionResponseX2 transformMap(Caso caso) {
        List<Actuacion> actuaciones = caso.getActuaciones();
        int ultimoItem = actuaciones.isEmpty() ? 0 : actuaciones.size() - 1;
        return ActuacionResponseX2.builder()
                .id(actuaciones.get(ultimoItem).getIdActuacion())
                .archivos(archivos(actuaciones.get(ultimoItem).getArchivos()))
                .build();
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
        if (caso.getEmailGenerado() == null) {
            try {
                LambdaUtils util = new LambdaUtils(lambdaService);
                String email = util.mailGeneradoLambda(caso);
                log.info("Correo creado con exito " + email);
                caso.setEmailGenerado(email);
                return registrar(builder.transformTarea(caso, request, idActuacion));
            } catch (Exception e) {
                log.error("Error al crear el correo registrando la tarea " + e.getMessage());
            }
        }
        try {
            CompletableFuture<JsonObject> completableFuture = CompletableFuture
                    .supplyAsync(() -> lambdaService.enviarCorreo(
                            LambdaMailRequestSendgrid.builder().content(request.getMensaje())
                                    .emailTo(request.getEquipos().get(0).getCorreo())
                                    .emailFrom(caso.getEmailGenerado())
                                    .subject("Asunto : " + request.getDenominacion()).build()));
            completableFuture.get();
        } catch (Exception e) {
            log.error("Error al enviar correo " + e.getMessage());
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
     * MEtodo para registrar la SubMaterias
     */
    @Override
    public DetailCaseResponse agregarSubMateria(MateriaRequestUpdate request) {
        Caso caso = verPodId(request.getIdCaso());
        List<MateriaDto> materias = caso.getMaterias();
        if (materias != null) {
            materias.clear();
            caso.setMaterias(builder.materiaDtoBuilderList(request.getMaterias()));
        }
        return transformFromCaso(registrar(caso));
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
        double mayor = casos.stream().max(Comparator.comparing(Caso::getMultaPotencial)).get()
                .getMultaPotencial().doubleValue();
        return CriticidadCasosResponse.builder().total(formatMoney(suma.doubleValue()))
                .detalles(transformToMapCritidicidad(casos, mayor)).build();
    }

    @Override
    public List<Map<String, Object>> verCasosPorMateria(String userName) {
        List<Caso> casos = listarCasosPorUserName(userName);
        if (casos.isEmpty()) {
            return new ArrayList<Map<String, Object>>();
        }
        List<String> materias = new ArrayList<String>();
        for (Caso caso : casos) {
            for (MateriaDto materia : caso.getMaterias()) {
                if (!materias.contains(materia.getNombreMateria())) {
                    materias.add(materia.getNombreMateria());
                }
            }
        }
        List<Map<String, Object>> mapResponse = new ArrayList<Map<String, Object>>();
        Map<String, Object> mapItem;
        for (String materia : materias) {
            mapItem = new HashMap<String, Object>();
            List<Map<String, Object>> newCaso = listCasosByMateria(materia, casos);
            mapItem.put("nombreMateria", materia);
            mapItem.put("cantidadCasos", newCaso.size());
            mapItem.put("casos", newCaso);
            mapResponse.add(mapItem);
        }
        mapResponse.sort(Comparator.comparing(m -> (int) m.get("cantidadCasos"),
                Comparator.nullsLast(Comparator.reverseOrder())));
        return mapResponse;
    }

    @Override
    public List<Map<String, Object>> verTotalesCompletados(String userName) {
        List<Caso> casos = listarCasosPorUserName(userName);
        List<Map<String, Object>> lista = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("tipo", "Actuaciones");
        map.put("completadas", totalActuacionesCompletadasGeneral(casos, true));
        map.put("activas", totalActuacionesCompletadasGeneral(casos, false));
        lista.add(map);
        map = new HashMap<String, Object>();
        map.put("tipo", "Documentos");
        map.put("total", totalDocumentosGenerales(casos));
        map.put("pendientes", totalDocumentosPendientes(casos, false));
        lista.add(map);
        map = new HashMap<String, Object>();
        map.put("tipo", "Tareas");
        map.put("completadas", totalTareasGeneralPorEstado(casos, true));
        map.put("pendientes", totalTareasGeneralPorEstado(casos, false));
        map.put("porVencer", totalTareasPorVencerCasos(casos));
        lista.add(map);
        return lista;
    }

    @Override
    public Caso actualizarTarea(TareaBody request, String idActuacion, String idCaso) {
        return null;
    }

    @Override
    public UpdateTareaResponse verTareaPorId(String idCaso, String idActuacion, String idTarea) {
        Caso caso = verPodId(idCaso);
        List<Actuacion> actuaciones = caso.getActuaciones();
        if (actuaciones.isEmpty()) {
            throw new NotFoundException("No hay Actuaciones para el Caso " + idCaso);
        }
        Actuacion actuacion = actuaciones.stream()
                .filter(item -> item.getIdActuacion().equals(idActuacion))
                .collect(Collectors.toList()).get(0);
        List<Tarea> tareas = actuacion.getTareas().stream()
                .filter(tarea -> tarea.getIdTarea().equals(idTarea)).collect(Collectors.toList());
        if (tareas.isEmpty()) {
            throw new NotFoundException("No hay tareas para la Actuacion");
        }
        Tarea tarea = tareas.get(0);
        return UpdateTareaResponse.builder().idTarea(tarea.getIdTarea())
                .denominacion(tarea.getDenominacion()).mensaje(tarea.getMensaje())
                .estado(tarea.getEstado())
                .fechaVencimiento(fechaFormateadaYYYMMDD(tarea.getFechaVencimiento()))
                .equipos(builder.getEquiposBody(tarea.getEquipos()))
                .archivos(builder.listArchivoBody(tarea.getArchivos())).build();
    }

    @Override
    public Caso eliminarTareaPorId(String idCaso, String idActuacion, String idTarea) {
        Caso caso = verPodId(idCaso);
        List<Actuacion> actuaciones = caso.getActuaciones();
        if (actuaciones.isEmpty()) {
            throw new NotFoundException("Este caso no tiene actuaciones");
        }
        Actuacion actuacion = actuaciones.stream()
                .filter(item -> item.getIdActuacion().equals(idActuacion))
                .collect(Collectors.toList()).get(0);
        int index = actuaciones.indexOf(actuacion);
        List<Tarea> tareas = actuacion.getTareas();
        if (tareas.isEmpty()) {
            throw new NotFoundException("Esta actuacion no tiene tareas");
        }
        Tarea tarea = tareas.stream().filter(item -> item.getIdTarea().equals(idTarea))
                .collect(Collectors.toList()).get(0);
        int indexTarea = tareas.indexOf(tarea);
        tareas.get(indexTarea).setEliminado(true);
        actuaciones.get(index).setTareas(tareas);
        caso.setActuaciones(actuaciones);
        return registrar(caso);
    }


    @Override
    public List<ActuacionResponseX3> verActuacionesPorIdCaso(String idCaso) {
        Caso caso = verPodId(idCaso);
        List<Actuacion> actuaciones = caso.getActuaciones();
        return actuaciones.stream().map(item -> transformActuacionResponseX3(item,caso.getUsuario())).collect(Collectors.toList());
    }
    
    private ActuacionResponseX3 transformActuacionResponseX3(Actuacion actuacion, String usuario) {
        List<ArchivoAdjunto> archivos = actuacion.getArchivos();
        String nombreArchivo = archivos.size()>0 ? archivos.get(0).getNombreArchivo(): "";
                return ActuacionResponseX3.builder()
                .idActuacion(actuacion.getIdActuacion())
                .documentoPrincipal(nombreArchivo)
                .fechaActuacion(fechaFormateada(actuacion.getFechaActuacion()))
                .nombreActuacion(actuacion.getDescripcion())
                .descripcion(actuacion.getDescripcionAux())
                .subidoPor(usuario)
                .anexos(0)
                .funcionarios(transformListFuncionarioMap(actuacion.getFuncionario()))
                .vencimientos(transformListVencimientoMap(actuacion.getTareas()))//Asumo que son de las tareas.
                .build();
    }
    
    private List<Map<String, Object>> transformListFuncionarioMap(List<FuncionarioDto> funcionarios){
        return funcionarios.stream().map(this::transformFuncionarioMap).collect(Collectors.toList());
    }
    
    private Map<String, Object> transformFuncionarioMap(FuncionarioDto dto){
        Map<String, Object> mapFuncionario = new HashMap<String, Object>();
        mapFuncionario.put("nombre", dto.getDatosFuncionario());
        mapFuncionario.put("cargo", "Intendente regional");
        mapFuncionario.put("etapa", "Etapa Sancionadora");
        return mapFuncionario;
    }
    
    private List<Map<String, Object>> transformListVencimientoMap(List<Tarea> tareas){
        return tareas.stream().map(this::transformVencimientoMap).collect(Collectors.toList());
    }
    
    private Map<String, Object> transformVencimientoMap(Tarea dto){
        Map<String, Object> mapFuncionario = new HashMap<String, Object>();
        mapFuncionario.put("id", dto.getIdTarea());
        mapFuncionario.put("fecha", fechaFormateada(dto.getFechaVencimiento()));
        mapFuncionario.put("descripcion", "--");
        return mapFuncionario;
    }
    
    private List<Map<String, Object>> listCasosByMateria(String nombreMateria, List<Caso> casos) {
        List<Map<String, Object>> newCaso = new ArrayList<>();
        Map<String, Object> itemCaso;
        for (Caso caso : casos) {
            for (MateriaDto materia : caso.getMaterias()) {
                if (materia.getNombreMateria().equals(nombreMateria)) {
                    itemCaso = new HashMap<String, Object>();
                    itemCaso.put("nombreCaso", caso.getDescripcionCaso());
                    itemCaso.put("ordenInspeccion", caso.getOrdenInspeccion());
                    itemCaso.put("materiasEspecificas", new ArrayList<>());
                    newCaso.add(itemCaso);
                }
            }
        }
        return newCaso.stream().distinct().collect(Collectors.toList());
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
        for (EtapaPojo etapa : externalAws.getTableEtapa().stream()
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
            if (tarea.getEliminado() == null) {

            } else if (tarea.getEliminado() == false) {
                List<ArchivoAdjunto> archivos = tarea.getArchivos();
                if (archivos == null) {
                    return 0;
                }
                if (archivos.size() > 0) {
                    int total = (int) archivos.stream().filter(item -> !item.getEstado()).count();
                    contador = contador + total;
                }
            }
        }
        return contador;
    }

    private List<DetalleActuacionResponse> transformDetalleActuacionResponse(List<Tarea> tareas) {
        List<DetalleActuacionResponse> detalle = new ArrayList<DetalleActuacionResponse>();
        for (Tarea tare : tareas) {
            if (tare.getEliminado() == null) {
                tare.setEliminado(false);
                detalle.add(transformFromTarea(tare));
            } else if (!tare.getEliminado()) {
                detalle.add(transformFromTarea(tare));
            }
        }
        return detalle;
    }

    private DetalleActuacionResponse transformFromTarea(Tarea tarea) {
        log.info(tarea.toString());
        return DetalleActuacionResponse.builder().idTarea(tarea.getIdTarea())
                .nombreTarea(tarea.getDenominacion())
                .cantidadDocumentos(tarea == null || tarea.getArchivos() == null ? 0
                        : tarea.getArchivos().size())
                .equipos(builder.getEquiposString(tarea.getEquipos()))
                .fechaRegistro(fechaFormateada(tarea.getFechaRegistro()))
                .fechaVencimiento(fechaFormateada(tarea.getFechaVencimiento()))
                .estado(tarea.getEstado()).build();
    }

    @SuppressWarnings("unchecked")
    private DetailCaseResponse transformFromCaso(Caso caso) {
        FuncionarioDto dtoFunci = funcionario(caso.getActuaciones());
        Map<String, Object> mapInfraccion = externalAws.tableInfraccion(caso.getId());
        Integer trabInvolucrados = null;
        Double sumaPotencial = null;
        Double sumaProvision = null;
        Integer totalMaterias = 0;
        Integer totalSubMaterias = 0;
        Map<String, Object> mapRiesgo = null;
        Map<String, Object> mapOrigen = null;
        if (!mapInfraccion.isEmpty()) {
            trabInvolucrados = Integer
                    .parseInt(mapInfraccion.get("cantidadInvolucrados").toString());
            sumaPotencial = Double.parseDouble(mapInfraccion.get("sumaMultaPotencial").toString());
            sumaProvision = Double.parseDouble(mapInfraccion.get("sumaProvision").toString());
            mapRiesgo = (Map<String, Object>) mapInfraccion.get("nivelRiesgo");
            mapOrigen = (Map<String, Object>) mapInfraccion.get("origenCaso");
        }
        List<MateriaResponse> materias = materiasResponseBuild(transformToDto(caso.getMaterias()),
                subMateriasBuild(caso.getMaterias()));
        totalMaterias = materias.size();
        for (MateriaResponse response : materias) {
            totalSubMaterias = totalSubMaterias + response.getSubMaterias().size();
        }
        return DetailCaseResponse.builder().idCaso(caso.getId())
                .nombreCaso(caso.getDescripcionCaso()).descripcion(caso.getDescripcionAdicional())
                .fechaCreacion(fechaFormateada(caso.getFechaInicio()))
                .ordenInspeccion(caso.getOrdenInspeccion())
                .tipoActuacion(tipoActuacion(caso.getActuaciones()))
                .cantidadDocumentos(cantidadDocumentos(caso.getActuaciones()))
                .idFuncionario(dtoFunci.getId()).funcionario(dtoFunci.getDatosFuncionario())
                .trabajadoresInvolucrados(trabInvolucrados).sumaMultaPotencial(sumaPotencial)
                .sumaProvision(sumaProvision).riesgo(mapRiesgo).origen(mapOrigen)
                .materiasResponse(materias)
                .totalMaterias(totalMaterias)
                .totalSubMaterias(totalSubMaterias)
                .build();
    }

    private List<MateriaResponse> materiasResponseBuild(List<MateriaPojo> materias,
            List<SubMateriaDto> subMaterias) {
        List<MateriaResponse> materiasResponse = new ArrayList<MateriaResponse>();
        for (MateriaPojo dto : materias) {
            materiasResponse.add(MateriaResponse.builder().idMateria(dto.getIdMateria())
                    .nombreMateria(dto.getNombreMateria())
                    .icono(dto.getIcono())
                    .color(dto.getColor())
                    .subMaterias(subMaterias.stream().parallel()
                            .filter(item -> item.getIdMateria().equals(dto.getIdMateria()))
                            .map(this::transformSubMateriaResponse).collect(Collectors.toList()))
                    .build());
        }
        return materiasResponse;
    }

    private SubMateriaResponse transformSubMateriaResponse(SubMateriaDto subMateriaDto) {
        return SubMateriaResponse.builder().idSubMateria(subMateriaDto.getIdSubMateria())
                .nombreSubMAteria(subMateriaDto.getNombreSubMateria()).build();
    }

    private List<SubMateriaDto> subMateriasBuild(List<MateriaDto> materias) {
        List<SubMateriaDto> items = new ArrayList<SubMateriaDto>();
        for (MateriaDto materia : materias) {
            List<SubMateriaDto> subMaterias = materia.getSubMaterias() != null
                    ? materia.getSubMaterias()
                    : new ArrayList<SubMateriaDto>();
            MateriaPojo materiaPojo = externalAws.getTable(materia.getId());
            for (SubMateriaDto dto : subMaterias) {
                items.add(SubMateriaDto.builder().idSubMateria(dto.getIdSubMateria())
                        .idMateria(dto.getIdMateria()).icono(materiaPojo.getIcono())
                        .color(materiaPojo.getColor()).nombreSubMateria(dto.getNombreSubMateria())
                        .build());
            }
        }
        return items;
    }

    private List<MateriaPojo> transformToDto(List<MateriaDto> materias) {
        return materias.stream().parallel().map(this::transformMateriaPojo)
                .collect(Collectors.toList());
    }

    private MateriaPojo transformMateriaPojo(MateriaDto materiaDto) {
        MateriaPojo materia = externalAws.getTable(materiaDto.getId());
        return MateriaPojo.builder().idMateria(materiaDto.getId())
                .nombreMateria(materia.getNombreMateria()).color(materia.getColor())
                .icono(materia.getIcono()).estado(materia.getEstado()).build();
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
                .tareasPendientes(totalTareasDelCaso(caso, false))
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
