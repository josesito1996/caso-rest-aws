package com.samy.service.app.util;

import static com.samy.service.app.util.Utils.uuidGenerado;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.model.request.ArchivoBody;
import com.samy.service.app.model.response.HomeCaseResponse;

public class ListUtils {

  public static List<HomeCaseResponse> orderByDesc(List<HomeCaseResponse> casos) {
    casos.sort(Comparator.comparing(HomeCaseResponse::getSiguienteVencimiento).reversed());
    return casos;
  }

  public static List<ArchivoAdjunto> listArchivoAdjunto(List<ArchivoBody> archivos) {
    return archivos.stream().map(item -> getArchivoAdjunto(item)).collect(Collectors.toList());
  }

  private static ArchivoAdjunto getArchivoAdjunto(ArchivoBody body) {
    return ArchivoAdjunto
            .builder()
            .id(uuidGenerado())
            .nombreArchivo(body.getNombreArchivo())
            .fechaRegistro(LocalDateTime.now())
            .tamaño(body.getTamaño())
            .estado(body.getEstado())
            .tipoArchivo(body.getTipo())
            .build();
  }

}
