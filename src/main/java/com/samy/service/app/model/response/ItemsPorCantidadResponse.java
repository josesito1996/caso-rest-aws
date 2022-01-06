package com.samy.service.app.model.response;

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
public class ItemsPorCantidadResponse implements Serializable {

	private static final long serialVersionUID = 7294029123860942762L;

	private String nombreItem;
	
	private Double cantidadNumber;
	
	private String cantidad;
	
}
