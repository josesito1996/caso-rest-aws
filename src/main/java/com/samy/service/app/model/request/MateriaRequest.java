package com.samy.service.app.model.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
public class MateriaRequest {
    
    @NotNull
    @NotEmpty
    @JsonProperty("id_materia")
    private String idMateria;
    
    @Valid
    private List<SubMateriaCheck> subMateriasCheck;
    
    @Valid
    private List<ReactSelectRequest> subMateriasSelect;
}
