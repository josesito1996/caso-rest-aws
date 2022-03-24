package com.samy.service.app.restTemplate;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.samy.service.app.aws.AnalisisRiesgoPojo;
import com.samy.service.app.restTemplate.model.ActuacionFileRequest;
import com.samy.service.app.restTemplate.model.ActuacionFileResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExternalEndpoint {

	@Value("${endpoints.api-files}")
	private String uploadFilePngActuacionUrl;

	@Value("${endpoints.api-analisisRiesgo}")
	private String apiAnalisisRiesgo;

	private RestTemplate restTemplate;

	public ExternalEndpoint(RestTemplateBuilder builder) {
		restTemplate = builder.errorHandler(new RestTemplateErrorHandler()).build();
	}

	public List<AnalisisRiesgoPojo> listByIdCaso(String idCaso) {
		log.info("ExternalEndpoint.listByIdCaso  : {} ", idCaso);
		AnalisisRiesgoPojo[] lista = restTemplate.getForObject(apiAnalisisRiesgo.concat("findById/{idCaso}"),
				AnalisisRiesgoPojo[].class, new Object[] { idCaso });
		log.info("lista {}", lista);
		return Arrays.asList(lista);
	}

	public ActuacionFileResponse uploadFilePngActuacion(ActuacionFileRequest request) {
		log.info("ExternalEndpoint.uploadFilePngActuacion  : {} ", new Gson().toJson(request));
		ActuacionFileResponse response = restTemplate.postForObject(
				uploadFilePngActuacionUrl.concat("uploadFilePngActuacion"), request, ActuacionFileResponse.class);
		log.info("Response {} ", response);
		return response;
	}

}
