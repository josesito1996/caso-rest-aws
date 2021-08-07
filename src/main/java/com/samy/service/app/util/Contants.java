package com.samy.service.app.util;

import java.time.LocalDate;

public class Contants {
    
    public static final LocalDate fechaActual = LocalDate.now();

    public static final int diasPlazoVencimiento = 8;

    public static final String REGEX_EMAIL = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)"
            + "+[a-zA-Z]{2,6}$";
    
    public static final String lambdaMailNombre = "lambda-mail-service";
    
    public static final String lambdaMailSenderNombre = "lambda-mailSender-service";
    
    public static final String passwordCaso = "51st3ma$2021.";

}
