package com.samy.service.app.external;

import java.io.Serializable;

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
public class EstadoCasoDto implements Serializable  {

    private static final long serialVersionUID = -6122644555826757692L;

    @DynamoDBAttribute(attributeName = "id_estado_caso")
    private String idEstadoCaso;
    
    @DynamoDBAttribute
    private String nombreEstado;
    
    @DynamoDBAttribute
    private Integer orden;
}
