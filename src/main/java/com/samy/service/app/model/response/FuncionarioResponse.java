package com.samy.service.app.model.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@Setter
@ToString
public class FuncionarioResponse implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 2514218572878642674L;
  private String idFuncionario;
  private String nombreFuncionario;
  private String etapaActuacion;
}
