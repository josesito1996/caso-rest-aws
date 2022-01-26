package com.samy.service.app.restTemplate.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
@Setter
@ToString
public class ActuacionFileResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6546248794525016639L;

	private String id;
	
	private String url;
}
