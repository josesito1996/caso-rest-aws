package com.samy.service.app.aws;

import java.io.Serializable;
import com.samy.service.app.model.request.ReactSelectRequest;

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
public class InfraccionItemPojo implements Serializable {

  private static final long serialVersionUID = 4503646672414874240L;


  private ReactSelectRequest materia;
  
  
  private ReactSelectRequest subMaterias;
  
  
  private ReactSelectRequest baseLegal;
  
  
  private String gravedad;
  
  
  private Double provision;
  
  
  private String descripcion;
  
  
  private Integer trabajadoresAfectados;
  
  
  private Double uitMultaPotencial;
  
  
  private Double multaPotencial;
  
}
