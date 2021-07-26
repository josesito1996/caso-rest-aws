package com.samy.service.app.util;

import java.util.Comparator;
import java.util.List;

import com.samy.service.app.model.response.HomeCaseResponse;

public class ListUtils {

    public static List<HomeCaseResponse> orderByDesc(List<HomeCaseResponse> casos) {
        casos.sort(Comparator.comparing(HomeCaseResponse::getSiguienteVencimiento).reversed());
        return casos;
    }
    
}
