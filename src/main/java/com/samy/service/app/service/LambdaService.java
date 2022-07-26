package com.samy.service.app.service;

import com.google.gson.JsonObject;
import com.samy.service.app.model.request.LambdaFileRequest;
import com.samy.service.app.model.request.LambdaMailRequest;
import com.samy.service.app.model.request.LambdaMailRequestSendgrid;

public interface LambdaService {

    JsonObject invocarLambdaMail(LambdaMailRequest payLoad);
    
    JsonObject enviarCorreo(LambdaMailRequestSendgrid request);
    
    JsonObject eliminarArchivo(LambdaFileRequest request);
    
}
