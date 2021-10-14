package com.samy.service.app.aws;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AnalisisRiesgoPojo implements Serializable {
  private static final long serialVersionUID = 5960050256963893497L;
  @JsonProperty("id_analisis")
  private String idAnalisis;
  
  private String nombreAsesor;
  
  private Integer cantidadInvolucrados;
  
  private List<InfraccionItemPojo> infracciones;
  
  @JsonProperty("id_caso")
  private String idCaso;
  
  private ReactSelectRequest OrigenCaso;
  
  private ReactSelectRequest nivelRiesgo;
  
  private Double sumaMultaPotencial;
  
  private Double sumaProvision;
}
