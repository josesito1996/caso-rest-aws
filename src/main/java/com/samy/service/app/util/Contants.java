package com.samy.service.app.util;

import java.time.LocalDate;

public class Contants {
    
    public static final LocalDate fechaActual = LocalDate.now();

    public static final int diasPlazoVencimiento = 8;

    public static final String REGEX_UUID = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$";
    
    public static final String REGEX_EMAIL = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)"
            + "+[a-zA-Z]{2,6}$";
    
    public static final String lambdaMailNombre = "lambda-mail-service";
    
    public static final String lambdaMailSenderNombre = "lambda-mailSender-service";
    
    public static final String passwordCaso = "51st3ma$2021.";

}
