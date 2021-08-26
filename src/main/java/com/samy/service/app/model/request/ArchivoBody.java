package com.samy.service.app.model.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
@JsonIgnoreProperties
public class ArchivoBody {
    
	@NotNull
	@NotEmpty
	private String nombreArchivo;

	@JsonInclude(content = Include.NON_NULL)
	private String base64;

	@NotNull
	@NotEmpty
	@JsonInclude(content = Include.NON_NULL)
	private String tipo;
	
	@JsonProperty("id_archivo")
	private String idArchivo;
	
	@JsonInclude(content = Include.NON_NULL)
	private Boolean estado;
}
