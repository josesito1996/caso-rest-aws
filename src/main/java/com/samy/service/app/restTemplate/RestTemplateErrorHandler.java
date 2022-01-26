package com.samy.service.app.restTemplate;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import com.samy.service.app.exception.NotFoundException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RestTemplateErrorHandler implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		log.info("RestTemplateErrorHandler.hasError");
		log.info("Response {}",response.getStatusCode());
		return (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
				|| response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		log.info("RestTemplateErrorHandler.handleError");
		log.info("Response {}",response.getStatusCode());
		if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
			throw new HttpClientErrorException(response.getStatusCode());
		} else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
			if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new NotFoundException("Recurso no encontrado");
			}
		}

	}

}
