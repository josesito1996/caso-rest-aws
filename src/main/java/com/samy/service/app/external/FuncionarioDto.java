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
public class FuncionarioDto {
	
	@NotEmpty
	@NotNull
	@JsonProperty("id_funcionario")
	@DynamoDBAttribute(attributeName = "id_funcionario")
	private String id;
	
	@NotEmpty
	@NotNull
	@JsonProperty("datos_funcionario")
	@DynamoDBAttribute(attributeName = "datos_funcionario")
	private String datosFuncionario;
}
