package com.samy.service.app.model.request;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
@ToString
public class ReactSelectRequest implements Serializable {

    private static final long serialVersionUID = -8114705409414754579L;

    @NotEmpty(message = "no debe estar vacio")
	@NotNull(message = "no debe ser nulo")
    @Schema(title = "Value", example = "c4b8984c-ca45-4d6c-92cb-84040be9a07d")
	private String value;
	
	@NotEmpty(message = "no debe estar vacio")
	@NotNull(message = "no debe ser nulo")
	@Schema(title = "Label", example = "Text label")
	private String label;
	
	@Schema(title = "CampoAux", example = "--", description = "Campo auxiliar para poder ordenar o filtrar")
	private String campoAux;
}
