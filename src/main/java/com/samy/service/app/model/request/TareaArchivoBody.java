package com.samy.service.app.model.request;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    private String id_caso;

    @NotEmpty
    @NotNull
    private String id_actuacion;

    @NotEmpty
    @NotNull
    private String id_tarea;

    @NotNull
    @Size(min = 1)
    private List<ArchivoBody> archivos;

}
