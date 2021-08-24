package com.samy.service.app.model.request;

import static com.samy.service.app.util.Contants.REGEX_EMAIL;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
public class EquipoBody implements Serializable {

    private static final long serialVersionUID = 437646972346810269L;

    @JsonProperty("id_equipo")
    @JsonInclude(content = Include.NON_NULL)
    private String idEquipo;
    
    @NotNull
    @NotEmpty
    @Pattern(regexp = REGEX_EMAIL)
    private String correo;

    @NotNull
    @NotEmpty
    private String destinatario;
}
