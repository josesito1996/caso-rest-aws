package com.samy.service.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.samy.service.app.model.Caso;
import com.samy.service.app.restTemplate.ExternalEndpoint;
import com.samy.service.app.restTemplate.model.ActuacionFileRequest;
import com.samy.service.app.service.CasoService;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class CasoServiceAwsApplication implements CommandLineRunner {

    @Autowired
    CasoService casoService;
    
    @Autowired
    ExternalEndpoint external;

    public static void main(String[] args) {
        SpringApplication.run(CasoServiceAwsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    	// testRestTemplate();
    }
    
    public void testRestTemplate() {
    	log.info("External data {}", external.uploadFilePngActuacion(ActuacionFileRequest.builder()
        		.idArchivo("47e3f93b-07d6-4ceb-9afd-b4a5ac149a61")
        		.nombreArchivo("Doc1.docx")
        		.type("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        		.build()));
    }
    
    public void arreglarCasos() {
        List<Caso> casos = casoService.listar();
        casos.forEach(caso -> {
            caso.setEmailGenerado("notificacion.sami@sidetechsolutions.com");
            log.info("Caso actualizado : {}", casoService.modificar(caso));
        });
    }

}
