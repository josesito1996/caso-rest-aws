package com.samy.service.app.service.impl;

import static com.samy.service.app.util.Utils.getExtension;
import static com.samy.service.app.util.Utils.uuidGenerado;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.service.FileService;
import com.samy.service.app.util.ArchivoS3;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

	@Value("${aws.bucket.name}")
	private String bucketName;

	@Autowired
	private AmazonS3 amazonS3;

	@Override
	public List<ArchivoAdjunto> uploadFile(List<ArchivoS3> files) {
		List<ArchivoAdjunto> archivos = new ArrayList<ArchivoAdjunto>();
		for (ArchivoS3 fileS3 : files) {
			try {
				File archivo = fileS3.getArchivo();
				Map<String, String> metadata = new HashMap<>();
				metadata.put("Content-Type", fileS3.getContentType());
				metadata.put("Content-Length", String.valueOf(archivo.length()));
				InputStream is = new FileInputStream(archivo);
				String uuidGenerado = uuidGenerado();
				upload(bucketName, archivo.getName().concat("(@@)").concat(uuidGenerado), Optional.of(metadata), is);
				archivos.add(new ArchivoAdjunto(uuidGenerado, archivo.getName(), getExtension(archivo.getName()), ""));
				archivo.delete();
			} catch (FileNotFoundException e) {
				log.error("Error al tratar con archivo " + e.getMessage());
			}
		}
		return archivos;
	}

	private void upload(String path, String fileName, Optional<Map<String, String>> optionalMetaData,
			InputStream input) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		optionalMetaData.ifPresent(map -> {
			if (!map.isEmpty()) {
				map.forEach(objectMetadata::addUserMetadata);
			}
		});
		try {
			amazonS3.putObject(path, fileName, input, objectMetadata);
		} catch (AmazonServiceException e) {
			throw new IllegalStateException("Error al subir archivo");
		}
	}

}
