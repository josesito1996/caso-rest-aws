package com.samy.service.app.service;

import com.samy.service.app.model.response.ActuacionFileResponse;

public interface ActuacionFilesService {

    public ActuacionFileResponse listarActuacionesConArchivos(String idCaso);
    
}
