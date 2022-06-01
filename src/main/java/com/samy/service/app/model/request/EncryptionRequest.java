package com.samy.service.app.model.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@ToString
public class EncryptionRequest implements Serializable{

	private static final long serialVersionUID = -1800325903056351843L;

	private String idCaso;
	
	private String idActuacion;
	
	private String userName;
	
}
