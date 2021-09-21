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
public class MateriaResponse {
    private String idMateria;
    private String icono;
    private String color;
    private String nombreMateria;
    private List<SubMateriaResponse> subMaterias;
}
