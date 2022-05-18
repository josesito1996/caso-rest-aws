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
public class ActuacionFileResponse {

    @JsonProperty("total_actuaciones")
    private Integer totalActuaciones;
    
    @JsonProperty("total_documentos_actuacion")
    private Integer totalDocumentosActuacion;
    
    @JsonProperty("total_pendientes")
    private Integer totalPendientes;
    
    @JsonProperty("actuaciones")
    private List<ActuacionDetalleFileResponse> actuaciones;
    
}
