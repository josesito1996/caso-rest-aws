package com.samy.service.app.aws;

import java.util.ArrayList;
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
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExternalDbAws {

	@Autowired
	private DynamoDB dynamoDB;

	@Autowired
	private AmazonDynamoDB awsDynamoBD;

	public MateriaPojo getTable(String idMateria) {
		log.info("ExternalDbAws.getTable");
		final ObjectMapper mapper = new ObjectMapper();
		Table tableMaterias = dynamoDB.getTable("materias");
		GetItemSpec spec = new GetItemSpec().withPrimaryKey("id_materia", idMateria);
		Item materiaItem = tableMaterias.getItem(spec);
		return mapper.convertValue(materiaItem.asMap(), MateriaPojo.class);
	}

	public AnalisisRiesgoPojo tableInfraccion(String idAnalisis) {
		log.info("ExternalDbAws.tableInfraccion");
		Table tableMaterias = dynamoDB.getTable("analisis-riesgo");
		GetItemSpec spec = new GetItemSpec().withPrimaryKey("id_analisis", idAnalisis);
		Item materiaItem = tableMaterias.getItem(spec);
		if (materiaItem == null) {
			log.info("validacion : {}",materiaItem == null);
			return AnalisisRiesgoPojo.builder().sumaMultaPotencial(0.0).sumaProvision(0.0).cantidadInvolucrados(0)
					.infracciones(new ArrayList<>()).build();
		}
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.convertValue(materiaItem.asMap(), AnalisisRiesgoPojo.class);
	}

	public List<AnalisisRiesgoPojo> tableInfraccionList(String idCaso) {
		DynamoDBMapperConfig mapperConfig = new DynamoDBMapperConfig.Builder()
				.withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement("analisis-riesgo"))
				.build();
		DynamoDBMapper mapper = new DynamoDBMapper(awsDynamoBD, mapperConfig);
		HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":id_caso", new AttributeValue().withS(idCaso));
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withFilterExpression("id_caso = :id_caso")
				.withExpressionAttributeValues(eav);
		List<AnalisisRiesgoPojo> analisisList = mapper.scan(AnalisisRiesgoPojo.class, scanExpression);
		log.info("AanlisisList {}", analisisList);
		return analisisList;
	}

	public List<EtapaPojo> getTableEtapa() {
		log.info("ExternalDbAws.getTableEtapa");
		DynamoDBMapperConfig mapperConfig = new DynamoDBMapperConfig.Builder()
				.withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement("etapas"))
				.build();
		DynamoDBMapper mapper = new DynamoDBMapper(awsDynamoBD, mapperConfig);
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		List<EtapaPojo> scanResult = mapper.scan(EtapaPojo.class, scanExpression);
		return scanResult;
	}

	public InspectorPojo tableInspector(String idInspector) {
		log.info("ExternalDbAws.tableInspector");
		Table tableInspector = dynamoDB.getTable("inspectores");
		GetItemSpec spec = new GetItemSpec().withPrimaryKey("id_inspector", idInspector);
		Item inspectorItem = tableInspector.getItem(spec);
		if (inspectorItem == null) {
			return InspectorPojo.builder().build();
		}
		final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		return mapper.convertValue(inspectorItem.asMap(), InspectorPojo.class);
	}

	public UsuarioPojo tableUsuario(String userName) {
		Map<String, AttributeValue> expresionAttributte = new HashMap<String, AttributeValue>();
		expresionAttributte.put(":userName", new AttributeValue().withS(userName));
		ScanRequest scan = new ScanRequest();
		scan.setTableName("usuarios");
		scan.setFilterExpression("nombreUsuario = :userName");
		scan.setExpressionAttributeValues(expresionAttributte);
		ScanResult scanResult = awsDynamoBD.scan(scan);
		int cantidad = scanResult.getCount();
		if (cantidad > 0) {
			for (Map<String, AttributeValue> item : scanResult.getItems()) {
				return UsuarioPojo.builder().idUsuario(item.get("id_usuario").getS())
						.nombres(item.get("nombres").getS()).apellidos(item.get("apellidos").getS()).build();
			}
		}

		return UsuarioPojo.builder().build();
	}

}
