package com.samy.service.app.service;

import com.samy.service.app.model.Caso;
import com.samy.service.app.model.request.ActuacionBody;
import com.samy.service.app.model.request.CasoBody;
import com.samy.service.app.model.request.TareaBody;

public interface CasoService extends ICrud<Caso, String> {

	public Caso verPodId(String id);

	public Caso registrarCaso(CasoBody request);

	public Caso registrarActuacion(ActuacionBody request, String idCaso);

	public Caso registrarTarea(TareaBody request, String idActuacion, String idCaso);

}
