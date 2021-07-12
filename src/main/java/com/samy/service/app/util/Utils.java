package com.samy.service.app.util;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	public static String fechaFormateada(LocalDateTime fecha) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY hh:mm:s");
		return fecha.format(formatter);
	}

	public static String fechaFormateada(LocalDate fecha) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
		return fecha.format(formatter);
	}

	public static String añoFecha(LocalDate fecha) {
		return String.valueOf(fecha.getYear());
	}

	public static String diaFecha(LocalDate fecha) {
		return String.valueOf(fecha.getDayOfMonth());
	}

	public static String mesFecha(LocalDate fecha) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM");
		return formatMes(fecha.format(formatter));
	}

	private static String formatMes(String mes) {
		String primeraLetra = mes.substring(0, 1).toUpperCase();
		String resto = mes.substring(1, mes.length());
		return primeraLetra.concat(resto);
	}

	public static String getExtension(String fileName) {
		return fileName.substring(fileName.indexOf("."), fileName.length());
	}

	public static ArchivoS3 archivoFromBase64(String base64, String fileName, String contentType) {
		File tempFile = new File(fileName);
		try (FileOutputStream fos = new FileOutputStream(tempFile)) {
			byte[] decodedFile = Base64.getDecoder().decode(base64Text(base64).getBytes(StandardCharsets.UTF_8));
			fos.write(decodedFile);
			return ArchivoS3.builder().archivo(tempFile).contentType(contentType).build();
		} catch (Exception e) {
			log.error("Error al convertir archivo desde Base64 " + e.getMessage());
			return ArchivoS3.builder().build();
		}
	}

	public static String base64Text(String base64) {
		return base64.isEmpty() || base64 == null ? "" : base64.split(",")[1];
	}
}
