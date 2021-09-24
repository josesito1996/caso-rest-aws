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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.samy.service.app.model.Caso;
import com.samy.service.app.model.request.ActuacionBody;
import com.samy.service.app.model.request.CasoBody;
import com.samy.service.app.model.request.MateriaRequestUpdate;
import com.samy.service.app.model.request.TareaArchivoBody;
import com.samy.service.app.model.request.TareaBody;
import com.samy.service.app.model.request.TareaCambioEstadoBody;
import com.samy.service.app.model.request.UpdateFileActuacionRequest;
import com.samy.service.app.model.response.ActuacionResponseX2;
import com.samy.service.app.model.response.ActuacionResponseX3;
import com.samy.service.app.model.response.CriticidadCasosResponse;
import com.samy.service.app.model.response.DetailCaseResponse;
import com.samy.service.app.model.response.HomeCaseResponse;
import com.samy.service.app.model.response.MiCarteraResponse;
import com.samy.service.app.model.response.NotificacionesVencimientosResponse;
import com.samy.service.app.model.response.SaveTareaResponse;
import com.samy.service.app.model.response.UpdateTareaResponse;
import com.samy.service.app.service.CasoService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api-caso")
@Slf4j
public class CasoController {

    @Autowired
    private CasoService service;

    @GetMapping(path = "/listAll")
    public List<Caso> listCasos() {
        return service.listar();
    }

    /**
    @GetMapping(path = "/listActuacionesByIdCaso/{idCaso}")
    public List<MainActuacionResponse> listarActuacionesPorIdCaso(@PathVariable String idCaso) {
        return service.listarActuacionesPorCaso(idCaso);
    }
    **/
    @GetMapping(path = "/listActuacionesByIdCaso/{idCaso}" )
    public List<ActuacionResponseX3> listarActuacionesPorIdCaso(@PathVariable String idCaso){
        log.info("ActuacionController.verActuacionesPorIdCaso");
        return service.verActuacionesPorIdCaso(idCaso);
    }
    
    @GetMapping(path = "/listAllByUserName/{userName}")
    public List<HomeCaseResponse> listarCasosPorNombreDeUsuario(@PathVariable String userName,
            @ParameterObject @RequestParam(required = true) Integer pageNumber,
            @RequestParam(required = true) Integer pageSize) {
        return service.listadoDeCasosPorUserName(userName, pageNumber, pageSize);
    }

    @PostMapping(path = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Caso registrarCaso(@Valid @RequestBody CasoBody requestBody) {
        return service.registrarCaso(requestBody);
    }

    @GetMapping(path = "/findById/{id}")
    public DetailCaseResponse findById(@PathVariable String id) {
        return service.mostratDetalleDelCasoPorId(id);
    }

    @GetMapping(path = "/findDetailTarea")
    public UpdateTareaResponse findDetailTareaById(@RequestParam(name = "id_case") String idCase,
            @RequestParam(name = "id_actuacion") String idActuacion,
            @RequestParam(name = "id_tarea") String idTarea) {
        return service.verTareaPorId(idCase, idActuacion, idTarea);
    }

    @PostMapping(path = "/saveActuacion/{idCaso}")
    public ActuacionResponseX2 registrarActuacion(@Valid @RequestBody ActuacionBody requestBody,
           @Valid @PathVariable String idCaso) {
        return service.registrarActuacion(requestBody, idCaso);
    }
    
    @PostMapping(path = "/updateFileActuacion")
    public ActuacionResponseX2 actualizarArchivoActuacion(@Valid @RequestBody UpdateFileActuacionRequest requestBody) {
        return service.añadirArchivoActuacion(requestBody);
    }

    @PostMapping(path = "/saveTarea")
    public SaveTareaResponse registrarTarea(@Valid @RequestBody TareaBody requestBody,
            @ParameterObject @RequestParam(name = "id_caso") String idCaso,
            @RequestParam(name = "id_actuacion") String idActuacion) {
        return service.registrarTarea(requestBody, idActuacion, idCaso);
    }

    @PostMapping(path = "/updateTarea")
    public SaveTareaResponse actualizarTarea(@Valid @RequestBody TareaBody requestBody,
            @ParameterObject @RequestParam(name = "id_caso") String idCaso,
            @RequestParam(name = "id_actuacion") String idActuacion) {
        return service.registrarTarea(requestBody, idActuacion, idCaso);
    }

    @GetMapping(path = "/deleteTarea")
    public Caso eliminarTarea(@RequestParam(name = "id_caso") String idCaso,
            @RequestParam(name = "id_actuacion") String idActuacion,
            @RequestParam(name = "id_tarea") String idTarea) {
        return service.eliminarTareaPorId(idCaso, idActuacion, idTarea);
    }

    @PostMapping(path = "/uploadFileFromTarea")
    public Map<String, Object> registrarArchivoTarea(
            @Valid @RequestBody TareaArchivoBody requestBody) {
        return service.registrarArchivoTarea(requestBody);
    }

    @PostMapping(path = "/changeStatusTarea")
    public Boolean cambiarEstadoTarea(@Valid @RequestBody TareaCambioEstadoBody requestBody) {
        return service.cambiarEstadoTarea(requestBody);
    }

    @GetMapping(path = "/listNotifVenciByUserName/{userName}")
    public List<NotificacionesVencimientosResponse> listarNotificacionesVencimientosPorNombreUsuario(
            @PathVariable String userName) {
        return service.listarNotificacionesVencimientos(userName);
    }

    @GetMapping(path = "/listCarteraByUserName/{userName}")
    public MiCarteraResponse verCarteraResponse(@PathVariable String userName) {
        return service.verCarteraResponse(userName);
    }

    @GetMapping(path = "/viewCriticidadByUserName/{userName}")
    public CriticidadCasosResponse verCriticidadResponse(@PathVariable String userName) {
        return service.verCriticidadResponse(userName);
    }

    @GetMapping(path = "/viewCasoPorMateriaByUserName/{userName}")
    public List<Map<String, Object>> verCasoPorMateriaResponse(@PathVariable String userName) {
        return service.verCasosPorMateria(userName);
    }

    @GetMapping(path = "/viewTotalesCompletados/{userName}")
    public List<Map<String, Object>> verTotalesCompletados(@PathVariable String userName) {
        return service.verTotalesCompletados(userName);
    }

    @PostMapping(path = "/addSubMateriaToCase")
    public DetailCaseResponse agregarSubMateria(@Valid @RequestBody MateriaRequestUpdate request) {
        return service.agregarSubMateria(request);
    }

}
