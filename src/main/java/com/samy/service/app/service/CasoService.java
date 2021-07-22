package com.samy.service.app.service;

import java.util.List;

import com.samy.service.app.model.Caso;
import com.samy.service.app.model.request.ActuacionBody;
import com.samy.service.app.model.request.CasoBody;
import com.samy.service.app.model.request.TareaArchivoBody;
import com.samy.service.app.model.request.TareaBody;
import com.samy.service.app.model.request.TareaCambioEstadoBody;
import com.samy.service.app.model.response.DetailCaseResponse;
import com.samy.service.app.model.response.HomeCaseResponse;
import com.samy.service.app.model.response.MainActuacionResponse;
import com.samy.service.app.model.response.NotificacionesVencimientosResponse;

public interface CasoService extends ICrud<Caso, String> {

    public Caso verPodId(String id);

    public Caso registrarCaso(CasoBody request);

    public Caso registrarActuacion(ActuacionBody request, String idCaso);

    public Caso registrarTarea(TareaBody request, String idActuacion, String idCaso);

    public Caso registrarArchivoTarea(TareaArchivoBody tareaArchivoBody);

    public Boolean cambiarEstadoTarea(TareaCambioEstadoBody tareaCambioEstadoBody);

    public List<Caso> listarCasosPorUserName(String userName);

    public List<HomeCaseResponse> listadoDeCasosPorUserName(String userName);

    public DetailCaseResponse mostratDetalleDelCasoPorId(String idCaso);

    public List<MainActuacionResponse> listarActuacionesPorCaso(String idCaso);

    public List<NotificacionesVencimientosResponse> listarNotificacionesVencimientos(String userName);

}
