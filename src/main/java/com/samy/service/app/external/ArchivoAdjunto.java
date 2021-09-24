package com.samy.service.app.external;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.samy.service.app.config.LocalDateTimeConverter;

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
	@JsonInclude(Include.NON_NULL)
	private String id;
	
	@NotEmpty
	@NotNull
	@JsonProperty("nombre_archivo")
	@DynamoDBAttribute(attributeName = "nombre_archivo")
	@JsonInclude(Include.NON_NULL)
	private String nombreArchivo;
	
	
	@NotEmpty
	@NotNull
	@JsonProperty("tipo_archivo")
	@DynamoDBAttribute(attributeName = "tipo_archivo")
	@JsonInclude(Include.NON_NULL)
	private String tipoArchivo;
	
	@NotEmpty
    @NotNull
    @JsonProperty("tamaño")
    @DynamoDBAttribute(attributeName = "tamaño")
    @JsonInclude(Include.NON_NULL)
    private String tamaño;
	
	@NotEmpty
	@NotNull
	@JsonProperty("url_archivo")
	@DynamoDBAttribute(attributeName = "url_archivo")
	@JsonInclude(Include.NON_NULL)
	private String url;
	
	@NotEmpty
    @NotNull
    @JsonProperty("estado_archivo")
    @DynamoDBAttribute(attributeName = "estado_archivo")
	@JsonInclude(Include.NON_NULL)
	private Boolean estado;
	
	
    @JsonProperty("es_principal")
    @DynamoDBAttribute(attributeName = "es_principal")
	private boolean esPrincipal;
	
    @NotNull
    @JsonProperty("fecha_registro")
    @DynamoDBAttribute(attributeName = "fecha_registro")
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @JsonInclude(Include.NON_NULL)
	private LocalDateTime fechaRegistro;
}
