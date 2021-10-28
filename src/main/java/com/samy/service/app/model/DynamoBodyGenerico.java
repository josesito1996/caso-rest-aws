package com.samy.service.app.model;

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
@DynamoDBDocument
@Builder
@Getter
@NoArgsConstructor
@Setter
@ToString
public class DynamoBodyGenerico implements Serializable {

    private static final long serialVersionUID = 5927367649659462589L;

    @DynamoDBAttribute
    private String value;

    @DynamoDBAttribute
    private String label;

    @DynamoDBAttribute
    private String campoAux;

}
