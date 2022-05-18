package com.samy.service.app.service;

import java.util.List;

import com.samy.service.app.external.EquipoDto;
import com.samy.service.app.model.Personal;

public interface PersonalService extends ICrud<Personal, String> {

    Personal verUnoPorId(String id);
    
    List<String> listarPersonal(List<EquipoDto> equipos);
    
    Personal registrarPersonal(Personal personal);
    
}
