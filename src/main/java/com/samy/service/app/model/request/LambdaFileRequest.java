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
public class LambdaFileRequest implements Serializable {

  private static final long serialVersionUID = -9035965184693981363L;

    private String httpMethod;
    
    private String idFile;
    
    private String bucketName;
}
