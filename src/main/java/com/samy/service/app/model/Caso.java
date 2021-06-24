package com.samy.service.app.model;

import java.time.LocalDate;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.samy.service.app.config.LocalDateConverter;
import com.samy.service.app.external.InspectorDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@DynamoDBTable(tableName = "casos")
@Getter
@NoArgsConstructor
@Setter
@ToString
public class Caso {
	@DynamoDBHashKey
	@DynamoDBAutoGeneratedKey
	@DynamoDBAttribute(attributeName = "id_caso")
	private String id;
	@DynamoDBAttribute
	private String descripcionCaso;
	@DynamoDBTypeConverted( converter = LocalDateConverter.class )
	@DynamoDBAttribute
	private LocalDate fechaInicio;
	@DynamoDBAttribute
	private String ordenInspeccion;
	@DynamoDBAttribute
	private InspectorDto inspectorTrabajo;
	@DynamoDBAttribute
	private InspectorDto inspectorAuxiliar;
	@DynamoDBAttribute
	private String materias;
	@DynamoDBAttribute
	private String denominacionCaso;
	@DynamoDBAttribute
	private Actuacion actuacion;
	@DynamoDBAttribute
	private Boolean estadoCaso;
}
