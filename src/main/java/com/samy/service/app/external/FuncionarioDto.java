package com.samy.service.app.external;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

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
	
    @DynamoDBAttribute(attributeName = "id_funcionario")
	private String id;
	
	@DynamoDBAttribute(attributeName = "datos_funcionario")
	private String datosFuncionario;
}
