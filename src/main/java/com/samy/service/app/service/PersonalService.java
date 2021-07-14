package com.samy.service.app.service;

import com.samy.service.app.model.Personal;

public interface PersonalService extends ICrud<Personal, String> {

    Personal verUnoPorId(String id);
    
}
