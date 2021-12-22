package com.samy.service.app.aws;

import java.io.Serializable;

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
public class InspectorPojo implements Serializable{

	private static final long serialVersionUID = 9135568650725083470L;

	@JsonProperty("id_inspector")
	private String id;

	private String correo;
	
	private String tipo;
	
	@JsonProperty("nombreInspector")
	private String nombreInspector;
	
	private Integer identity;

	private String telefono;

	private Integer estado;
	
	private String cargo;

}
