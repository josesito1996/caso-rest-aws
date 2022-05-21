package com.samy.service.app.model.request;

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
public class LambdaMailRequestBody {
    private String nombreUsuario;

    private String nombres;

    private String apellidos;

    private String email;

    private String password;
}
