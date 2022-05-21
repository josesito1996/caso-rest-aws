package com.samy.service.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samy.service.app.model.response.ActuacionFileResponse;
import com.samy.service.app.service.ActuacionFilesService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api-actuacion")
@Slf4j
public class ActuacionController {

  @Autowired
  private ActuacionFilesService service;


	@GetMapping(path = "/listDetailActuacionByIdCase/{idCaso}")
	public ActuacionFileResponse listDetalleActuacionRenponse(@PathVariable String idCaso) {
		log.info("Id del CAso " + idCaso);
		return service.listarActuacionesConArchivos(idCaso);
	}
}
