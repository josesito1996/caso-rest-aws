package com.samy.service.app.model.request;

import static com.samy.service.app.util.Contants.REGEX_UUID;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public class EditarActuacionRequest {

    @NotNull
    @NotEmpty
    @Pattern(regexp = REGEX_UUID, message = "Formato de ID invalido")
    private String idCaso;
    
    @NotNull
    @NotEmpty
    @Pattern(regexp = REGEX_UUID, message = "Formato de ID invalido")
    private String idActuacion;
    
    @NotNull
    @NotEmpty
    private String descripcionActuacion;
    
}
