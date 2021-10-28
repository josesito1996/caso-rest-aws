package com.samy.service.app;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.samy.service.app.model.Caso;
import com.samy.service.app.model.DynamoBodyGenerico;
import com.samy.service.app.service.CasoService;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class CasoServiceAwsApplication implements CommandLineRunner {

    @Autowired
    CasoService casoService;

    public static void main(String[] args) {
        SpringApplication.run(CasoServiceAwsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //arreglarCasos();
    }
    
    public void arreglarCasos() {
        List<Caso> casos = casoService.listar();
        casos.forEach(caso -> {
            caso.setIntendencias(new ArrayList<DynamoBodyGenerico>());
            caso.setSedes(new ArrayList<DynamoBodyGenerico>());
            caso.setEmpresas(new ArrayList<DynamoBodyGenerico>());
            log.info("Caso actualizado : {}", casoService.modificar(caso));
        });
    }

}
