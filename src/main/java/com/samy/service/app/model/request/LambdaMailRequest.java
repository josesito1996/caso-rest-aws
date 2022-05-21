package com.samy.service.app.model.request;

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
public class LambdaMailRequest {

    private String httpStatus;

    private Integer maxResults;

    private LambdaMailRequestBody mailBody;
}
