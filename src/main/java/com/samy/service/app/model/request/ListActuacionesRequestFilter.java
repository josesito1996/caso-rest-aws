package com.samy.service.app.model.request;

import java.util.List;

import javax.validation.constraints.NotNull;

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
public class ListActuacionesRequestFilter {

  @NotNull
  private List<ReactSelectRequest> tipoActuacion;
  
  @NotNull
  private List<ReactSelectRequest> etapaActuacion;
  
  private ReactSelectRequest ordenarPor;
  
}
