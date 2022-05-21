package com.samy.service.app.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class TareaDetalleResponse {

    private String id;
    
    @JsonProperty("denominacion")
    private String denominacion;
    
    @JsonProperty("fecha_vencimiento")
    private String fechaVencimiento;
    
    @JsonProperty("documentos")
    private List<DocumentoDetalleResponse> documentos;
    
}
