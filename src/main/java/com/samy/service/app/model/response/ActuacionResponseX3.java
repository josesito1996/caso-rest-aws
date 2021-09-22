package com.samy.service.app.model.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
public class ActuacionResponseX3 {

    private String idActuacion;
    
    private String documentoPrincipal;
    
    private String fechaActuacion;
    
    private String nombreActuacion;
    
    @JsonInclude(Include.NON_NULL)
    private String descripcion;
    
    private String subidoPor;
    
    private int anexos;
    
    private List<Map<String, Object>> funcionarios;
    
    private List<Map<String, Object>> vencimientos;
    
}
