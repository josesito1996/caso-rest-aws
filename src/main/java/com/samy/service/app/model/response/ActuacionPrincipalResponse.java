package com.samy.service.app.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@ToString
public class ActuacionPrincipalResponse {
    private String denominacion;
    private List<DocumentoDetalleResponse> documentos;
}
