package com.samy.service.app.model.request;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

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
public class RecordatorioRequest  implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("texto_dias")
    @NotEmpty
    @NotNull
    @DynamoDBAttribute(attributeName = "dias_texto")
    private String texto;
    
    @JsonProperty("numero_dias")
    @NotNull
    @DynamoDBAttribute(attributeName = "dias_numero")
    private Integer dia;
    
}
