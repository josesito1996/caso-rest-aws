package com.samy.service.app.model.request;

import static com.samy.service.app.util.Contants.REGEX_UUID;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DocumentoAnexoRequest implements Serializable {
    
    private static final long serialVersionUID = -1627544377874367123L;

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
    @Pattern(regexp = REGEX_UUID, message = "Formato de ID invalido")
    private String idArchivo;
    
    private boolean esPrincipal;
}
