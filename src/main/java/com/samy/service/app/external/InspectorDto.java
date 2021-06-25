package com.samy.service.app.external;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@DynamoDBDocument
@Getter
@NoArgsConstructor
@Setter
@ToString
public class InspectorDto {
	
	@NotNull
	@NotEmpty
	@DynamoDBAttribute(attributeName = "id_inspector")
	private String id;
	
	
	@NotNull
	@NotEmpty
	@JsonProperty("nombre_inspector")
	@DynamoDBAttribute(attributeName = "nombre_inspector")
	private String nombreInspector;
	
	@NotNull
	@NotEmpty
	@DynamoDBAttribute(attributeName = "tipo")
	private String tipo;
}
