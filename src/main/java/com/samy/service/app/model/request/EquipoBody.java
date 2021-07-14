package com.samy.service.app.model.request;

import static com.samy.service.app.util.Contants.REGEX_EMAIL;

import java.io.Serializable;

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
public class EquipoBody implements Serializable {

    private static final long serialVersionUID = 437646972346810269L;

    @NotNull
    @NotEmpty
    @Pattern(regexp = REGEX_EMAIL)
    private String correo;

    @NotNull
    @NotEmpty
    private String destinatario;
}
