package com.samy.service.app.model.request;

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
public class SubMateriaCheck {

    @NotNull
    @NotEmpty
    @JsonProperty("id_sub_materia")
    private String idSubMateria;
    
    @NotNull
    @NotEmpty
    @JsonProperty("sub_materia")
    private String subMateria;
    
}
