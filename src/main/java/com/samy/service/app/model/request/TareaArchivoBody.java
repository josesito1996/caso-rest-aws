package com.samy.service.app.model.request;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
@NoArgsConstructor
@Setter
@ToString
public class TareaArchivoBody implements Serializable {

    private static final long serialVersionUID = 893890546885962278L;

    @NotEmpty
    @NotNull
    @Schema(title = "ID Caso", example = "6eb40afa-32d8-4fc1-af41-3297ec617798")
    private String id_caso;

    @NotEmpty
    @NotNull
    @Schema(title = "ID Actuacion", example = "f3b4067a-52d8-4e23-a9c3-d32458d888f7")
    private String id_actuacion;

    @NotEmpty
    @NotNull
    @Schema(title = "ID Tarea", example = "3dd71db6-3bf8-47da-8da3-655d2f5397ad")
    private String id_tarea;

    @NotNull
    @Size(min = 1)
    private List<ArchivoBody> archivos;

}
