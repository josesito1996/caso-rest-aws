package com.samy.service.app.config;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

@Configuration
@EnableDynamoDBRepositories(basePackages = "com.samy.service.app.repo")
public class DynamoBdConfig {

	@Value("${aws.config.region}")
	private String region;

	@Value("${aws.config.access-key}")
	private String accessKey;

	@Value("${aws.config.secret-key}")
	private String secretKey;

	@Value("${aws.config.service-endpoint}")
	private String serviceEnpoint;

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {

		return AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(endpointConfiguration())
				.withCredentials(awsCredentialsProvider()).build();
	}

	public AwsClientBuilder.EndpointConfiguration endpointConfiguration() {
		return new AwsClientBuilder.EndpointConfiguration(serviceEnpoint, region);
	}

	public AWSCredentialsProvider awsCredentialsProvider() {
		return new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
	}
}
