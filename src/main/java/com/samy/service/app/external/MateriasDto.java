package com.samy.service.app.external;

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
public class MateriasDto {
	@DynamoDBAttribute(attributeName = "id_materia")
	private String id;
	@DynamoDBAttribute
	private String color;
	@DynamoDBAttribute
	private String icono;
	@DynamoDBAttribute(attributeName = "nombre_materia")
	@JsonProperty("nombre_materia")
	private String nombreMateria;
}
