package com.samy.service.app.model.request;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class TareaBody implements Serializable {

    private static final long serialVersionUID = 6719383487779952290L;

    @JsonProperty("id_tarea")
    @Schema(title = "ID Tarea", example = "0916a9ca-1a55-4f14-a2bf-0c88457e0158")
    private String idTarea;

    @NotNull
    @NotEmpty
    @Schema(title = "Denominacion", example = "Tarea233")
    private String denominacion;

    @NotNull
    @JsonProperty("fecha_vencimiento")
    @Schema(title = "Fecha vencimiento", example = "2021-11-03")
    private LocalDate fechaVencimiento;

    @Valid
    private List<EquipoBody> equipos;

    @Schema(title = "Mensaje", example = "mensaje para sandra sin demora.")
    private String mensaje;

    @Schema(title = "Estado", example = "true", description = "Estado de la tarea <b>Activo<b/>/<b>Inactivo</b>")
    private boolean estado;
    
    @Schema(title = "Eliminado", example = "false", description = "True si la tarea esta eliminada")
    private boolean eliminado;
    
    @Valid
    private List<ArchivoBody> archivos;
    
    @NotNull
    @Valid
    private ReactSelectRequest tipoTarea;
    
    @Valid
    @NotNull
    @Schema(title = "Recordatorio")
    private RecordatorioRequest recordatorio;

}