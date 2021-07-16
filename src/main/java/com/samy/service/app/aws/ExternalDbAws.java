package com.samy.service.app.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExternalDbAws {

    @Autowired
    private DynamoDB dynamoDB;

    public MateriaPojo getTable(String idMateria) {
        final ObjectMapper mapper = new ObjectMapper();
        Table tableMaterias = dynamoDB.getTable("materias");
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("id_materia", idMateria);
        Item materiaItem = tableMaterias.getItem(spec);
        return mapper.convertValue(materiaItem.asMap(), MateriaPojo.class);
    }
}
