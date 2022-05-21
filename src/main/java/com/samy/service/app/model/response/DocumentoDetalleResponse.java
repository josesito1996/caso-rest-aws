package com.samy.service.app.model.response;

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
public class DocumentoDetalleResponse {
    private String id;
    
    @JsonProperty("nombre_archivo")
    private String nombreArchivo;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("fecha_registro")
    private String fechaRegistro;
    
    private Boolean isChecked;
}
