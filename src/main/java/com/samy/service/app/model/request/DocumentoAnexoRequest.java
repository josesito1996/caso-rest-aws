package com.samy.service.app.model.request;

import static com.samy.service.app.util.Contants.REGEX_UUID;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(title = "Id Caso", example = "6eb40afa-32d8-4fc1-af41-3297ec617798")
    private String idCaso;
    
    @NotNull
    @NotEmpty
    @Pattern(regexp = REGEX_UUID, message = "Formato de ID invalido")
    @Schema(title = "Id Actuacione", example = "f3b4067a-52d8-4e23-a9c3-d32458d888f7")
    private String idActuacion;
    
    @NotNull
    @NotEmpty
    @Pattern(regexp = REGEX_UUID, message = "Formato de ID invalido")
    @Schema(title = "Id Archivo", example = "3dd71db6-3bf8-47da-8da3-655d2f5397ad")
    private String idArchivo;
    
    private String url;
    
    private boolean esPrincipal;
}
