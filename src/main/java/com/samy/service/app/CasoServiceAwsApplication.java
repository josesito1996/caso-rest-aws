package com.samy.service.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.samy.service.app.config.EndpointProperties;
import com.samy.service.app.model.Caso;
import com.samy.service.app.service.CasoService;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class CasoServiceAwsApplication implements CommandLineRunner {

	@Autowired
	CasoService casoService;
	
	@Autowired
	EndpointProperties props;


	public static void main(String[] args) {
		SpringApplication.run(CasoServiceAwsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// testRestTemplate();
		// testCasosFiles();
		log.info("ENDPOINTS {}", props);
		// arreglarCasos();
	}

	public void testCasosFiles() {
		/*
		List<Caso> casos = casoService.listar();
		casos.forEach(item -> {
			List<Actuacion> actuaciones = item.getActuaciones();
			actuaciones.forEach(actuacion -> {
				List<ArchivoAdjunto> archivos = actuacion.getArchivos();
				archivos.forEach(archivo -> {
					try {
						DocumentoAnexoResponse response = casoService.cambiarUrl(DocumentoAnexoRequest.builder()
								.idCaso(item.getId())
								.idActuacion(actuacion.getIdActuacion())
								.idArchivo(archivo.getId())
								.build());
						log.info("Response {}", response);
					} catch (Exception e) {
						log.error("Error {}", e);
					}
					
					try {
						ActuacionFileResponse response = external.uploadFilePngActuacion(ActuacionFileRequest.builder()
								.idArchivo(archivo.getId()).nombreArchivo(archivo.getNombreArchivo())
								.type(archivo.getTipoArchivo()).build());
						log.info("Response {}", response);
					} catch (Exception e) {
						log.error("Error {}", e);
					}
					
				});
			});
		});
		*/
	}


	public void arreglarCasos() {
		List<Caso> casos = casoService.listar();
		casos.forEach(caso -> {
			caso.setEmailGenerado("ponte.en@forma.com.pe");
			log.info("Caso actualizado : {}", casoService.modificar(caso));
		});
	}

}
