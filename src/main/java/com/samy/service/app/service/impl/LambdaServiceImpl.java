package com.samy.service.app.service.impl;

import static com.samy.service.app.util.Contants.lambdaMailNombre;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.samy.service.app.model.request.LambdaMailRequest;
import com.samy.service.app.service.LambdaService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LambdaServiceImpl implements LambdaService {

    @Autowired
    AWSLambda awsLambda;

    @Override
    public JsonObject invocarLambdaMail(LambdaMailRequest request) {
        try {
            Gson gson = new Gson();
            String payLoad = gson.toJson(request);
            log.info(payLoad);
            InvokeRequest invokeRequest = new InvokeRequest().withFunctionName(lambdaMailNombre)
                    .withPayload(payLoad);
            InvokeResult result = awsLambda.invoke(invokeRequest);
            String ans = new String(result.getPayload().array(), StandardCharsets.UTF_8);
            JsonElement element = JsonParser.parseString(ans);
            return element.getAsJsonObject();
        } catch (ServiceException e) {
            log.error("Error al invocar lambda -> " + e.toString());
            return new JsonObject();
        }
    }

}
