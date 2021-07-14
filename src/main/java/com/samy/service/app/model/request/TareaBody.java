package com.samy.service.app.model.request;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
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

    @NotNull
    @Valid
    private List<EquipoBody> equipos;

    @NotEmpty
    @NotNull
    private String mensaje;

    @NotNull
    private Boolean estado;

}
