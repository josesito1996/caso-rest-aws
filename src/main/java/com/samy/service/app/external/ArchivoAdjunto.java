package com.samy.service.app.external;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@DynamoDBDocument
@Getter
@NoArgsConstructor
@Setter
@ToString
public class ArchivoAdjunto {
	
	@JsonProperty("id_archivo")
	@DynamoDBAttribute(attributeName = "id_archivo")
	private String id;
	
	@NotEmpty
	@NotNull
	@JsonProperty("nombre_archivo")
	@DynamoDBAttribute(attributeName = "nombre_archivo")
	private String nombreArchivo;
	
	
	@NotEmpty
	@NotNull
	@JsonProperty("tipo_archivo")
	@DynamoDBAttribute(attributeName = "tipo_archivo")
	private String tipoArchivo;
	
	@NotEmpty
	@NotNull
	@JsonProperty("url_archivo")
	@DynamoDBAttribute(attributeName = "url_archivo")
	private String url;
	
	@NotEmpty
    @NotNull
    @JsonProperty("estado_archivo")
    @DynamoDBAttribute(attributeName = "estado_archivo")
	private Boolean estado;
}
