package com.samy.service.app.service.impl;

import static com.samy.service.app.util.Utils.fechaFormateada;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samy.service.app.model.Actuacion;
import com.samy.service.app.model.Caso;
import com.samy.service.app.model.request.ActuacionBody;
import com.samy.service.app.model.request.CasoBody;
import com.samy.service.app.model.request.TareaBody;
import com.samy.service.app.model.response.DetailCaseResponse;
import com.samy.service.app.model.response.HomeCaseResponse;
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

	@Transactional
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

	@Override
	public List<Caso> listarCasosPorUserName(String userName) {
		return repo.findByUsuario(userName);
	}

	@Override
	public List<HomeCaseResponse> listadoDeCasosPorUserName(String userName) {
		return listarCasosPorUserName(userName).stream().map(this::transformToHomeCase).collect(Collectors.toList());
	}
	@Override
	public DetailCaseResponse mostratDetalleDelCasoPorId(String idCaso) {
		return transformFromCaso(verPodId(idCaso));
	}
	
	private DetailCaseResponse transformFromCaso(Caso caso) {
		return DetailCaseResponse.builder()
				.idCaso(caso.getId())
				.nombreCaso(caso.getDescripcionCaso())
				.descripcion(caso.getDescripcionAdicional())
				.fechaCreacion(fechaFormateada(caso.getFechaInicio()))
				.ordenInspeccion(caso.getOrdenInspeccion())
				.materias(caso.getMaterias())
				.build();
	}
	
	private HomeCaseResponse transformToHomeCase(Caso caso) {
		return HomeCaseResponse.builder()
				.idCaso(caso.getId())
				.fechaInicio(fechaFormateada(caso.getFechaInicio()))
				.etapaActuacion(etapaActuacion(caso.getActuaciones()))
				.riesgo(null)
				.nombreCaso(caso.getDescripcionCaso())
				.ordenInspeccion(caso.getOrdenInspeccion())
				.utltimaActuacion(fechaActuacion(caso.getActuaciones()))
				.tipoActuacion(tipoActuacion(caso.getActuaciones()))
				.totalTareas(null)
				.tareasPendientes(null)
				.aVencer(null)
				.build();
	}

	/**
	 * Ultima etapa de la Actuacion
	 * @param actuaciones
	 * @return
	 */
	private String etapaActuacion(List<Actuacion> actuaciones) {
		return actuaciones.isEmpty() ? " --- " : actuaciones.get(actuaciones.size() - 1).getEtapa().getNombreEtapa();
	}
	
	/**
	 * Ultima fecha de Actuacion
	 * @param actuaciones
	 * @return
	 */
	private String fechaActuacion(List<Actuacion> actuaciones) {
		return actuaciones.isEmpty() ? " --- " : fechaFormateada(actuaciones.get(actuaciones.size() - 1).getFechaActuacion());
	}
	
	/**
	 * Ultima Tipo de Actuacion
	 * @param actuaciones
	 * @return
	 */
	private String tipoActuacion(List<Actuacion> actuaciones) {
		return actuaciones.isEmpty() ? " --- " : actuaciones.get(actuaciones.size() - 1).getTipoActuacion().getNombreTipoActuacion();
	}
}
