package com.samy.service.app.model.request;

import static com.samy.service.app.util.Contants.REGEX_UUID;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
@Setter
@NoArgsConstructor
@ToString
public class UpdateCasoResumenRequest implements Serializable {

  private static final long serialVersionUID = 6745709522306659984L;
  
  @NotNull
  @Pattern(regexp = REGEX_UUID)
  @Schema(title = "Id Caso", example = "8119d2e9-ff6d-4058-812b-2e2da0a0b385")
  private String idCaso;
  
  @NotNull
  @NotEmpty
  @Schema(title = "Resumen del caso", example = "Descripcion o resument a modificar del caso")
  private String resumen;
}
