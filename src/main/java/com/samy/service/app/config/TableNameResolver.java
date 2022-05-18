package com.samy.service.app.config;

import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TableNameResolver extends DynamoDBMapperConfig.DefaultTableNameResolver {

	private String envProfile;

	public TableNameResolver() {
	}

	public TableNameResolver(String envProfile) {
		this.envProfile = envProfile;
	}

	@Override
	public String getTableName(Class<?> clazz, DynamoDBMapperConfig config) {
		String stageName = envProfile.concat("_");
		String rawTableName = super.getTableName(clazz, config);
		log.info("StageName {}, rawTableName {}", stageName, rawTableName);
		return rawTableName.concat(stageName.equals("dev_") ? "" : stageName);
	}
}
