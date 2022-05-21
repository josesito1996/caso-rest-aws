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
public class MainActuacionResponse implements Serializable {

    private static final long serialVersionUID = -5736745887614818740L;
    
    private String año;
    
    private List<ActuacionResponse> actuaciones;

}
