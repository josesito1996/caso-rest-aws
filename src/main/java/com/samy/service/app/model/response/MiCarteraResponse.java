package com.samy.service.app.model.response;

import java.util.List;
import java.util.Map;

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
public class MiCarteraResponse {
    private String hasta;

    private String casosActivos;
    
    private String casosConcluidos;
    
    private List<Integer> data;

    private String casosRegistrados;

    private List<Map<String, Object>> etapas;

}
