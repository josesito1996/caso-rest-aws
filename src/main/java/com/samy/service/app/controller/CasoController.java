package com.samy.service.app.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import com.samy.service.app.model.response.CriticidadCasosResponse;
import com.samy.service.app.model.response.DetailCaseResponse;
import com.samy.service.app.model.response.DocumentoAnexoResponse;
import com.samy.service.app.model.response.HomeCaseResponse;
import com.samy.service.app.model.response.MiCarteraResponse;
import com.samy.service.app.model.response.NotificacionesVencimientosResponse;
import com.samy.service.app.model.response.SaveTareaResponse;
import com.samy.service.app.model.response.UpdateCasoResumenResponse;
import com.samy.service.app.model.response.UpdateTareaResponse;
import com.samy.service.app.service.CasoService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api-caso")
@Slf4j
public class CasoController {

    @Autowired
    private CasoService service;

    @Operation(description = "Este metodo lista todas las actuaciones tal cual estan en la BD")
    @GetMapping(path = "/listAll")
    public List<Caso> listCasos() {
        return service.listar();
    }

    /**
     * @GetMapping(path = "/listActuacionesByIdCaso/{idCaso}") public
     *                  List<MainActuacionResponse>
     *                  listarActuacionesPorIdCaso(@PathVariable String idCaso) {
     *                  return service.listarActuacionesPorCaso(idCaso); }
     **/
    @Operation(description = "Lista las actuaciones relacionadas a un caso en especifico.")
    @GetMapping(path = "/listActuacionesByIdCaso/{idCaso}")
    public List<ActuacionResponseX3> listarActuacionesPorIdCaso(@PathVariable String idCaso) {
        log.info("ActuacionController.verActuacionesPorIdCaso");
        return service.verActuacionesPorIdCaso(idCaso);
    }

    @Operation(description = "Lista las actuaciones relacionadas a un caso en especifico y ademas las puedes filtrar por parametros")
    @PostMapping(path = "/listActuacionesByIdCasoWithParams/{idCaso}")
    public List<ActuacionResponseX3> listarActuacionesPorIdCasoConParametros(
            @PathVariable String idCaso, @RequestBody @Valid ListActuacionesRequestFilter params) {
        log.info("ActuacionController.verActuacionesPorIdCaso");
        return service.verActuacionesPorIdCaso(idCaso, params);
    }

    @Operation(description = "Elimina una tarea de la BD, mediante un request que contiene los Id's necesarios")
    @PutMapping(path = "/deleteTask")
    public Map<String, Object> eliminarTarea(@Valid @RequestBody EliminarTareaRequest request) {
        log.info("ActuacionController.eliminarTarea");
        return service.eliminarTarea(request);
    }

    @Operation(description = "Edita la descripcion de la actuacion",method = "PUT")
    @PutMapping(path = "/editActuacion")
    public ActuacionResponseX3 editarActuacion(@Valid @RequestBody EditarActuacionRequest request) {
        log.info("ActuacionController.elimieditActuacionnarTarea");
        return service.editarActuacion(request);
    }

    @Operation(description = "Lista los casos correspondientes a un usuario")
    @GetMapping(path = "/listAllByUserName/{userName}")
    public List<HomeCaseResponse> listarCasosPorNombreDeUsuario(@PathVariable String userName,
            @ParameterObject @RequestParam(required = true) Integer pageNumber,
            @RequestParam(required = true) Integer pageSize) {
        return service.listadoDeCasosPorUserName(userName, pageNumber, pageSize);
    }

