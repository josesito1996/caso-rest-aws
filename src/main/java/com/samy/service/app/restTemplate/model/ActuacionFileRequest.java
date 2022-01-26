package com.samy.service.app.restTemplate.model;

import static com.samy.service.app.util.Contants.REGEX_UUID;
import static com.samy.service.app.util.Contants.REGEX_MIME_TYPE;
import java.io.Serializable;

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
public class ActuacionFileRequest implements Serializable {

	private static final long serialVersionUID = -807407274620743302L;

	@NotNull
	@NotNull
	@Pattern(regexp = REGEX_UUID)
	private String idArchivo;
	
	@NotNull
	@NotNull
	private String nombreArchivo;
	
	@NotNull
	@NotNull
	@Pattern(regexp = REGEX_MIME_TYPE)
	private String type;
	
}
