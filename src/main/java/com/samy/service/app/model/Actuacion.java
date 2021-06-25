package com.samy.service.app.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.samy.service.app.config.LocalDateConverter;
import com.samy.service.app.config.LocalDateTimeConverter;
import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.external.EtapaDto;
import com.samy.service.app.external.FuncionarioDto;
import com.samy.service.app.external.TipoActuacionDto;

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
public class Actuacion {

	@DynamoDBAttribute(attributeName = "id_actuacion")
	private String idActuacion;
	
	@DynamoDBAttribute
	@DynamoDBTypeConverted( converter = LocalDateConverter.class )
	private LocalDate fechaActuacion;
	
	@DynamoDBTypeConverted( converter = LocalDateTimeConverter.class )
	private LocalDateTime fechaRegistro;
	
	@DynamoDBAttribute
	private String descripcion;
	
	@DynamoDBAttribute
	private FuncionarioDto funcionario;
	
	@DynamoDBAttribute
	private TipoActuacionDto tipoActuacion;
	
	@DynamoDBAttribute
	private EtapaDto etapa;
	
	@DynamoDBAttribute
	private List<ArchivoAdjunto> archivos;
	
	@DynamoDBAttribute
	private List<Tarea> tareas;
}