    @Operation(description = "Registra un nuevo Caso")
    @PostMapping(path = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Caso registrarCaso(@Valid @RequestBody CasoBody requestBody) {
        return service.registrarCaso(requestBody);
    }

    @Operation(description = "Muestra el detalle de un caso en especifico")
    @GetMapping(path = "/findById/{id}")
    public DetailCaseResponse findById(@PathVariable String id) {
        return service.mostratDetalleDelCasoPorId(id);
    }

    @Operation(description = "Modifica el resumen del caso")
    @PutMapping(path = "/updateResumen")
    public UpdateCasoResumenResponse updateResumen(
            @RequestBody @Valid UpdateCasoResumenRequest request) {
        return service.updateResumen(request);
    }

    @SuppressWarnings("deprecation")
    @Operation(description = "Muestra el detalle de una tarea en especifico", deprecated = true)
    @GetMapping(path = "/findDetailTarea")
    public UpdateTareaResponse findDetailTareaById(@RequestParam(name = "id_case") String idCase,
            @RequestParam(name = "id_actuacion") String idActuacion,
            @RequestParam(name = "id_tarea") String idTarea) {
        return service.verTareaPorId(idCase, idActuacion, idTarea);
    }

    @Operation(description = "Registra la actuacion de un caso en especifico")
    @PostMapping(path = "/saveActuacion/{idCaso}")
    public ActuacionResponseX2 registrarActuacion(@Valid @RequestBody ActuacionBody requestBody,
            @Valid @PathVariable String idCaso) {
        return service.registrarActuacion(requestBody, idCaso);
    }

    @Operation(description = "Registra la actuacion de un caso en especifico")
    @PostMapping(path = "/updateFileActuacion")
    public ActuacionResponseX2 actualizarArchivoActuacion(
            @Valid @RequestBody UpdateFileActuacionRequest requestBody) {
        return service.añadirArchivoActuacion(requestBody);
    }

    @Operation(description = "Registra la tarea de una actuacion")
    @PostMapping(path = "/saveTarea")
    public SaveTareaResponse registrarTarea(@Valid @RequestBody TareaBody requestBody,
            @ParameterObject @RequestParam(name = "id_caso") String idCaso,
            @RequestParam(name = "id_actuacion") String idActuacion) {
        return service.registrarTarea(requestBody, idActuacion, idCaso);
    }

    @Operation(description = "Cambia el archivo principal de la actuacion")
    @PutMapping(path = "/updatePrincipalFile")
    public List<DocumentoAnexoResponse> cambiarArchivoPrincipal(
            @Valid @RequestBody DocumentoAnexoRequest request) {
        return service.cambiarPrincipal(request);
    }

    @Operation(description = "Modifica los datos de la tarea correspondiente a la actuacion.")
    @PostMapping(path = "/updateTarea")
    public SaveTareaResponse actualizarTarea(@Valid @RequestBody TareaBody requestBody,
            @ParameterObject @RequestParam(name = "id_caso") String idCaso,
            @RequestParam(name = "id_actuacion") String idActuacion) {
        return service.registrarTarea(requestBody, idActuacion, idCaso);
    }

    @Operation(description = "Obtiene el detalle de la tarea de la actuacion.")
    @GetMapping(path = "/viewTareaDetail")
    public SaveTareaResponse viewTareaDetail(
            @ParameterObject @RequestParam(name = "id_caso") String idCaso,
            @ParameterObject @RequestParam(name = "id_actuacion") String idActuacion,
            @ParameterObject @RequestParam(name = "id_tarea") String idTarea) {
        return service.verTareaPorIdV2(idCaso, idActuacion, idTarea);
    }

    @Operation(description = "Elimina una tarea en especifica")
    @GetMapping(path = "/deleteTarea")
    public Caso eliminarTarea(@RequestParam(name = "id_caso") String idCaso,
            @RequestParam(name = "id_actuacion") String idActuacion,
            @RequestParam(name = "id_tarea") String idTarea) {
        return service.eliminarTareaPorId(idCaso, idActuacion, idTarea);
    }

    @Operation(description = "Registra la tarea de una actuacion")
    @PostMapping(path = "/uploadFileFromTarea")
    public Map<String, Object> registrarArchivoTarea(
            @Valid @RequestBody TareaArchivoBody requestBody) {
        return service.registrarArchivoTarea(requestBody);
    }

    @Operation(description = "Cambia el estado de la tarea")
    @PostMapping(path = "/changeStatusTarea")
    public Boolean cambiarEstadoTarea(@Valid @RequestBody TareaCambioEstadoBody requestBody) {
        return service.cambiarEstadoTarea(requestBody);
    }

    @Operation(description = "Lista las notificaciones de vencimiento correspondientes al usuario")
    @GetMapping(path = "/listNotifVenciByUserName/{userName}")
    public List<NotificacionesVencimientosResponse> listarNotificacionesVencimientosPorNombreUsuario(
            @PathVariable String userName) {
        return service.listarNotificacionesVencimientos(userName);
    }

    @Operation(description = "Lista la cantidad de casos por etapa.")
    @GetMapping(path = "/listCarteraByUserName/{userName}")
    public MiCarteraResponse verCarteraResponse(@PathVariable String userName) {
        return service.verCarteraResponse(userName);
    }

    @Operation(description = "Lista los totales de monto de multa y el porcentaje de cada caso.")
    @GetMapping(path = "/viewCriticidadByUserName/{userName}")
    public CriticidadCasosResponse verCriticidadResponse(@PathVariable String userName) {
        return service.verCriticidadResponse(userName);
    }

    @Operation(description = "Lista los totales de monto de multa y el porcentaje de cada caso.")
    @GetMapping(path = "/viewCasoPorMateriaByUserName/{userName}")
    public List<Map<String, Object>> verCasoPorMateriaResponse(@PathVariable String userName) {
        return service.verCasosPorMateria(userName);
    }

    @Operation(description = "Lista la cantidad de casos completados por actuaciones, documentos y tareas")
    @GetMapping(path = "/viewTotalesCompletados/{userName}")
    public List<Map<String, Object>> verTotalesCompletados(@PathVariable String userName) {
        return service.verTotalesCompletados(userName);
    }
    
    @Operation(description = "Agrega una subMateria al caso.")
    @PostMapping(path = "/addSubMateriaToCase")
    public DetailCaseResponse agregarSubMateria(@Valid @RequestBody MateriaRequestUpdate request) {
        return service.agregarSubMateria(request);
    }

}
