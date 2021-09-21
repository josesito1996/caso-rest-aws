package com.samy.service.app.aws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExternalDbAws {

    @Autowired
    private DynamoDB dynamoDB;
    
    @Autowired
    private AmazonDynamoDB awsDynamoBD;

    public MateriaPojo getTable(String idMateria) {
        final ObjectMapper mapper = new ObjectMapper();
        Table tableMaterias = dynamoDB.getTable("materias");
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("id_materia", idMateria);
        Item materiaItem = tableMaterias.getItem(spec);
        return mapper.convertValue(materiaItem.asMap(), MateriaPojo.class);
    }
    
    public Map<String, Object> tableInfraccion(String idAnalisis) {
        Table tableMaterias = dynamoDB.getTable("analisis-riesgo");
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("id_analisis", idAnalisis);
        Item materiaItem = tableMaterias.getItem(spec);
        if (materiaItem == null) {
            return new HashMap<String, Object>();
        }
        return materiaItem.asMap();
    }

    public List<EtapaPojo> getTableEtapa() {
        DynamoDBMapperConfig mapperConfig = new DynamoDBMapperConfig.Builder()
                .withTableNameOverride(
                        DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement("etapas"))
                .build();
        DynamoDBMapper mapper = new DynamoDBMapper(awsDynamoBD, mapperConfig);
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<EtapaPojo> scanResult = mapper.scan(EtapaPojo.class, scanExpression);
        return scanResult;
    }
}
