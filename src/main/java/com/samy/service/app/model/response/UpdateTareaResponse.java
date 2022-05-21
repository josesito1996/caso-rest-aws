package com.samy.service.app.model.response;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.samy.service.app.model.request.ArchivoBody;
import com.samy.service.app.model.request.EquipoBody;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateTareaResponse {
    @JsonProperty("id_tarea")
    private String idTarea;

    @NotNull
    @NotEmpty
    private String denominacion;

    @NotNull
    @JsonProperty("fecha_vencimiento")
    private String fechaVencimiento;

    @NotNull
    @Valid
    private List<EquipoBody> equipos;

    @NotEmpty
    @NotNull
    private String mensaje;

    @NotNull
    private Boolean estado;
    
    @Valid
    private List<ArchivoBody> archivos;
}
