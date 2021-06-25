package com.samy.service.app.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.samy.service.app.model.Caso;
import com.samy.service.app.model.request.ActuacionBody;
import com.samy.service.app.model.request.CasoBody;
import com.samy.service.app.model.request.TareaBody;
import com.samy.service.app.repo.CasoRepo;
import com.samy.service.app.repo.GenericRepo;
import com.samy.service.app.service.CasoService;

@Service
public class CasoServiceImpl extends CrudImpl<Caso, String> implements CasoService {

	@Autowired
	private CasoRepo repo;

	@Autowired
	private CasoRequestBuilder builder;

	@Override
	protected GenericRepo<Caso, String> getRepo() {

		return repo;
	}

	@Override
	public Caso verPodId(String id) {
		Optional<Caso> optional = verPorId(id);
		return optional.isPresent() ? optional.get() : new Caso();
	}

	@Override
	public Caso registrarCaso(CasoBody request) {
		return registrar(builder.transformFromBody(request));
	}

	@Override
	public Caso registrarActuacion(ActuacionBody request, String idCaso) {
		Caso caso = verPodId(idCaso);
		return registrar(builder.transformFromNewCaso(caso, request));
	}

	@Override
	public Caso registrarTarea(TareaBody request, String idActuacion, String idCaso) {
		Caso caso = verPodId(idCaso);
		return registrar(builder.transformFromNewActuacion(caso, request, idActuacion));
	}

}
