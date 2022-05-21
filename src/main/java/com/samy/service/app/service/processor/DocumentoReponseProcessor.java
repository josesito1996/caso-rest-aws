package com.samy.service.app.service.processor;

import static com.samy.service.app.util.Utils.fechaFormateada;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.model.response.DocumentoAnexoResponse;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DocumentoReponseProcessor {

	public List<DocumentoAnexoResponse> transformDocumentosAnexos(List<ArchivoAdjunto> archivos) {
		return archivos.stream().map(this::transfomrDocumentoAnexoResponse)
				.sorted(Comparator.comparing(DocumentoAnexoResponse::isEsPrincipal).reversed())
				.collect(Collectors.toList());
	}

	private DocumentoAnexoResponse transfomrDocumentoAnexoResponse(ArchivoAdjunto archivo) {
		String fechaRegistro = archivo.getFechaRegistro() != null ? fechaFormateada(archivo.getFechaRegistro()) : null;
		return DocumentoAnexoResponse.builder().idArchivo(archivo.getId()).type(archivo.getTipoArchivo())
				.nombreArchivo(archivo.getNombreArchivo()).tamaño(archivo.getTamaño()).fechaRegistro(fechaRegistro)
				.esPrincipal(archivo.isEsPrincipal()).url(archivo.getUrl()).build();
	}
	
}
