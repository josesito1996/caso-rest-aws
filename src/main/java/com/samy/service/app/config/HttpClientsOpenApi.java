package com.samy.service.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.samy.service.samifiles.service.api.ProcessControllerApi;
import com.samy.service.samiprimary.service.api.AnalisisRiesgoControllerApi;
import com.samy.service.samiprimary.service.api.EtapaControllerApi;
import com.samy.service.samiprimary.service.api.InspectorControllerApi;
import com.samy.service.samiprimary.service.api.MateriaControllerApi;
import com.samy.service.samiusers.service.api.UsuarioControllerApi;

@Configuration
public class HttpClientsOpenApi {

	@Bean
	public MateriaControllerApi apiMateria() {
		return new MateriaControllerApi();
	}

	@Bean
	public AnalisisRiesgoControllerApi apiAnalisisRiesgo() {
		return new AnalisisRiesgoControllerApi();
	}

	@Bean
	public EtapaControllerApi apiEtapa() {
		return new EtapaControllerApi();
	}

	@Bean
	public InspectorControllerApi apiInspector() {
		return new InspectorControllerApi();
	}

	@Bean
	public ProcessControllerApi apiProcess() {
		return new ProcessControllerApi();
	}
	
	@Bean
	public UsuarioControllerApi apiUsuario() {
		return new UsuarioControllerApi();
	}
}
