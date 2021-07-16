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
public class ActuacionDetalleFileResponse {
    
    @JsonProperty("fecha_registro")
    private String fechaRegistro;
    
    @JsonProperty("tipo_actuacion")
    private String tipoActuacion;
    
    @JsonProperty("total_tareas_actuacion")
    private Integer totalTareasActuacion;
    
    @JsonProperty("total_tareas_realizadas")
    private Integer totalTareasRealizadas;
    
    @JsonProperty("total_documentos_pendientes")
    private Integer totalDocumentosPendientes;
    
    @JsonProperty("subido_por")
    private String subidoPor;
    
    @JsonProperty("actuacion_principal")
    private ActuacionPrincipalResponse actuacionPrincipal;
    
    @JsonProperty("tareas")
    private List<TareaDetalleResponse> tareas;

}
