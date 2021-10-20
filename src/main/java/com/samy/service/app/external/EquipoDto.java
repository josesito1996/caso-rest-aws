package com.samy.service.app.external;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

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
public class EquipoDto {

    @DynamoDBAttribute(attributeName = "id_equipo")
    private String idEquipo;
    
    @DynamoDBAttribute(attributeName = "nombre")
    private String nombre;

}
