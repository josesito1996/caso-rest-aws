package com.samy.service.app.model.request;

import static com.samy.service.app.util.Contants.REGEX_UUID;
import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
  /**
   * 
   */
  private static final long serialVersionUID = 6745709522306659984L;
  @NotNull
  @Pattern(regexp = REGEX_UUID)
  private String idCaso;
  
  @NotNull
  @NotEmpty
  private String resumen;
}
