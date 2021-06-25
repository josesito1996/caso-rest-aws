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
public class EquipoDto {
	
	@NotEmpty
	@NotNull
	@JsonProperty("id_equipo")
	@DynamoDBAttribute(attributeName = "id_equipo")
	private String idEquipo;
	
	@NotEmpty
	@NotNull
	@JsonProperty("nombre_equipo")
	@DynamoDBAttribute(attributeName = "nombre_equipo")
	private String nombreEquipo;
}
