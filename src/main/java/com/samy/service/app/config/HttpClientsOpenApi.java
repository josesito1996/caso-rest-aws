package com.samy.service.app.config;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private EndpointProperties properties;

	private com.samy.service.samifiles.service.ApiClient apiFiles() {
		com.samy.service.samifiles.service.ApiClient apiFiles = new com.samy.service.samifiles.service.ApiClient();
		apiFiles.setBasePath(properties.getOfficeFilesUrl());
		return apiFiles;
	}

	private com.samy.service.samiprimary.service.ApiClient apiPrimary() {
		com.samy.service.samiprimary.service.ApiClient apiPrimary = new com.samy.service.samiprimary.service.ApiClient();
		apiPrimary.setBasePath(properties.getPrimaryUrl());
		return apiPrimary;
	}

	private com.samy.service.samiusers.service.ApiClient apiUsers() {
		com.samy.service.samiusers.service.ApiClient apiUsers = new com.samy.service.samiusers.service.ApiClient();
		apiUsers.setBasePath(properties.getUsersUrl());
		return apiUsers;
	}

	@Bean
	public MateriaControllerApi apiMateria() {
		return new MateriaControllerApi(apiPrimary());
	}

	@Bean
	public AnalisisRiesgoControllerApi apiAnalisisRiesgo() {
		return new AnalisisRiesgoControllerApi(apiPrimary());
	}

	@Bean
	public EtapaControllerApi apiEtapa() {
		return new EtapaControllerApi(apiPrimary());
	}

	@Bean
	public InspectorControllerApi apiInspector() {
		return new InspectorControllerApi(apiPrimary());
	}

	@Bean
	public ProcessControllerApi apiProcess() {
		return new ProcessControllerApi(apiFiles());
	}

	@Bean
	public UsuarioControllerApi apiUsuario() {
		return new UsuarioControllerApi(apiUsers());
	}
}
