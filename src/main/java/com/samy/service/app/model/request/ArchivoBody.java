package com.samy.service.app.model.request;

import javax.validation.constraints.NotEmpty;
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
public class ArchivoBody {
	@NotNull
	@NotEmpty
	private String nombreArchivo;

	private String base64;

	@NotNull
	@NotEmpty
	private String tipo;
}
