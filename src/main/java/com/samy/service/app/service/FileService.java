package com.samy.service.app.service;

import java.util.List;

import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.util.ArchivoS3;

public interface FileService {

	public List<ArchivoAdjunto> uploadFile(List<ArchivoS3> archivos);
	
}
