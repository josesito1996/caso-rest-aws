package com.samy.service.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.samy.service.app.repo.CasoRepo;

@SpringBootApplication
public class CasoServiceAwsApplication{

	@Autowired
	CasoRepo repo;
	
	public static void main(String[] args) {
		SpringApplication.run(CasoServiceAwsApplication.class, args);
	}
}
