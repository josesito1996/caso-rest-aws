package com.samy.service.app.service.impl;

import static com.samy.service.app.util.ListUtils.orderByDesc;
import static com.samy.service.app.util.Utils.añoFecha;
import static com.samy.service.app.util.Utils.diaFecha;
import static com.samy.service.app.util.Utils.fechaFormateada;
import static com.samy.service.app.util.Utils.mesFecha;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samy.service.app.aws.MateriaDbAws;
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
import com.samy.service.app.model.response.ActuacionResponse;
import com.samy.service.app.model.response.DetailCaseResponse;
import com.samy.service.app.model.response.DetalleActuacionResponse;
import com.samy.service.app.model.response.HomeCaseResponse;
import com.samy.service.app.model.response.MainActuacionResponse;
import com.samy.service.app.repo.CasoRepo;
import com.samy.service.app.repo.GenericRepo;
import com.samy.service.app.service.CasoService;

@Service
public class CasoServiceImpl extends CrudImpl<Caso, String> implements CasoService {

    @Autowired
    private CasoRepo repo;

    @Autowired
    private CasoRequestBuilder builder;

    @Autowired
    private MateriaDbAws materiaAws;

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
    public Caso registrarActuacion(ActuacionBody request, String idCaso) {
        Caso caso = verPodId(idCaso);
        return registrar(builder.transformActuacion(caso, request));
    }

    /**
     * Metodo que registra la tarea.
     */
    @Override
    public Caso registrarTarea(TareaBody request, String idActuacion, String idCaso) {
        Caso caso = verPodId(idCaso);
        return registrar(builder.transformTarea(caso, request, idActuacion));
    }

    /**
     * Metodo que registra el archivo de una tarea ya registrada previamente.
     */
    @Override
    public Caso registrarArchivoTarea(TareaArchivoBody tareaArchivoBody) {
        Caso caso = verPodId(tareaArchivoBody.getId_caso());
        return registrar(builder.transformUpdateTarea(caso, tareaArchivoBody));
    }

    /**
     * Metodo que lista los cassos correspondientes a un usuario de la BD.
     */
    @Override
    public List<Caso> listarCasosPorUserName(String userName) {
        return repo.findByUsuario(userName);
    }

    /**
     * MEtodo que lista los casos por nombre de usuario, pero para poder verlo en el
     * Front.
     */
    @Override
    public List<HomeCaseResponse> listadoDeCasosPorUserName(String userName) {
        return orderByDesc(listarCasosPorUserName(userName).stream().map(this::transformToHomeCase)
                .collect(Collectors.toList()));
    }

    /**
     * Metodo que muestra el detalle de un caso en especifico
     */
    @Override
    public DetailCaseResponse mostratDetalleDelCasoPorId(String idCaso) {
        return transformFromCaso(verPodId(idCaso));
    }

    /**
     * Metodo que lista las actuaciones correspondientes a un solo Caso.
     */
    @Override
    public List<MainActuacionResponse> listarActuacionesPorCaso(String idCaso) {
        return transformMainActuacion(verPodId(idCaso).getActuaciones());
    }

    private List<MainActuacionResponse> transformMainActuacion(List<Actuacion> actuaciones) {
        List<MainActuacionResponse> mainActuaciones = new ArrayList<MainActuacionResponse>();
        Map<Object, List<Actuacion>> actuMap = actuaciones.stream().collect(
                Collectors.groupingBy(actuacion -> añoFecha(actuacion.getFechaActuacion())));
        actuMap.forEach((key, item) -> {
            mainActuaciones.add(new MainActuacionResponse(String.valueOf(key),
                    item.stream().map(this::transformDetalle).collect(Collectors.toList())));
        });
        return mainActuaciones;
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
                .etapaActuacion(etapaActuacion(caso.getActuaciones())).riesgo(null)
                .nombreCaso(caso.getDescripcionCaso()).ordenInspeccion(caso.getOrdenInspeccion())
                .utltimaActuacion(fechaActuacion(caso.getActuaciones()))
                .tipoActuacion(tipoActuacion(caso.getActuaciones())).totalTareas(null)
                .tareasPendientes(null).aVencer(null).build();
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

    /**
     * Ultima etapa de la Actuacion
     * 
     * @param actuaciones
     * @return
     */
    private String etapaActuacion(List<Actuacion> actuaciones) {
        return actuaciones.isEmpty() ? " --- "
                : actuaciones.get(actuaciones.size() - 1).getEtapa().getNombreEtapa();
    }

    /**
     * Ultima fecha de Actuacion
     * 
     * @param actuaciones
     * @return
     */
    private String fechaActuacion(List<Actuacion> actuaciones) {
        return actuaciones.isEmpty() ? " --- "
                : fechaFormateada(actuaciones.get(actuaciones.size() - 1).getFechaActuacion());
    }

    /**
     * Ultima Tipo de Actuacion
     * 
     * @param actuaciones
     * @return
     */
    private String tipoActuacion(List<Actuacion> actuaciones) {
        if (actuaciones == null) {
            return " --- ";
        }
        return actuaciones.isEmpty() ? " --- "
                : actuaciones.get(actuaciones.size() - 1).getTipoActuacion()
                        .getNombreTipoActuacion();
    }

    /**
     * Cantidad de documentos de la ultima actuacion.
     * 
     * @param actuaciones
     * @return
     */
    private Integer cantidadDocumentos(List<Actuacion> actuaciones) {
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
     * Nombre de Funcionario de ultima actuacion.
     * 
     * @param actuaciones
     * @return
     */
    private String funcionario(List<Actuacion> actuaciones) {
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
}
