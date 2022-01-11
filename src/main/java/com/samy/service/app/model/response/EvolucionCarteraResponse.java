package com.samy.service.app.model.response;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
public class EvolucionCarteraResponse implements Serializable {

	private static final long serialVersionUID = -6802691321453240331L;
	
	private List<String> meses;
	
	private List<Map<String, Object>> series;
}
