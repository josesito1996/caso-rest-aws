package com.samy.service.app.model.response;

import java.io.Serializable;
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
public class ReporteCasosEmpresa implements Serializable {

  private static final long serialVersionUID = -5059326721680894670L;

  private Integer terminadosEnInforme;
  
  private Integer terminadosEnActaInfraccion;
  
  private Integer tramite;
  
  private Integer resolucionesPorEtapa;
  
  private Integer conMulta;
  
  private Double sumaMulta;
  
}
