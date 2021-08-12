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
public class SubMateriaDto {

    @DynamoDBAttribute(attributeName = "id_sub_materia")
    private String idSubMateria;

    @DynamoDBAttribute(attributeName = "nombre_sub_materia")
    private String nombreSubMateria;

    @DynamoDBAttribute(attributeName = "id_materia")
    private String idMateria;

    @DynamoDBAttribute(attributeName = "prioridad")
    private Boolean prioridad;

}
