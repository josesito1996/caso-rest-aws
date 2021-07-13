package com.samy.service.app.service.impl;

import static com.samy.service.app.util.Utils.archivoFromBase64;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.samy.service.app.model.request.ArchivoBody;
import com.samy.service.app.util.ArchivoS3;

@Component
public class FileResquestBuilder {

    public List<ArchivoS3> getFiles(List<ArchivoBody> archivos) {
        return archivos.stream().map(this::transformToFile).collect(Collectors.toList());
    }

    private ArchivoS3 transformToFile(ArchivoBody archivoBody) {
        return archivoFromBase64(archivoBody.getBase64(), archivoBody.getNombreArchivo(),
                archivoBody.getTipo());
    }
}
