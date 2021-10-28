package com.samy.service.app.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.samy.service.app.config.LocalDateConverter;
import com.samy.service.app.config.LocalDateTimeConverter;
import com.samy.service.app.external.InspectorDto;
import com.samy.service.app.external.MateriaDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@DynamoDBTable(tableName = "casos")
@Getter
@NoArgsConstructor
@Setter
@ToString
public class Caso {

    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    @DynamoDBAttribute(attributeName = "id_caso")
    private String id;

    @DynamoDBAttribute
    private String descripcionAdicional;

    @DynamoDBAttribute
    private String descripcionCaso;

    @DynamoDBTypeConverted(converter = LocalDateConverter.class)
    @DynamoDBAttribute
    private LocalDate fechaInicio;

    @DynamoDBAttribute
    private String ordenInspeccion;

    @DynamoDBAttribute
    private BigDecimal multaPotencial;

    @DynamoDBAttribute
    private List<InspectorDto> inspectorTrabajo;

    @DynamoDBAttribute
    private List<InspectorDto> inspectorAuxiliar;

    @DynamoDBAttribute
    private List<MateriaDto> materias;

    @DynamoDBAttribute
    private List<Actuacion> actuaciones;

    @DynamoDBAttribute
    private List<DynamoBodyGenerico> intendencias;

    @DynamoDBAttribute
    private List<DynamoBodyGenerico> empresas;

    @DynamoDBAttribute
    private List<DynamoBodyGenerico> sedes;

    @DynamoDBAttribute
    private String origenInspeccion;

    @DynamoDBAttribute
    private Integer trabajadoresInvolucrados;

    @DynamoDBAttribute
    private String emailGenerado;

    @DynamoDBAttribute
    private String resumen;

    @DynamoDBAttribute
    private Boolean estadoCaso;

    @DynamoDBAttribute
    private String usuario;

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute
    private LocalDateTime registro;
}
