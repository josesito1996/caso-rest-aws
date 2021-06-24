package com.samy.service.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samy.service.app.model.Caso;
import com.samy.service.app.service.CasoService;

@RestController
@RequestMapping("/api-caso")
public class CasoController {

	@Autowired
	private CasoService service;

	@GetMapping(path = "/listAll")
	public List<Caso> listCasos() {
		return service.listar();
	}

}
