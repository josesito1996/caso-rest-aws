package com.samy.service.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Configuration
@ConfigurationProperties(prefix = "external-url")
@Getter
@RefreshScope
@Setter
@ToString
public class EndpointProperties {

	private String primaryUrl;
	
	private String officeFilesUrl;
	
	private String usersUrl;
	
	private String urlLogin;
	
}
