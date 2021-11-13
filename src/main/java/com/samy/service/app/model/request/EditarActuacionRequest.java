package com.samy.service.app.model.request;

import static com.samy.service.app.util.Contants.REGEX_UUID;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public class EditarActuacionRequest implements Serializable {

    private static final long serialVersionUID = 6426312283936097030L;

    @NotNull
    @NotEmpty
    @Pattern(regexp = REGEX_UUID, message = "Formato de ID invalido")
    @Schema(title = "Id Caso", example = "03e783c1-29d6-4198-bb85-78f38ff3e4f3")
    private String idCaso;
    
    @NotNull
    @NotEmpty
    @Pattern(regexp = REGEX_UUID, message = "Formato de ID invalido")
    @Schema(title = "Id Actuacion", example = "7d8160f2-00bd-40b8-be03-9d5cf90d3f68")
    private String idActuacion;
    
    @NotNull
    @NotEmpty
    @Schema(title = "Descripcion actuacion", example = "Esto es una descripcipon")
    private String descripcionActuacion;
    
}
