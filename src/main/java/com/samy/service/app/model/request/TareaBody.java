package com.samy.service.app.model.request;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class TareaBody {

    @JsonProperty("id_tarea")
    private String idTarea;

    @NotNull
    @NotEmpty
    private String denominacion;

    @NotNull
    @JsonProperty("fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Valid
    private List<EquipoBody> equipos;

    private String mensaje;

    private boolean estado;
    
    private boolean eliminado;
    
    @Valid
    private List<ArchivoBody> archivos;
    
    @NotNull
    @Valid
    private ReactSelectRequest tipoTarea;
    
    @Valid
    @NotNull
    private RecordatorioRequest recordatorio;

}