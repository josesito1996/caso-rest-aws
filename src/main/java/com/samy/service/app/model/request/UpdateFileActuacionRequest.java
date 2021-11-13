package com.samy.service.app.model.request;

import static com.samy.service.app.util.Contants.REGEX_UUID;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
@Builder
@ToString
public class UpdateFileActuacionRequest implements Serializable {

  private static final long serialVersionUID = 5356892351474565281L;

  @NotNull
  @Pattern(regexp = REGEX_UUID)
  @Schema(title = "Id Caso", example = "5703e7f4-a1b9-4060-acd4-928a5dfd0423")
  private String idCaso;

  @NotNull
  @Pattern(regexp = REGEX_UUID)
  @Schema(title = "Id Actuacion", example = "32a18b9b-0f5d-4ec1-82b9-33eb573cd362")
  private String idActuacion;
  
  @Valid
  @NotNull
  @Size(min = 1)
  private List<ArchivoBody> archivos;
}
