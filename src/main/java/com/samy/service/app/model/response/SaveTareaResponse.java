package com.samy.service.app.model.response;

import java.io.Serializable;
import java.util.List;

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
public class SaveTareaResponse implements Serializable {

  private static final long serialVersionUID = -6072201226536728822L;

  private String tipoTarea;
  
  private String descripcion;
  
  @JsonInclude(Include.NON_NULL)
  private List<String> destinatario;
  
  @JsonInclude(Include.NON_NULL)
  private List<String> correo;
  
  @JsonInclude(Include.NON_NULL)
  private String mensaje;
  
  private String fechaRegistro;
  
  private String fechaVencimiento;
  
  private int recordatorio;
  
}
