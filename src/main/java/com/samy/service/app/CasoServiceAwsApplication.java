package com.samy.service.app;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class CasoServiceAwsApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(CasoServiceAwsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            String fileName = "proxy.conf";
            String rutaLinux = "/etc/nginx/conf.d/";
            File archivoConf = new File("src/main/resources/" + fileName);
            File ficheroDestino = new File(rutaLinux, fileName);
            log.info("Archivo pesa " + archivoConf.length() + " bytes");
            if (archivoConf.exists()) {
                if (!ficheroDestino.exists()) {
                    Files.copy(Paths.get(archivoConf.getAbsolutePath()),
                            Paths.get(ficheroDestino.getAbsolutePath()),
                            StandardCopyOption.REPLACE_EXISTING);
                    log.info("Archivo Copiado en Linux");
                } else {
                    log.info("Estas en Windows");
                }
            }   
        } catch (Exception e) {
            log.error("Error : " + e.getMessage());
        }
    }
}
