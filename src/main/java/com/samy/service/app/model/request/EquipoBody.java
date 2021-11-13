package com.samy.service.app.model.request;

import static com.samy.service.app.util.Contants.REGEX_EMAIL;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(title = "Id equipo", description = "276ffcaa-e051-4568-aac8-ea3086782259")
    private String idEquipo;
    
    @NotNull
    @NotEmpty
    @Pattern(regexp = REGEX_EMAIL)
    @Schema(title = "Id equipo", description = "shfiestas@gmail.com")
    private String correo;

    @NotNull
    @NotEmpty
    @Schema(title = "Id equipo", description = "jcastilloc@gmail.com")
    private String destinatario;
}
