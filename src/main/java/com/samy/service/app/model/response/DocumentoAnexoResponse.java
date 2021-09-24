package com.samy.service.app.model.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
public class DocumentoAnexoResponse implements Serializable {

    private static final long serialVersionUID = 1565685203757870259L;
    
    private String idArchivo;
    
    private String nombreArchivo;
    
    @JsonInclude(Include.NON_NULL)
    private String tamaño;
    
    @JsonInclude(Include.NON_NULL)
    private String fechaRegistro;
    
    private boolean esPrincipal;

}
