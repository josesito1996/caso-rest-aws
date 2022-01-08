package com.samy.service.app.model.response;

import java.io.Serializable;
import java.util.List;

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
public class ResponseBar implements Serializable {

	private static final long serialVersionUID = -3473949486096819070L;

	private String name;
	
	private List<Integer> data;
	
	private String color;
	
}
