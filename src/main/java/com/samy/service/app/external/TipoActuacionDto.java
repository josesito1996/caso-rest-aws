package com.samy.service.app.external;

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
public class TipoActuacionDto {
	@JsonProperty("id_tipo_actuacion")
	@DynamoDBAttribute(attributeName = "id_tipo_actuacion")
	private String id;
	@JsonProperty("nombre_tipo_actuacion")
	@DynamoDBAttribute(attributeName = "nombre_tipo_actuacion")
	private String nombreTipoActuacion;
}
