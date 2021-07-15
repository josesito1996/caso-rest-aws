package com.samy.service.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.samy.service.app.service.CasoService;

@SpringBootApplication
public class CasoServiceAwsApplication {

    @Autowired
    CasoService service;

    public static void main(String[] args) {
        SpringApplication.run(CasoServiceAwsApplication.class, args);
    }

}
