package com.samy.service.app.model.request;

import static com.samy.service.app.util.Contants.REGEX_UUID;

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
public class EliminarTareaRequest {

    @NotNull
    @NotEmpty
    @Pattern(regexp = REGEX_UUID, message = "Formato de ID invalido")
    @Schema(title = "Id caso", example = "6eb40afa-32d8-4fc1-af41-3297ec617798")
    private String idCaso;

    @NotNull
    @NotEmpty
    @Pattern(regexp = REGEX_UUID, message = "Formato de ID invalido")
    @Schema(title = "Id Actuacion", example = "c0884aad-6adb-427d-9ef7-5c7925144a9a")
    private String idActuacion;

    @NotNull
    @NotEmpty
    @Pattern(regexp = REGEX_UUID, message = "Formato de ID invalido")
    @Schema(title = "Id Tarea", example = "ba3831d4-9510-47b2-9d6c-920969026ab5")
    private String idTarea;

    @Schema(title = "Eliminado", example = "true", description = "True para eliminar")
    private boolean eliminado;

}
