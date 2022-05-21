package com.samy.service.app.util;

import static com.samy.service.app.util.Contants.passwordCaso;

import com.google.gson.JsonObject;
import com.samy.service.app.model.Caso;
import com.samy.service.app.model.request.LambdaMailRequest;
import com.samy.service.app.model.request.LambdaMailRequestBody;
import com.samy.service.app.service.LambdaService;

public class LambdaUtils {

    private LambdaService lambdaService;

    public LambdaUtils(LambdaService lambdaService) {
        this.lambdaService = lambdaService;
    }

    public String mailGeneradoLambda(Caso caso) throws Exception {
        try {
            JsonObject obj = lambdaService.invocarLambdaMail(LambdaMailRequest.builder()
                    .httpStatus("POST").mailBody(lambdaMailBuilder(caso)).build());
            String email = obj.get("email").getAsString();
            return email;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private LambdaMailRequestBody lambdaMailBuilder(Caso caso) {
        return LambdaMailRequestBody.builder()
                .nombreUsuario(caso.getDescripcionCaso().replace(" ", "")
                        .concat(caso.getOrdenInspeccion()).toLowerCase())
                .nombres(caso.getDescripcionCaso()).apellidos("").password(passwordCaso).build();
    }

}
