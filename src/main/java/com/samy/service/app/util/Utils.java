package com.samy.service.app.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

	public static String uuidGenerado() {
		return UUID.randomUUID().toString();
	}

	public static <T> T convertFromString(String cadena, Class<T> clazz) {
		try {
			return new ObjectMapper().readValue(cadena, clazz);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static String fechaFormateada(LocalDate fecha) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
		return fecha.format(formatter);
	}
}
