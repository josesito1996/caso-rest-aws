package com.samy.service.app.model.response;

import java.io.Serializable;
import java.util.List;
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
public class DetallePorEmpresaResponse implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 146815768477843958L;

  private String empresa;

  private String fechaCreacion;

  private Integer cantidadCasos;

  private Integer cantidadActuaciones;

  private List<Map<String, Object>> usuarios;

}
