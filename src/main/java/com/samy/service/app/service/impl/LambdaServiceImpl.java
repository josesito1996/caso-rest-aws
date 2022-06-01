package com.samy.service.app.service.impl;

import static com.samy.service.app.util.Contants.lambdaMailNombre;
import static com.samy.service.app.util.Contants.lambdaMailSenderNombre;

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
import com.samy.service.app.model.request.LambdaMailRequestSendgrid;
import com.samy.service.app.service.LambdaService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LambdaServiceImpl implements LambdaService {

    @Autowired
    AWSLambda awsLambda;

    @Override
    public JsonObject invocarLambdaMail(LambdaMailRequest request) {
        log.info("Ejecutando lambda para crear correo");
        try {
            Gson gson = new Gson();
            String payLoad = gson.toJson(request);
            InvokeRequest invokeRequest = new InvokeRequest().withFunctionName(lambdaMailNombre)
                    .withPayload(payLoad);
            InvokeResult result = awsLambda.invoke(invokeRequest);
            String ans = new String(result.getPayload().array(), StandardCharsets.UTF_8);
            JsonElement element = JsonParser.parseString(ans);
            log.info("Lambda ejecutó con exito " + element);
            return element.getAsJsonObject();
        } catch (ServiceException e) {
            log.error("Error al invocar lambda -> " + e.toString());
            return new JsonObject();
        }
    }

    @Override
    public JsonObject enviarCorreo(LambdaMailRequestSendgrid request) {
        try {
            log.info("Cuerp de la peticion para el envio de correo : " + request);
            Gson gson = new Gson();
            String payLoad = gson.toJson(request);
            log.info("PayLoad {}",payLoad);
            InvokeRequest invokeRequest = new InvokeRequest()
                    .withFunctionName(lambdaMailSenderNombre).withPayload(payLoad);
            InvokeResult result = awsLambda.invoke(invokeRequest);
            String ans = new String(result.getPayload().array(), StandardCharsets.UTF_8);
            JsonElement element = JsonParser.parseString(ans);
            log.info("Respuesta de la Lambda : " + ans);
            return element.getAsJsonObject();
        } catch (ServiceException e) {
        	e.printStackTrace();
            log.error("Error al invocar lambda -> " + e.toString());
            return new JsonObject();
        }
    }
}
