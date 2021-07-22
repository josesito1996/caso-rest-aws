package com.samy.service.app.model.response;

import java.util.Map;

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
public class NotificacionesVencimientosResponse {

    private String idCaso;

    private String idActuacion;

    private String idTarea;

    private String fechaVencimiento;

    private String nombreCaso;

    private Map<String, Object> descripcion;

}
