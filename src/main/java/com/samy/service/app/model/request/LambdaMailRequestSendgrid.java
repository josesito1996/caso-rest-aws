package com.samy.service.app.model.request;

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
public class LambdaMailRequestSendgrid {

	private String emailFrom;
	private String emailTo;
	private String templateId;
	private Map<String, String> dynamicTemplate;

}
