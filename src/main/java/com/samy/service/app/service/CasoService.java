package com.samy.service.app.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.samy.service.app.model.Caso;
import com.samy.service.app.model.request.ActuacionBody;
import com.samy.service.app.model.request.CasoBody;
import com.samy.service.app.model.request.DocumentoAnexoRequest;
import com.samy.service.app.model.request.EditarActuacionRequest;
import com.samy.service.app.model.request.EliminarTareaRequest;
import com.samy.service.app.model.request.ListActuacionesRequestFilter;
import com.samy.service.app.model.request.MateriaRequestUpdate;
import com.samy.service.app.model.request.TareaArchivoBody;
import com.samy.service.app.model.request.TareaBody;
import com.samy.service.app.model.request.TareaCambioEstadoBody;
import com.samy.service.app.model.request.UpdateCasoResumenRequest;
import com.samy.service.app.model.request.UpdateFileActuacionRequest;
import com.samy.service.app.model.response.ActuacionResponseX2;
import com.samy.service.app.model.response.ActuacionResponseX3;
import com.samy.service.app.model.response.CasosConRiesgoResponse;
import com.samy.service.app.model.response.CriticidadCasosResponse;
import com.samy.service.app.model.response.DetailCaseResponse;
import com.samy.service.app.model.response.DocumentoAnexoResponse;
import com.samy.service.app.model.response.GraficoCasosTemplateResponse;
import com.samy.service.app.model.response.GraficoImpactoCarteraResponse;
import com.samy.service.app.model.response.HomeCaseResponse;
import com.samy.service.app.model.response.ItemsPorCantidadResponse;
import com.samy.service.app.model.response.MainActuacionResponse;
import com.samy.service.app.model.response.MiCarteraResponse;
import com.samy.service.app.model.response.NotificacionesVencimientosResponse;
import com.samy.service.app.model.response.SaveTareaResponse;
import com.samy.service.app.model.response.UpdateCasoResumenResponse;
import com.samy.service.app.model.response.UpdateTareaResponse;

public interface CasoService extends ICrud<Caso, String> {

    public List<Caso> listarCasosPorUserName(String userName);

    public List<Caso> listarCasosPorUserNameYFechaInicio(String userName, LocalDate fechaInicio);

    public Caso verPodId(String id);

    public Caso registrarCaso(CasoBody request);

    public DetailCaseResponse agregarSubMateria(MateriaRequestUpdate request);

    public ActuacionResponseX2 registrarActuacion(ActuacionBody request, String idCaso);

    public ActuacionResponseX2 añadirArchivoActuacion(UpdateFileActuacionRequest request);

    public SaveTareaResponse registrarTarea(TareaBody request, String idActuacion, String idCaso);
    
    public SaveTareaResponse verTareaPorIdV2(String idCaso, String idActuacion, String idTarea);

    public Caso actualizarTarea(TareaBody request, String idActuacion, String idCaso);

    public Map<String, Object> registrarArchivoTarea(TareaArchivoBody tareaArchivoBody);

    public Boolean cambiarEstadoTarea(TareaCambioEstadoBody tareaCambioEstadoBody);

    @Deprecated
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
            String userName, Boolean isProximos);

    public MiCarteraResponse verCarteraResponse(String userName);

    public CriticidadCasosResponse verCriticidadResponse(String userName);

    public List<Map<String, Object>> verCasosPorMateria(String userName);

    public List<Map<String, Object>> verTotalesCompletados(String userName);

    public List<ActuacionResponseX3> verActuacionesPorIdCaso(String idCaso);
    
    public List<ActuacionResponseX3> verActuacionesPorIdCaso(String idCaso, ListActuacionesRequestFilter params);

    public List<ItemsPorCantidadResponse> casosPorEmpresa(String userName);
    
    public List<ItemsPorCantidadResponse> casosPorTrabajdoresInvolucrados(String userName);
    
    public GraficoImpactoCarteraResponse verGraficoImpactoResponse(String userName);
    
    public GraficoImpactoCarteraResponse verGraficoImpactoResponseV2(String userName);
    
    public List<CasosConRiesgoResponse> dataTableCasosRiesgoResponse(String userName);
    
    public GraficoCasosTemplateResponse evolucionCarteraResponse(String userName, String desde, String hasta);
    
    public GraficoCasosTemplateResponse materiasFiscalizadas(String userName);
    
    /**
     * Documento Anexo REsponse
     */

    public List<DocumentoAnexoResponse> cambiarPrincipal(DocumentoAnexoRequest request);
    
    public DocumentoAnexoResponse cambiarUrl(DocumentoAnexoRequest requests);
    
    public Map<String, Object> eliminarTarea(EliminarTareaRequest request);
    
    public ActuacionResponseX3 editarActuacion(EditarActuacionRequest request);
    
    public UpdateCasoResumenResponse updateResumen(UpdateCasoResumenRequest request);
    
}
