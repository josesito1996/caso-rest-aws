package com.samy.service.app.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.samy.service.app.model.Caso;
import com.samy.service.app.model.request.ActuacionBody;
import com.samy.service.app.model.request.CasoBody;
import com.samy.service.app.model.request.MateriaRequestUpdate;
import com.samy.service.app.model.request.TareaArchivoBody;
import com.samy.service.app.model.request.TareaBody;
import com.samy.service.app.model.request.TareaCambioEstadoBody;
import com.samy.service.app.model.response.ActuacionResponseX2;
import com.samy.service.app.model.response.ActuacionResponseX3;
import com.samy.service.app.model.response.CriticidadCasosResponse;
import com.samy.service.app.model.response.DetailCaseResponse;
import com.samy.service.app.model.response.HomeCaseResponse;
import com.samy.service.app.model.response.MainActuacionResponse;
import com.samy.service.app.model.response.MiCarteraResponse;
import com.samy.service.app.model.response.NotificacionesVencimientosResponse;
import com.samy.service.app.model.response.UpdateTareaResponse;

public interface CasoService extends ICrud<Caso, String> {

    public List<Caso> listarCasosPorUserName(String userName);

    public List<Caso> listarCasosPorUserNameYFechaInicio(String userName, LocalDate fechaInicio);

    public Caso verPodId(String id);

    public Caso registrarCaso(CasoBody request);

    public DetailCaseResponse agregarSubMateria(MateriaRequestUpdate request);

    public ActuacionResponseX2 registrarActuacion(ActuacionBody request, String idCaso);

    public Caso registrarTarea(TareaBody request, String idActuacion, String idCaso);

    public Caso actualizarTarea(TareaBody request, String idActuacion, String idCaso);

    public Map<String, Object> registrarArchivoTarea(TareaArchivoBody tareaArchivoBody);

    public Boolean cambiarEstadoTarea(TareaCambioEstadoBody tareaCambioEstadoBody);

    public UpdateTareaResponse verTareaPorId(String idCaso, String idActuacion, String idTarea);
    
    public Caso eliminarTareaPorId(String idCaso, String idActuacion, String idTarea);

    /**
     * Vista del Dashboard.
     */
    public List<HomeCaseResponse> listadoDeCasosPorUserName(String userName, Integer pageNumber,
            Integer pageSize);

    public DetailCaseResponse mostratDetalleDelCasoPorId(String idCaso);

    public List<MainActuacionResponse> listarActuacionesPorCaso(String idCaso);

    public List<NotificacionesVencimientosResponse> listarNotificacionesVencimientos(
            String userName);

    public MiCarteraResponse verCarteraResponse(String userName);

    public CriticidadCasosResponse verCriticidadResponse(String userName);

    public List<Map<String, Object>> verCasosPorMateria(String userName);

    public List<Map<String, Object>> verTotalesCompletados(String userName);
    
    
    public List<ActuacionResponseX3> verActuacionesPorIdCaso(String idCaso);

}
