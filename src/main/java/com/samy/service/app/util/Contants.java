package com.samy.service.app.util;

import java.time.LocalDate;

public class Contants {
    
    public static final LocalDate fechaActual = LocalDate.now();

    public static final int diasPlazoVencimiento = 3;

    public static final String REGEX_EMAIL = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)"
            + "+[a-zA-Z]{2,6}$";

}
