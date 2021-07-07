package com.samy.service.app.controller;

import java.util.List;

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
import com.samy.service.app.model.request.TareaBody;
import com.samy.service.app.model.response.HomeCaseResponse;
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
	
	@GetMapping(path = "/listAllByUserName/{userName}")
	public List<HomeCaseResponse> listarCasosPorNombreDeUsuario(@PathVariable String userName) {
		return service.listadoDeCasosPorUserName(userName);
	}

	@PostMapping(path = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Caso registrarCaso(@Valid @RequestBody CasoBody requestBody) {
		log.info("Cuerpo del Body : " + requestBody.toString());
		return service.registrarCaso(requestBody);
	}
	
	@GetMapping(path = "/findById/{id}")
	public Caso findById(@PathVariable String id) {
		return service.verPodId(id);
	}
	

	@PostMapping(path = "/saveActuacion/{idCaso}")
	public Caso registrarActuacion(@Valid @RequestBody ActuacionBody requestBody, @PathVariable String idCaso) {
		log.info("Cuerpo del Body : " + requestBody.toString());
		return service.registrarActuacion(requestBody, idCaso);
	}

	@PostMapping(path = "/saveTarea")
	public Caso registrarTarea(@Valid @RequestBody TareaBody requestBody,@ParameterObject @RequestParam(name = "id_caso") String idCaso,
			@RequestParam(name = "id_actuacion") String idActuacion) {
		log.info("Cuerpo del Body : " + requestBody.toString());
		return service.registrarTarea(requestBody, idActuacion, idCaso);
	}

}
