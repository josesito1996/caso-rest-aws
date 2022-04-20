package com.samy.service.app.aws;

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
public class EtapaPojo {

  @JsonProperty("id_etapa")
  private String id_etapa;

  @JsonProperty("nombreEtapa")
  private String nombreEtapa;

  @JsonProperty("nroOrden")
  private Integer nroOrden;

}
