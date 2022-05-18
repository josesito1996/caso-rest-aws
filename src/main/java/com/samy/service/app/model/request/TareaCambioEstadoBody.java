package com.samy.service.app.model.request;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
public class TareaCambioEstadoBody implements Serializable {

    private static final long serialVersionUID = -7427021443488614549L;

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
    private boolean eliminado;
    
    @NotNull
    private boolean estado;
    
}
