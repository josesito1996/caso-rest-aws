package com.samy.service.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samy.service.app.model.response.ActuacionFileResponse;
import com.samy.service.app.restTemplate.ExternalEndpoint;
import com.samy.service.app.restTemplate.model.ActuacionFileRequest;
import com.samy.service.app.service.ActuacionFilesService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api-actuacion")
@Slf4j
public class ActuacionController {

    @Autowired
    private ActuacionFilesService service;
    
    @Autowired
    private ExternalEndpoint external;
    
    @GetMapping(path = "/listDetailActuacionByIdCase/{idCaso}")
    public ActuacionFileResponse listDetalleActuacionRenponse(@PathVariable String idCaso) {
        log.info("Id del CAso " + idCaso);
        return service.listarActuacionesConArchivos(idCaso);
    }
    
    @GetMapping(path = "/testRest")
    public com.samy.service.app.restTemplate.model.ActuacionFileResponse fileResponse(){
    	return external.uploadFilePngActuacion(ActuacionFileRequest.builder()
        		.idArchivo("47e3f93b-07d6-4ceb-9afd-b4a5ac149a60")
        		.nombreArchivo("Doc1.docx")
        		.type("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        		.build());
    }
}
