package com.samy.service.app.model;

import java.time.LocalDateTime;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.samy.service.app.config.LocalDateTimeConverter;
import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.external.EquipoDto;

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
public class Tarea {
	
	@DynamoDBAttribute(attributeName = "id_tarea")
	private String idTarea;
	
	@DynamoDBAttribute
	private String denominacion;
	@DynamoDBAttribute
	@DynamoDBTypeConverted( converter = LocalDateTimeConverter.class )
	private LocalDateTime fechaRegistro;
	@DynamoDBAttribute
	@DynamoDBTypeConverted( converter = LocalDateTimeConverter.class )
	private LocalDateTime fechaVencimiento;
	@DynamoDBAttribute
	private EquipoDto equipo;
	@DynamoDBAttribute
	private List<ArchivoAdjunto> archivos;
	@DynamoDBAttribute
	private Boolean estado;
}
