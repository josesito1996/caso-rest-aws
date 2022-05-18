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
public class TipoTarea implements Serializable {

    private static final long serialVersionUID = 6034935112724664260L;
    
    @DynamoDBAttribute(attributeName = "id_tipo_tarea")
    private String idTipoTarea;
    
    @DynamoDBAttribute
    private String nombreTipo;
}
