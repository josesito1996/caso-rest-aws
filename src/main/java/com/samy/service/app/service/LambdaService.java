package com.samy.service.app.service;

import com.google.gson.JsonObject;
import com.samy.service.app.model.request.LambdaMailRequest;

public interface LambdaService {

    JsonObject invocarLambdaMail(LambdaMailRequest payLoad);
    
}
