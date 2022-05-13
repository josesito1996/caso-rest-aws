package com.samy.service.app.service.impl;

import static com.samy.service.app.service.impl.ServiceUtils.totalActuacionesCompletadasGeneral;
import static com.samy.service.app.service.impl.ServiceUtils.totalCasosPorEstado;
import static com.samy.service.app.service.impl.ServiceUtils.totalDocumentosGenerales;
import static com.samy.service.app.service.impl.ServiceUtils.totalDocumentosPendientes;
import static com.samy.service.app.service.impl.ServiceUtils.totalTareasGeneralPorEstado;
import static com.samy.service.app.service.impl.ServiceUtils.totalTareasPorVencerCasos;
import static com.samy.service.app.util.Contants.ID_PRIMERA_INSTANCIA;
import static com.samy.service.app.util.Contants.ID_SEGUNDA_INSTANCIA;
import static com.samy.service.app.util.Contants.ID_TERCERA_INSTANCIA;
import static com.samy.service.app.util.Contants.correoSami;
import static com.samy.service.app.util.ListUtils.listArchivoAdjunto;
import static com.samy.service.app.util.ListUtils.orderByDesc;
import static com.samy.service.app.util.Utils.añoFecha;
import static com.samy.service.app.util.Utils.diaFecha;
import static com.samy.service.app.util.Utils.fechaFormateada;
import static com.samy.service.app.util.Utils.fechaFormateadaYYYMMDD;
import static com.samy.service.app.util.Utils.formatMoney;
import static com.samy.service.app.util.Utils.formatMoneyV2;
import static com.samy.service.app.util.Utils.getPorcentaje;
import static com.samy.service.app.util.Utils.mesAnioFecha;
import static com.samy.service.app.util.Utils.mesFecha;
import static com.samy.service.app.util.Utils.round;
import static com.samy.service.app.util.Utils.toLocalDateYYYYMMDD;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.samy.service.app.aws.ExternalDbAws;
import com.samy.service.app.exception.BadRequestException;
import com.samy.service.app.exception.NotFoundException;
import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.external.EstadoCasoDto;
import com.samy.service.app.external.MateriaDto;
import com.samy.service.app.model.Actuacion;
import com.samy.service.app.model.Caso;
import com.samy.service.app.model.Tarea;
import com.samy.service.app.model.request.ActuacionBody;
import com.samy.service.app.model.request.ArchivoBody;
import com.samy.service.app.model.request.CasoBody;
import com.samy.service.app.model.request.DocumentoAnexoRequest;
import com.samy.service.app.model.request.EditarActuacionRequest;
import com.samy.service.app.model.request.EliminarTareaRequest;
import com.samy.service.app.model.request.LambdaMailRequestSendgrid;
import com.samy.service.app.model.request.ListActuacionesRequestFilter;
import com.samy.service.app.model.request.MateriaRequestUpdate;
import com.samy.service.app.model.request.ReactSelectRequest;
import com.samy.service.app.model.request.TareaArchivoBody;
import com.samy.service.app.model.request.TareaBody;
import com.samy.service.app.model.request.TareaCambioEstadoBody;
import com.samy.service.app.model.request.UpdateCasoResumenRequest;
import com.samy.service.app.model.request.UpdateFileActuacionRequest;
import com.samy.service.app.model.response.ActuacionResponse;
import com.samy.service.app.model.response.ActuacionResponseX2;
import com.samy.service.app.model.response.ActuacionResponseX3;
import com.samy.service.app.model.response.CasoDto;
import com.samy.service.app.model.response.CasosConRiesgoResponse;
import com.samy.service.app.model.response.CriticidadCasosResponse;
import com.samy.service.app.model.response.DetailCaseResponse;
import com.samy.service.app.model.response.DetalleActuacionResponse;
import com.samy.service.app.model.response.DocumentoAnexoResponse;
import com.samy.service.app.model.response.GraficoCasosTemplateResponse;
import com.samy.service.app.model.response.GraficoImpactoCarteraResponse;
import com.samy.service.app.model.response.HomeCaseResponse;
import com.samy.service.app.model.response.ItemsPorCantidadResponse;
import com.samy.service.app.model.response.MainActuacionResponse;
import com.samy.service.app.model.response.MiCarteraResponse;
import com.samy.service.app.model.response.NotificacionesVencimientosResponse;
import com.samy.service.app.model.response.ResponseBar;
import com.samy.service.app.model.response.SaveTareaResponse;
import com.samy.service.app.model.response.UpdateCasoResumenResponse;
import com.samy.service.app.model.response.UpdateTareaResponse;
import com.samy.service.app.repo.CasoRepo;
import com.samy.service.app.repo.GenericRepo;
import com.samy.service.app.service.CasoService;
import com.samy.service.app.service.LambdaService;
import com.samy.service.app.service.processor.ActuacionResponseProcessor;
import com.samy.service.app.service.processor.CasoRequestBuilder;
import com.samy.service.app.service.processor.CasoResponseProcessor;
import com.samy.service.app.service.processor.DocumentoReponseProcessor;
import com.samy.service.app.service.processor.TareaResponseProcessor;
import com.samy.service.app.util.ListPagination;
import com.samy.service.samifiles.service.model.ActuacionFileRequest;
import com.samy.service.samifiles.service.model.ActuacionFileResponse;
import com.samy.service.samiprimary.service.model.AnalisisRiesgo;
import com.samy.service.samiprimary.service.model.EtapaResponse;
import com.samy.service.samiusers.service.model.ColaboradorResponse;
import com.samy.service.samiusers.service.model.Usuario;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CasoServiceImpl extends CrudImpl<Caso, String> implements CasoService {

	@Autowired
	private CasoRepo repo;

	@Autowired
	private CasoRequestBuilder builder;

	@Autowired
	private ExternalDbAws externalAws;

	@Autowired
	private LambdaService lambdaService;

	@Override
	protected GenericRepo<Caso, String> getRepo() {

		return repo;
	}

	/**
	 * Metodo que sirve para visualizar el caso por ID
	 */
	@Override
	public Caso verPodId(String id) {
		Optional<Caso> optional = verPorId(id);
		if (!optional.isPresent()) {
			throw new NotFoundException("El Caso con el ID : " + id + " no existe");
		}
		return optional.get();
	}

	/**
	 * Metodo que registra el caso.
	 */
	@Transactional
	@Override
	public Caso registrarCaso(CasoBody request) {
		Caso casoParaRegistrar = builder.transformFromBody(request);
		casoParaRegistrar.setEmpresa(externalAws.getUser(request.getUsuario()).getEmpresa());
		Caso casoRegistrado = registrar(casoParaRegistrar);
		if (casoRegistrado != null) {
			try {
				// LambdaUtils util = new LambdaUtils(lambdaService);
				// String email;
				// email = util.mailGeneradoLambda(casoRegistrado);
				casoRegistrado.setEmailGenerado(correoSami);
				return modificar(casoRegistrado);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return casoRegistrado;
	}

	/**
	 * Metodo que registra la actuacion.
	 */
	@Transactional
	@Override
	public ActuacionResponseX2 registrarActuacion(ActuacionBody request, String idCaso) {
		Caso caso = verPodId(idCaso);
		String estadoCaso = request.getEstadoCaso().getCampoAux();
		if (estadoCaso.contains("3") || estadoCaso.contains("5") || estadoCaso.contains("10")) {
			caso.setEstadoCaso(false);
		}
		ActuacionResponseProcessor processor = new ActuacionResponseProcessor();
		return processor.transformMap(registrar(builder.transformActuacion(caso, request)));
	}

	/**
	 * Metodo que registra la tarea.
	 */
	@Override
	public SaveTareaResponse registrarTarea(TareaBody request, String idActuacion, String idCaso) {
		Caso caso = verPodId(idCaso);
		/**
		 * if (caso.getEmailGenerado() == null) { try { LambdaUtils util = new
		 * LambdaUtils(lambdaService); String email = util.mailGeneradoLambda(caso);
		 * log.info("Correo creado con exito " + email); caso.setEmailGenerado(email);
		 * return registraTareaResponse(request, idActuacion, caso); } catch (Exception
		 * e) { log.error("Error al crear el correo registrando la tarea " +
		 * e.getMessage()); } }
		 **/
		try {
			CompletableFuture<JsonObject> completableFuture = CompletableFuture.supplyAsync(
					() -> lambdaService.enviarCorreo(LambdaMailRequestSendgrid.builder().content(request.getMensaje())
							.emailTo(request.getEquipos().get(0).getCorreo()).emailFrom(caso.getEmailGenerado())
							.subject("Asunto : " + request.getDenominacion()).build()));
			completableFuture.get();
		} catch (Exception e) {
			log.error("Error al enviar correo " + e.getMessage());
		}
		TareaResponseProcessor processor = new TareaResponseProcessor(builder, repo);
		return processor.registraTareaResponse(request, idActuacion, caso);
	}

	/**
	 * Metodo que registra el archivo de una tarea ya registrada previamente.
	 */
	@Override
	public Map<String, Object> registrarArchivoTarea(TareaArchivoBody tareaArchivoBody) {
		Caso caso = verPodId(tareaArchivoBody.getId_caso());
		TareaResponseProcessor processor = new TareaResponseProcessor();
		return processor.transformMapTarea(registrar(builder.transformUpdateTarea(caso, tareaArchivoBody)), tareaArchivoBody);
	}

	/**
	 * Metodo que cambia es estado de una Tarea
	 */
	@Override
	public Boolean cambiarEstadoTarea(TareaCambioEstadoBody tareaCambioEstadoBody) {
		Caso caso = verPodId(tareaCambioEstadoBody.getId_caso());
		return registrar(builder.transformCambioEstadoTarea(caso, tareaCambioEstadoBody)).getId() != null;
	}

	/**
	 * MEtodo para registrar la SubMaterias
	 */
	@Override
	public DetailCaseResponse agregarSubMateria(MateriaRequestUpdate request) {
		Caso caso = verPodId(request.getIdCaso());
		List<MateriaDto> materias = caso.getMaterias();
		if (materias != null) {
			materias.clear();
			caso.setMaterias(builder.materiaDtoBuilderList(request.getMaterias()));
		}
		CasoResponseProcessor processor = new CasoResponseProcessor(externalAws);
		return processor.transformFromCaso(registrar(caso));
	}

	/**
	 * Metodo que lista los cassos correspondientes a un usuario de la BD.
	 */
	@Override
	public List<Caso> listarCasosPorUserName(String userName) {
		return repo.findByUsuario(userName);
	}

	/**
	 * Metodo que lista Casos por nombre de usuario y FEcha de inicio como
	 * parametros.
	 */
	@Override
	public List<Caso> listarCasosPorUserNameYFechaInicio(String userName, LocalDate fechaInicio) {
		return repo.findByUsuarioAndFechaInicio(userName, fechaInicio);
	}

	/**
	 * MEtodo que lista los casos por nombre de usuario, pero para poder verlo en el
	 * Front.
	 */
	@Override
	public List<HomeCaseResponse> listadoDeCasosPorUserName(String userName, Integer pageNumber, Integer pageSize) {
		List<Caso> lista = getUserNamePrincipal(userName);
		CasoResponseProcessor processor = new CasoResponseProcessor(externalAws);
		return ListPagination.getPage(
				orderByDesc(lista.stream().map(processor::transformToHomeCase).collect(Collectors.toList())), pageNumber,
				pageSize);
	}

	/**
	 * Metodo que muestra el detalle de un caso en especifico
	 */
	@Override
	public DetailCaseResponse mostratDetalleDelCasoPorId(String idCaso) {
		CasoResponseProcessor processor = new CasoResponseProcessor(externalAws);
		return processor.transformFromCaso(verPodId(idCaso));
	}

	/**
	 * Metodo que lista las Notificacion y Vencimientos
	 */
	@Override
	public List<NotificacionesVencimientosResponse> listarNotificacionesVencimientos(String userName,
			Boolean isProximos) {
		/*
		 * return listarCasosPorUserName(userName).stream()
		 * .map(this::transformNotificacionesVencimientosResponse)
		 * .collect(Collectors.toList());
		 */
		TareaResponseProcessor processor = new TareaResponseProcessor();
		return processor.test(getUserNamePrincipal(userName), isProximos);
	}

	/**
	 * Metodo que lista las actuaciones correspondientes a un solo Caso.
	 */
	@Override
	public List<MainActuacionResponse> listarActuacionesPorCaso(String idCaso) {
		return transformMainActuacion(verPodId(idCaso).getActuaciones());
	}

	@Override
	public MiCarteraResponse verCarteraResponse(String userName) {
		List<Caso> casos = getUserNamePrincipal(userName);
		int totalCasos = casos.size();
		int totalCasosActivos = totalCasosPorEstado(casos, true);
		int totalCasosConcluidos = totalCasosPorEstado(casos, false);
		return MiCarteraResponse.builder().hasta(fechaFormateada(LocalDateTime.now()))
				.casosActivos(String.valueOf(totalCasosActivos)).casosConcluidos(String.valueOf(totalCasosConcluidos))
				.casosRegistrados(String.valueOf(totalCasos)).etapas(transformToMap(casos)).build();
	}

	@Override
	public CriticidadCasosResponse verCriticidadResponse(String userName) {
		List<Caso> casos = getUserNamePrincipal(userName);
		if (casos.isEmpty()) {
			return CriticidadCasosResponse.builder().build();
		}
		BigDecimal suma = casos.parallelStream().map(caso -> {
			AnalisisRiesgo analisisRiesgo = externalAws.tableInfraccion(caso.getId())
					.stream().sorted(Comparator.comparing(AnalisisRiesgo::getFechaRegistro).reversed())
					.findFirst().orElse(AnalisisRiesgo.builder()
							.sumaMultaPotencial(0.0)
							.build());
			caso.setMultaPotencial(BigDecimal.valueOf(analisisRiesgo.getSumaMultaPotencial()));
			return caso.getMultaPotencial();
		}).reduce(BigDecimal.ZERO, BigDecimal::add);
		double mayor = casos.stream().max(Comparator.comparing(Caso::getMultaPotencial)).get().getMultaPotencial()
				.doubleValue();
		CasoResponseProcessor processor = new CasoResponseProcessor();
		return CriticidadCasosResponse.builder().total(formatMoney(suma.doubleValue()))
				.detalles(processor.transformToMapCritidicidad(casos, mayor)).build();
	}

	@Override
	public List<Map<String, Object>> verCasosPorMateria(String userName) {
		List<Caso> casos = getUserNamePrincipal(userName);
		if (casos.isEmpty()) {
			return new ArrayList<Map<String, Object>>();
		}
		List<String> materias = new ArrayList<String>();
		for (Caso caso : casos) {
			for (MateriaDto materia : caso.getMaterias()) {
				if (!materias.contains(materia.getNombreMateria())) {
					materias.add(materia.getNombreMateria());
				}
			}
		}
		List<Map<String, Object>> mapResponse = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapItem;
		for (String materia : materias) {
			mapItem = new HashMap<String, Object>();
			List<Map<String, Object>> newCaso = listCasosByMateria(materia, casos);
			mapItem.put("nombreMateria", materia);
			mapItem.put("cantidadCasos", newCaso.size());
			mapItem.put("casos", newCaso);
			mapResponse.add(mapItem);
		}
		mapResponse.sort(Comparator.comparing(m -> (int) m.get("cantidadCasos"),
				Comparator.nullsLast(Comparator.reverseOrder())));
		return mapResponse;
	}

	@Override
	public List<Map<String, Object>> verTotalesCompletados(String userName) {
		List<Caso> casos = getUserNamePrincipal(userName);
		List<Map<String, Object>> lista = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tipo", "Actuaciones");
		map.put("completadas", totalActuacionesCompletadasGeneral(casos, true));
		map.put("activas", totalActuacionesCompletadasGeneral(casos, false));
		lista.add(map);
		map = new HashMap<String, Object>();
		map.put("tipo", "Documentos");
		map.put("total", totalDocumentosGenerales(casos));
		map.put("pendientes", totalDocumentosPendientes(casos, false));
		lista.add(map);
		map = new HashMap<String, Object>();
		map.put("tipo", "Tareas");
		map.put("completadas", totalTareasGeneralPorEstado(casos, true));
		map.put("pendientes", totalTareasGeneralPorEstado(casos, false));
		map.put("porVencer", totalTareasPorVencerCasos(casos));
		lista.add(map);
		return lista;
	}

	@Override
	public Caso actualizarTarea(TareaBody request, String idActuacion, String idCaso) {
		return null;
	}

	@Override
	public UpdateTareaResponse verTareaPorId(String idCaso, String idActuacion, String idTarea) {
		Caso caso = verPodId(idCaso);
		List<Actuacion> actuaciones = caso.getActuaciones();
		if (actuaciones.isEmpty()) {
			throw new NotFoundException("No hay Actuaciones para el Caso " + idCaso);
		}
		Actuacion actuacion = actuaciones.stream().filter(item -> item.getIdActuacion().equals(idActuacion))
				.collect(Collectors.toList()).get(0);
		List<Tarea> tareas = actuacion.getTareas().stream().filter(tarea -> tarea.getIdTarea().equals(idTarea))
				.collect(Collectors.toList());
		if (tareas.isEmpty()) {
			throw new NotFoundException("No hay tareas para la Actuacion");
		}
		Tarea tarea = tareas.get(0);
		return UpdateTareaResponse.builder().idTarea(tarea.getIdTarea()).denominacion(tarea.getDenominacion())
				.mensaje(tarea.getMensaje()).estado(tarea.isEstado())
				.fechaVencimiento(fechaFormateadaYYYMMDD(tarea.getFechaVencimiento()))
				.equipos(builder.getEquiposBody(tarea.getEquipos()))
				.archivos(builder.listArchivoBody(tarea.getArchivos())).build();
	}

	@Override
	public Caso eliminarTareaPorId(String idCaso, String idActuacion, String idTarea) {
		Caso caso = verPodId(idCaso);
		List<Actuacion> actuaciones = caso.getActuaciones();
		if (actuaciones.isEmpty()) {
			throw new NotFoundException("Este caso no tiene actuaciones");
		}
		Actuacion actuacion = actuaciones.stream().filter(item -> item.getIdActuacion().equals(idActuacion))
				.collect(Collectors.toList()).get(0);
		int index = actuaciones.indexOf(actuacion);
		List<Tarea> tareas = actuacion.getTareas();
		if (tareas.isEmpty()) {
			throw new NotFoundException("Esta actuacion no tiene tareas");
		}
		Tarea tarea = tareas.stream().filter(item -> item.getIdTarea().equals(idTarea)).collect(Collectors.toList())
				.get(0);
		int indexTarea = tareas.indexOf(tarea);
		tareas.get(indexTarea).setEliminado(true);
		actuaciones.get(index).setTareas(tareas);
		caso.setActuaciones(actuaciones);
		return registrar(caso);
	}

	@Override
	public List<ActuacionResponseX3> verActuacionesPorIdCaso(String idCaso) {
		Caso caso = verPodId(idCaso);
		List<Actuacion> actuaciones = caso.getActuaciones();
		ActuacionResponseProcessor processor = new ActuacionResponseProcessor(externalAws);
		List<ActuacionResponseX3> response = actuaciones.stream()
				.sorted(Comparator.comparing(Actuacion::getFechaRegistro).reversed())
				// .peek(item -> System.out.println(item))
				.map(processor::transformActuacionResponseX3)
				// .peek(item -> System.out.println(item.getFechaRegistro() + " - " +
				// item.getFechaActuacion()))
				.collect(Collectors.toList());
		if (response.isEmpty()) {
			return response;
		}
		response.get(0).setPrimerItem(true);
		return response;
	}

	@Override
	public List<DocumentoAnexoResponse> cambiarPrincipal(DocumentoAnexoRequest request) {
		log.info("CasoServiceImpl.cambiarPrincipal");
		Caso caso = verPodId(request.getIdCaso());
		if (caso.getActuaciones().isEmpty()) {
			throw new NotFoundException("No hay actuaciones registradas para el caso : " + caso.getDescripcionCaso());
		}
		List<Actuacion> actuaciones = caso.getActuaciones().stream()
				.filter(actuacion -> actuacion.getIdActuacion().equals(request.getIdActuacion()))
				.collect(Collectors.toList());
		if (actuaciones.isEmpty()) {
			throw new NotFoundException("No hay actuaciones registradas con el ID  : " + request.getIdActuacion());
		}
		Actuacion actuacion = actuaciones.get(0);
		List<ArchivoAdjunto> archivos = actuacion.getArchivos().stream().flatMap(item -> {
			if (item.getId().equals(request.getIdArchivo())) {
				item.setEsPrincipal(request.isEsPrincipal());
				item.setUrl(request.getUrl());
				// item.setEstado(request.isEliminado());
			} else {
				item.setEsPrincipal(!request.isEsPrincipal());
				// item.setUrl(request.getUrl());
				// item.setEstado(!item.isEstado());
			}
			return Stream.of(item);
		}).collect(Collectors.toList());

		List<ArchivoAdjunto> newArchivos = archivos.stream().filter(item -> item.getId().equals(request.getIdArchivo()))
				.collect(Collectors.toList());
		if (newArchivos.isEmpty()) {
			throw new NotFoundException("No hay archivos registrados con el ID  : " + request.getIdArchivo());
		}
		ArchivoAdjunto archivo = newArchivos.get(0);
		int indexArchivo = actuacion.getArchivos().indexOf(archivo);
		int indexActuacion = caso.getActuaciones().indexOf(actuacion);
		caso.getActuaciones().get(indexActuacion).getArchivos().set(indexArchivo, archivo);
		Caso caseModified = modificar(caso);
		DocumentoReponseProcessor processor = new DocumentoReponseProcessor();
		return processor.transformDocumentosAnexos(caseModified.getActuaciones().get(indexActuacion).getArchivos());
	}

	@Override
	public Map<String, Object> eliminarTarea(EliminarTareaRequest request) {
		Caso caso = verPodId(request.getIdCaso());
		if (caso.getActuaciones().isEmpty()) {
			throw new NotFoundException("No hay actuaciones registradas para el caso : " + caso.getDescripcionCaso());
		}
		List<Actuacion> actuaciones = caso.getActuaciones().stream()
				.filter(actuacion -> actuacion.getIdActuacion().equals(request.getIdActuacion()))
				.collect(Collectors.toList());
		if (actuaciones.isEmpty()) {
			throw new NotFoundException("No hay actuaciones registradas con el ID  : " + request.getIdActuacion());
		}
		Actuacion actuacion = actuaciones.get(0);
		List<Tarea> tareas = actuacion.getTareas().stream()
				.filter(item -> !item.isEliminado() && item.getIdTarea().equals(request.getIdTarea()))
				.collect(Collectors.toList());
		if (tareas.isEmpty()) {
			throw new NotFoundException("No hay tareas registradas con el ID_ACTUACION  : " + request.getIdActuacion());
		}
		Tarea tarea = tareas.get(0);
		int indexTarea = actuacion.getTareas().indexOf(tarea);
		int indexActuacion = caso.getActuaciones().indexOf(actuacion);
		tarea.setEliminado(request.isEliminado());
		actuacion.getTareas().set(indexTarea, tarea);
		caso.getActuaciones().set(indexActuacion, actuacion);
		Caso casoModified = modificar(caso);
		return transformVencimientoMap(casoModified.getActuaciones().get(indexActuacion).getTareas().get(indexTarea));
	}

	private Map<String, Object> transformVencimientoMap(Tarea dto) {
		Map<String, Object> mapFuncionario = new HashMap<String, Object>();
		mapFuncionario.put("id", dto.getIdTarea());
		mapFuncionario.put("fecha", fechaFormateada(dto.getFechaVencimiento()));
		mapFuncionario.put("descripcion", dto.getDenominacion());
		return mapFuncionario;
	}

	private List<Map<String, Object>> listCasosByMateria(String nombreMateria, List<Caso> casos) {
		List<Map<String, Object>> newCaso = new ArrayList<>();
		Map<String, Object> itemCaso;
		for (Caso caso : casos) {
			for (MateriaDto materia : caso.getMaterias()) {
				if (materia.getNombreMateria().equals(nombreMateria)) {
					itemCaso = new HashMap<String, Object>();
					itemCaso.put("nombreCaso", caso.getDescripcionCaso());
					itemCaso.put("ordenInspeccion", caso.getOrdenInspeccion());
					itemCaso.put("materiasEspecificas", new ArrayList<>());
					newCaso.add(itemCaso);
				}
			}
		}
		return newCaso.stream().distinct().collect(Collectors.toList());
	}

	private List<Map<String, Object>> transformToMap(List<Caso> casos) {
		List<Map<String, Object>> listMap = new ArrayList<>();
		Map<String, Object> mapa;
		for (EtapaResponse etapa : externalAws.listEtapas().stream().sorted(Comparator.comparing(EtapaResponse::getNroOrden))
				.collect(Collectors.toList())) {
			List<ResponseBar> listResoluciones = new ArrayList<>();
			if (!etapa.getNroOrden().equals(4)) {
				int contadorPrimera = 0;
				int contadorSegunda = 0;
				int contadorTercera = 0;
				mapa = new HashMap<>();
				if (etapa.getNroOrden() == 3) {
					for (Caso caso : casos) {
						List<Actuacion> actuacionesPorEtapa = caso.getActuaciones().stream()
								.filter(item -> (item.getEtapa().getId().equals(etapa.getIdEtapa())))
								.collect(Collectors.toList());
						if (!actuacionesPorEtapa.isEmpty()) {
							for (Actuacion actuacion : actuacionesPorEtapa) {
								EstadoCasoDto estado = actuacion.getEstadoCaso();
								if (estado.getOrden() == ID_PRIMERA_INSTANCIA) {
									log.info("Primera {},{}", estado, caso.getDescripcionCaso());
									if (contadorPrimera >= 1) {
										contadorPrimera = 0;
									}
									contadorPrimera++;
								}
								if (estado.getOrden() == ID_SEGUNDA_INSTANCIA) {
									log.info("Segunda {},{}", estado, caso.getDescripcionCaso());
									if (contadorSegunda >= 1) {
										contadorSegunda = 0;
									}
									contadorSegunda++;
								}
								if (estado.getOrden() == ID_TERCERA_INSTANCIA) {
									log.info("Tercera {},{}", estado, caso.getDescripcionCaso());
									if (contadorTercera >= 1) {
										contadorTercera = 0;
									}
									contadorTercera++;
								}
							}
						}
					}
					listResoluciones.add(ResponseBar.builder().name("1era").color("#466EFE")
							.data(Arrays.asList(contadorPrimera)).build());
					listResoluciones.add(ResponseBar.builder().name("2da").color("#FFB000")
							.data(Arrays.asList(contadorSegunda)).build());
					listResoluciones.add(ResponseBar.builder().name("3era").color("#8146FE")
							.data(Arrays.asList(contadorTercera)).build());
				}
				String idEtapa = etapa.getIdEtapa();
				int contadorCasos = cuentaCasos(idEtapa, casos);
				mapa.put("nombreEtapa", etapa.getNombreEtapa());
				mapa.put("cantidad", contadorCasos);
				mapa.put("porcentaje", getPorcentaje(contadorCasos, casos.size()));
				mapa.put("data", listResoluciones);
				listMap.add(mapa);
			}
		}
		return listMap;
	}

	private int cuentaCasos(String idEtapa, List<Caso> casos) {
		int contadorCasos = 0;
		for (Caso caso : casos) {
			int contadorActuaciones = 0;
			List<Actuacion> actuaciones = caso.getActuaciones();
			if (actuaciones != null) {
				if (!actuaciones.isEmpty()) {
					int sizeActuaciones = actuaciones.size();
					Actuacion actuacion = actuaciones.get(sizeActuaciones - 1);
					if (actuacion.getEtapa().getId().equals(idEtapa)) {
						contadorActuaciones++;
					}
				}
			}
			if (contadorActuaciones > 0) {
				contadorCasos++;
			}
		}
		return contadorCasos;
	};

	private List<MainActuacionResponse> transformMainActuacion(List<Actuacion> actuaciones) {
		List<MainActuacionResponse> mainActuaciones = new ArrayList<MainActuacionResponse>();
		Map<Object, List<Actuacion>> actuMap = actuaciones.stream()
				.collect(Collectors.groupingBy(actuacion -> añoFecha(actuacion.getFechaActuacion())));
		for (Map.Entry<Object, List<Actuacion>> entry : actuMap.entrySet()) {
			mainActuaciones.add(new MainActuacionResponse(String.valueOf(entry.getKey()),
					getListActuacionResponse(entry.getValue())));
		}
		return mainActuaciones;
	}

	public List<ActuacionResponse> getListActuacionResponse(List<Actuacion> actuaciones) {
		List<ActuacionResponse> lista = new ArrayList<ActuacionResponse>();
		int contador = 0;
		for (Actuacion actuacion : actuaciones) {
			ActuacionResponse actuacionResponse = transformDetalle(actuacion);
			actuacionResponse.setIsOpen(contador == 0);
			lista.add(actuacionResponse);
			contador++;
		}
		return lista;
	}

	private ActuacionResponse transformDetalle(Actuacion actuacion) {
		return ActuacionResponse.builder().idActuacion(actuacion.getIdActuacion())
				.dia(diaFecha(actuacion.getFechaActuacion())).mes(mesFecha(actuacion.getFechaActuacion()))
				.tipo(actuacion.getTipoActuacion().getNombreTipoActuacion())
				.etapa(actuacion.getEtapa().getNombreEtapa()).descripcionActuacion(actuacion.getDescripcion())
				.totalDocumentosActuacion(actuacion.getArchivos() == null ? 0 : actuacion.getArchivos().size())
				.totalDocumentosTareas(countDocumentosDeTareas(actuacion.getTareas()))
				.totalTareasRealizadas(countTareasRealizadas(actuacion.getTareas()).intValue())
				.detalles(transformDetalleActuacionResponse(actuacion.getTareas())).build();
	}

	private Long countTareasRealizadas(List<Tarea> tareas) {
		return tareas.stream().filter(estado -> estado.isEstado()).count();
	}

	private Integer countDocumentosDeTareas(List<Tarea> tareas) {
		int contador = 0;
		for (Tarea tarea : tareas) {
			if (tarea.isEstado() == false) {
				List<ArchivoAdjunto> archivos = tarea.getArchivos();
				if (archivos == null) {
					return 0;
				}
				if (archivos.size() > 0) {
					int total = (int) archivos.stream().filter(item -> !item.isEstado()).count();
					contador = contador + total;
				}
			}
		}
		return contador;
	}

	private List<DetalleActuacionResponse> transformDetalleActuacionResponse(List<Tarea> tareas) {
		List<DetalleActuacionResponse> detalle = new ArrayList<DetalleActuacionResponse>();
		for (Tarea tare : tareas) {
			detalle.add(transformFromTarea(tare));
		}
		return detalle;
	}

	private DetalleActuacionResponse transformFromTarea(Tarea tarea) {
		log.info(tarea.toString());
		return DetalleActuacionResponse.builder().idTarea(tarea.getIdTarea()).nombreTarea(tarea.getDenominacion())
				.cantidadDocumentos(tarea == null || tarea.getArchivos() == null ? 0 : tarea.getArchivos().size())
				.equipos(builder.getEquiposString(tarea.getEquipos()))
				.fechaRegistro(fechaFormateada(tarea.getFechaRegistro()))
				.fechaVencimiento(fechaFormateada(tarea.getFechaVencimiento())).estado(tarea.isEstado()).build();
	}

	public int getIndexActuacion(String idActuacion, List<Actuacion> actuaciones) {
		if (actuaciones.isEmpty()) {
			throw new BadRequestException("Este caso no tiene actuaciones registradas");
		}
		List<Actuacion> actuacionAux = actuaciones.stream().filter(item -> item.getIdActuacion().equals(idActuacion))
				.collect(Collectors.toList());
		if (actuacionAux.isEmpty()) {
			throw new NotFoundException("El id : " + idActuacion + " no se encuentra registrado");
		}
		return actuaciones.indexOf(actuacionAux.get(0));
	}

	public int getIndexTarea(String idTarea, List<Tarea> tareas) {
		if (tareas.isEmpty()) {
			throw new BadRequestException("Esta actuacion no tiene tareas registradas");
		}
		List<Tarea> tareaAux = tareas.stream().filter(item -> item.getIdTarea().equals(idTarea))
				.collect(Collectors.toList());
		if (tareaAux.isEmpty()) {
			throw new NotFoundException("El id de tarea : " + idTarea + " no se encuentra registrado");
		}
		return tareas.indexOf(tareaAux.get(0));
	}

	@Override
	public ActuacionResponseX2 añadirArchivoActuacion(UpdateFileActuacionRequest request) {
		Caso caso = verPodId(request.getIdCaso());
		List<Actuacion> actuaciones = caso.getActuaciones();
		if (actuaciones.isEmpty()) {
			throw new BadRequestException(
					"No hay actuaciones registradas para el caso ".concat(caso.getDescripcionCaso()));
		}
		List<Actuacion> actuacionFilter = actuaciones.stream()
				.filter(item -> item.getIdActuacion().equals(request.getIdActuacion())).collect(Collectors.toList());
		if (actuacionFilter.isEmpty()) {
			throw new BadRequestException(
					"No hay actuaciones registradas con el ID : ".concat(request.getIdActuacion()));
		}
		Actuacion actuacion = actuacionFilter.get(0);
		int indexActuacion = actuaciones.indexOf(actuacion);
		List<ArchivoAdjunto> archivos = actuacion.getArchivos();
		if (archivos.isEmpty()) {
			archivos = listArchivoAdjunto(request.getArchivos());
		} else {
			for (ArchivoAdjunto adj : listArchivoAdjunto(request.getArchivos())) {
				archivos.add(adj);
			}
		}
		actuacion.setArchivos(archivos);
		actuaciones.set(indexActuacion, actuacion);
		caso.setActuaciones(actuaciones);
		modificar(caso);
		List<ArchivoAdjunto> newListArchivo = new ArrayList<>();
		for (ArchivoAdjunto adj : archivos) {
			for (ArchivoBody body : request.getArchivos()) {
				if (body.getNombreArchivo().equals(adj.getNombreArchivo())) {
					newListArchivo.add(adj);
				}
			}
		}
		return ActuacionResponseX2.builder().id(actuacion.getIdActuacion())
				.archivos(newListArchivo.stream().distinct().collect(Collectors.toList())).build();
	}

	@Override
	public ActuacionResponseX3 editarActuacion(EditarActuacionRequest request) {
		Caso caso = verPodId(request.getIdCaso());
		List<Actuacion> actuaciones = caso.getActuaciones().stream()
				.filter(item -> item.getIdActuacion().equals(request.getIdActuacion())).collect(Collectors.toList());
		if (actuaciones.isEmpty()) {
			throw new NotFoundException("No hay actuaciones para este caso ID : " + caso.getId());
		}
		Actuacion actuacion = actuaciones.get(0);
		int indexActuacion = caso.getActuaciones().indexOf(actuacion);
		actuacion.setDescripcion(request.getDescripcionActuacion());
		caso.getActuaciones().set(indexActuacion, actuacion);
		Caso casoEdit = modificar(caso);
		ActuacionResponseProcessor processor = new ActuacionResponseProcessor(externalAws);
		return processor.transformActuacionResponseX3(casoEdit.getActuaciones().get(indexActuacion));
	}

	@Override
	public UpdateCasoResumenResponse updateResumen(UpdateCasoResumenRequest request) {
		Caso caso = verPodId(request.getIdCaso());
		caso.setDescripcionAdicional(request.getResumen());
		return UpdateCasoResumenResponse.builder().resumen(modificar(caso).getDescripcionAdicional()).build();
	}

	@Override
	public List<ActuacionResponseX3> verActuacionesPorIdCaso(String idCaso, ListActuacionesRequestFilter params) {
		log.info("Params : {}", params);
		Caso caso = verPodId(idCaso);
		List<Actuacion> actuaciones = caso.getActuaciones();
		List<ActuacionResponseX3> response = new ArrayList<ActuacionResponseX3>();
		ActuacionResponseProcessor processor = new ActuacionResponseProcessor(externalAws);
		Supplier<Stream<ActuacionResponseX3>> stream = () -> actuaciones.stream()
				.filter(item -> evaluateArrays(item, params)).map(item -> processor.transformActuacionResponseX3(item));
		Comparator<ActuacionResponseX3> comparator = new Comparator<ActuacionResponseX3>() {
			@Override
			public int compare(ActuacionResponseX3 o1, ActuacionResponseX3 o2) {
				return 0;
			}
		};
		if (params.getOrdenarPor() != null) {
			if (params.getOrdenarPor().getLabel() == null) {
				comparator = Comparator.comparing(ActuacionResponseX3::getFechaRegistro).reversed();
			} else {
				switch (params.getOrdenarPor().getValue()) {
				case "asc":
					comparator = Comparator.comparing(ActuacionResponseX3::getFechaRegistro);
					break;
				case "desc":
					comparator = Comparator.comparing(ActuacionResponseX3::getFechaRegistro).reversed();
					break;
				case "masAnexo":
					comparator = Comparator.comparing(ActuacionResponseX3::getAnexos).reversed();
					break;
				case "menosAnexo":
					comparator = Comparator.comparing(ActuacionResponseX3::getAnexos);
					break;
				default:
					comparator = Comparator.comparing(ActuacionResponseX3::getFechaRegistro).reversed();
					break;
				}
			}
		} else {
			comparator = Comparator.comparing(ActuacionResponseX3::getFechaRegistro);
		}
		response = stream.get().sorted(comparator).collect(Collectors.toList());
		if (response.isEmpty()) {
			return response;
		}
		response.get(0).setPrimerItem(true);
		return response;
	}

	private boolean evaluateArrays(Actuacion item, ListActuacionesRequestFilter params) {
		if (params.getTipoActuacion().isEmpty() && !params.getEtapaActuacion().isEmpty()) {
			return containsEtapa(item.getEtapa().getId(), params);
		} else if (params.getEtapaActuacion().isEmpty() && !params.getTipoActuacion().isEmpty()) {
			boolean rpta = containsTipoActuacion(item.getTipoActuacion().getId(), params);
			return rpta;
		} else if (!params.getTipoActuacion().isEmpty() && !params.getEtapaActuacion().isEmpty()) {
			boolean rpta = (containsEtapa(item.getEtapa().getId(), params)
					&& containsTipoActuacion(item.getTipoActuacion().getId(), params));
			;
			return rpta;
		} else {
			return true;
		}
	}

	private boolean containsEtapa(String idEtapa, ListActuacionesRequestFilter params) {
		long cantEtapas = params.getEtapaActuacion().stream().filter(item -> item.getValue().equals(idEtapa)).count();
		log.info("CAntidad Etapas : {}", cantEtapas);
		return cantEtapas == 1;
	}

	private boolean containsTipoActuacion(String idTipoActuacion, ListActuacionesRequestFilter params) {
		long cantTipos = params.getTipoActuacion().stream().filter(item -> item.getValue().equals(idTipoActuacion))
				.count();
		log.info("CAntidad Tipos : {}", cantTipos);
		return cantTipos == 1;
	}

	@Override
	public SaveTareaResponse verTareaPorIdV2(String idCaso, String idActuacion, String idTarea) {
		Caso caso = verPodId(idCaso);
		List<Actuacion> actuaciones = caso.getActuaciones();
		if (actuaciones.isEmpty()) {
			throw new BadRequestException("No hay actuaciones para este caso " + idCaso);
		}
		Actuacion actuacion = actuaciones.stream().filter(item -> item.getIdActuacion().equals(idActuacion))
				.collect(Collectors.toList()).get(0);
		List<Tarea> tareas = actuacion.getTareas();
		if (tareas.isEmpty()) {
			throw new BadRequestException("No hay tareas para este actuacion " + idActuacion);
		}
		Tarea tarea = tareas.stream().filter(item -> item.getIdTarea().equals(idTarea)).collect(Collectors.toList())
				.get(0);
		TareaResponseProcessor processor = new TareaResponseProcessor();
		return processor.transformResponse(tarea);
	}

	@Override
	public List<ItemsPorCantidadResponse> casosPorEmpresa(String userName) {
		List<Caso> casosPorUsuario = getUserNamePrincipal(userName);
		List<CasoDto> casosDto = casosPorUsuario.stream().map(item -> {
			String empresa = item.getEmpresas().isEmpty() ? "" : item.getEmpresas().get(0).getLabel();
			return CasoDto.builder().idCaso(item.getId()).nombreCaso(item.getDescripcionCaso()).empresa(empresa)
					.build();
		}).collect(Collectors.toList());
		Map<String, Long> agrupado = casosDto.stream()
				.collect(Collectors.groupingBy(CasoDto::getEmpresa, Collectors.counting()));

		int mayor = agrupado.entrySet().stream().map(item -> item.getValue()).max(Comparator.naturalOrder()).get()
				.intValue();
		List<ItemsPorCantidadResponse> listResponse = new ArrayList<>();
		for (Map.Entry<String, Long> entry : agrupado.entrySet()) {
			int cantidad = entry.getValue().intValue();
			listResponse.add(ItemsPorCantidadResponse.builder().nombreItem(entry.getKey())
					.cantidadNumber(getPorcentaje(cantidad, mayor)).cantidad(cantidad).build());
		}
		return listResponse;
	}

	@Override
	public List<ItemsPorCantidadResponse> casosPorTrabajdoresInvolucrados(String userName) {
		List<Caso> casosPorUsuario = getUserNamePrincipal(userName);

		List<CasoDto> casosDto = casosPorUsuario.stream().map(item -> {
			List<AnalisisRiesgo> listAnalisis = externalAws.tableInfraccion(item.getId());
			Integer sumTrabajadores = listAnalisis.stream()
					.flatMapToInt(element -> IntStream.of(element.getCantidadInvolucrados())).sum();
			return CasoDto.builder().idCaso(item.getId()).trabajadoresAfectados(sumTrabajadores)
					.nombreCaso(item.getDescripcionCaso()).build();
		}).collect(Collectors.toList());

		int mayorTrabajadoresInvolucrados = casosDto.stream().map(item -> item.getTrabajadoresAfectados())
				.max(Comparator.naturalOrder()).get();

		return casosDto.parallelStream().map(item -> {
			Integer trabajadoresAfectados = item.getTrabajadoresAfectados();
			return ItemsPorCantidadResponse.builder().nombreItem(item.getNombreCaso())
					.cantidadNumber(getPorcentaje(trabajadoresAfectados, mayorTrabajadoresInvolucrados))
					.cantidad(trabajadoresAfectados).build();
		}).collect(Collectors.toList());
	}

	/*
	 * Este metodo quedo deprecado por que no cumple con lo que se pide en el
	 * dashboard
	 */
	@Deprecated
	@Override
	public GraficoImpactoCarteraResponse verGraficoImpactoResponse(String userName) {
		List<Caso> casosPorUsuario = listarCasosPorUserName(userName).stream().filter(Caso::getEstadoCaso)
				.collect(Collectors.toList());
		List<CasoDto> casosDto = casosPorUsuario.stream().map(item -> {

			AnalisisRiesgo listAnalisis = externalAws.tableInfraccion(item.getId()).stream()
					.reduce((first, second) -> second)
					.orElse(AnalisisRiesgo.builder().sumaMultaPotencial(0.0).sumaProvision(0.0).build());

			// Double sumaMultaPotencial =
			// listAnalisis.stream().mapToDouble(AnalisisRiesgoPojo::getSumaMultaPotencial)
			// .sum();
			// Double sumaProvision =
			// listAnalisis.stream().mapToDouble(AnalisisRiesgoPojo::getSumaProvision).sum();
			Double sumaMultaPotencial = listAnalisis.getSumaMultaPotencial();
			Double sumaProvision = listAnalisis.getSumaProvision();
			return CasoDto.builder().idCaso(item.getId()).mesCaso(mesAnioFecha(item.getFechaInicio()))
					.multaPotencial(sumaMultaPotencial).provision(sumaProvision).fechaRegistro(item.getFechaInicio())
					.estado(item.getEstadoCaso()).build();
		}).sorted(Comparator.comparing(CasoDto::getFechaRegistro)).collect(Collectors.toList());
		Map<String, Long> mapCantidadMes = casosDto.stream()
				.collect(Collectors.groupingBy(CasoDto::getMesCaso, LinkedHashMap::new, Collectors.counting()));
		Map<String, Double> mapSumaMulta = casosDto.stream().collect(
				Collectors.groupingBy(CasoDto::getMesCaso, Collectors.summingDouble(CasoDto::getMultaPotencial)));
		Map<String, Double> mapSumaProvision = casosDto.stream()
				.collect(Collectors.groupingBy(CasoDto::getMesCaso, Collectors.summingDouble(CasoDto::getProvision)));
		log.info("MapCantidades {}", mapCantidadMes);
		log.info("MapSumaMulta {}", mapSumaMulta);
		log.info("MapSumaMultaProvision {}", mapSumaProvision);
		/*
		 * Para las series
		 */
		Map<String, Object> mapSerie = new HashMap<>();
		mapSerie.put("casosActivos",
				mapCantidadMes.entrySet().stream().map(item -> item.getValue()).collect(Collectors.toList()));
		mapSerie.put("multaPotencialAcumulada",
				mapSumaMulta.entrySet().stream().map(item -> round(item.getValue(), 2)).collect(Collectors.toList()));
		mapSerie.put("provisiones", mapSumaProvision.entrySet().stream().map(item -> {
			return round(item.getValue(), 2);
		}).collect(Collectors.toList()));
		return GraficoImpactoCarteraResponse.builder().series(mapSerie)
				.xAxisCategories(mapCantidadMes.entrySet().stream().map(Entry::getKey).collect(Collectors.toList()))
				.build();
	}

	@Override
	public GraficoImpactoCarteraResponse verGraficoImpactoResponseV2(String userName) {
		List<Caso> casosPorUsuario = getUserNamePrincipal(userName);
		List<CasoDto> casosDto = casosPorUsuario.stream().parallel().map(item -> {
			AnalisisRiesgo listAnalisis = externalAws.tableInfraccion(item.getId()).stream()
					.reduce((first, second) -> second)
					.orElse(AnalisisRiesgo.builder().sumaMultaPotencial(0.0).sumaProvision(0.0).build());
			Double sumaMultaPotencial = listAnalisis.getSumaMultaPotencial();
			Double sumaProvision = listAnalisis.getSumaProvision();
			return CasoDto.builder().idCaso(item.getId()).mesCaso(mesAnioFecha(item.getFechaInicio()))
					.multaPotencial(sumaMultaPotencial).provision(sumaProvision).fechaRegistro(item.getFechaInicio())
					.estado(item.getEstadoCaso()).build();
		}).sorted(Comparator.comparing(CasoDto::getFechaRegistro)).collect(Collectors.toList());

		Map<String, Long> mapCantidadMes = casosDto.stream()
				.collect(Collectors.groupingBy(CasoDto::getMesCaso, LinkedHashMap::new, Collectors.counting()));

		Map<String, Double> mapSumaMulta = casosDto.stream().filter(CasoDto::isEstado).collect(Collectors.groupingBy(
				CasoDto::getMesCaso, LinkedHashMap::new, Collectors.summingDouble(CasoDto::getMultaPotencial)));

		Map<String, Double> mapSumaProvision = casosDto.stream().filter(CasoDto::isEstado).collect(Collectors
				.groupingBy(CasoDto::getMesCaso, LinkedHashMap::new, Collectors.summingDouble(CasoDto::getProvision)));

		Map<String, Object> mapSeries = new HashMap<>();
		List<Object> itemMapSerie = new ArrayList<>();
		List<String> itemAxisCategory = new ArrayList<>();
		List<Double> provisiones = new ArrayList<>();
		List<Double> multaPotenciales = new ArrayList<>();

		double multaInicial = 0.0;
		for (Entry<String, Double> entry : mapSumaMulta.entrySet()) {
			double amountTotal = entry.getValue();
			multaInicial = multaInicial + amountTotal;
			multaPotenciales.add(multaInicial);
		}

		double provisionInicial = 0.0;
		for (Entry<String, Double> entry : mapSumaProvision.entrySet()) {
			log.info("Provision {}", entry);
			double amountTotal = entry.getValue();
			provisionInicial = provisionInicial + amountTotal;
			provisiones.add(provisionInicial);
		}

		/////////////////////////////////
		int countInicial = 0;
		for (Entry<String, Long> entry : mapCantidadMes.entrySet()) {
			// log.info("Item : {},{}", entry.getKey(), entry.getValue());
			String key = entry.getKey();
			int countTotal = entry.getValue().intValue();
			List<CasoDto> newList = casosDto.stream().filter(item -> item.getMesCaso().equals(key))
					.collect(Collectors.toList());
			int countActives = (int) newList.stream().filter(CasoDto::isEstado).count();
			int countInactives = newList.size() - countActives;
			itemMapSerie.add((countInicial + countTotal - countInactives));
			itemAxisCategory.add(key);
			countInicial = countTotal + countInicial - countInactives;
		}
		mapSeries.put("casosActivos", itemMapSerie);
		mapSeries.put("provisiones", provisiones);
		mapSeries.put("multaPotencialAcumulada", multaPotenciales);
		return GraficoImpactoCarteraResponse.builder().xAxisCategories(itemAxisCategory).series(mapSeries).build();
	}

	@Override
	public List<CasosConRiesgoResponse> dataTableCasosRiesgoResponse(String userName) {
		List<Caso> casosPorUsuario = getUserNamePrincipal(userName);
		return casosPorUsuario.stream().filter(Caso::getEstadoCaso).parallel().map(item -> {
			AnalisisRiesgo listAnalisis = externalAws.tableInfraccion(item.getId()).stream()
					.reduce((first, second) -> second)
					.orElse(AnalisisRiesgo.builder().sumaMultaPotencial(0.0).sumaProvision(0.0).build());
			// Double sumaMultaPotencial =
			// listAnalisis.stream().mapToDouble(AnalisisRiesgoPojo::getSumaMultaPotencial)
			// .sum();
			// Double sumaProvision =
			// listAnalisis.stream().mapToDouble(AnalisisRiesgoPojo::getSumaProvision).sum();
			Double sumaMultaPotencial = listAnalisis.getSumaMultaPotencial();
			Double sumaProvision = listAnalisis.getSumaProvision();
			String color = "";
			if (sumaMultaPotencial > 0 && sumaMultaPotencial <= 3000) {
				color = "green";
			} else if (sumaMultaPotencial >= 3001 && sumaMultaPotencial <= 5000) {
				color = "red";
			} else {
				color = "yellow";
			}
			return CasosConRiesgoResponse.builder().nombreCaso(item.getDescripcionCaso())
					.multaPotencial(formatMoneyV2(round(sumaMultaPotencial, 2)))
					.provisiones(formatMoneyV2(round(sumaProvision, 2))).color(color).build();
		}).collect(Collectors.toList());
	}

	@Override
	public GraficoCasosTemplateResponse evolucionCarteraResponse(String userName, String desde, String hasta) {
		LocalDate dateDesde = toLocalDateYYYYMMDD(desde);
		LocalDate dateHasta = toLocalDateYYYYMMDD(hasta);
		List<Caso> casosPorUsuario = getUserNamePrincipal(userName).stream()
				.filter(item -> item.getFechaInicio().isAfter(dateDesde.minusMonths(1))
						&& item.getFechaInicio().isBefore(dateHasta))
				.sorted(Comparator.comparing(Caso::getFechaInicio)).collect(Collectors.toList());
		List<CasoDto> casosDto = casosPorUsuario.stream().map(item -> {
			String fecha = mesAnioFecha(item.getFechaInicio());
			return CasoDto.builder().idCaso(item.getId()).mesCaso(fecha).fechaRegistro(item.getFechaInicio())
					.nombreCaso(item.getDescripcionCaso()).estado(item.getEstadoCaso()).build();
		}).collect(Collectors.toList());
		Map<String, Long> totalPorMeses = casosDto.stream()
				.collect(Collectors.groupingBy(CasoDto::getMesCaso, LinkedHashMap::new, Collectors.counting()));
		List<Map<String, Object>> listMap = new ArrayList<>();
		List<Integer> acumInicio = new ArrayList<>();
		List<Integer> acumNuevo = new ArrayList<>();
		List<Integer> acumCerrado = new ArrayList<>();
		for (String tipoCaso : Arrays.asList("Nuevos Casos", "Casos cerrados", "Casos al inicio del periodo")) {
			Map<String, Object> itemMap = new HashMap<>();
			itemMap.put("name", tipoCaso);
			if (tipoCaso.equals("Nuevos Casos")) {
				for (Map.Entry<String, Long> item : totalPorMeses.entrySet()) {
					acumNuevo.add(item.getValue().intValue());
				}
				itemMap.put("data", acumNuevo);
			} else if (tipoCaso.equals("Casos cerrados")) {
				for (Map.Entry<String, Long> item : totalPorMeses.entrySet()) {
					String fecha = item.getKey();
					Long cantidadCerrados = casosDto.stream()
							.filter(itemCaso -> !itemCaso.isEstado() && fecha.equals(itemCaso.getMesCaso())).count();
					acumCerrado.add(cantidadCerrados.intValue());
				}
				itemMap.put("data", acumCerrado);
			} else if (tipoCaso.equals("Casos al inicio del periodo")) {
				int contador = 0;
				for (@SuppressWarnings("unused")
				Map.Entry<String, Long> item : totalPorMeses.entrySet()) {
					if (contador == 0) {
						acumInicio.add(0);
					} else {
						int acumInicioAnterior = acumInicio.get(contador - 1);
						int acumNuevoAnterior = acumNuevo.get(contador - 1);
						int acumCerradoAnterior = acumCerrado.get(contador - 1);
						acumInicio.add(acumInicioAnterior + acumNuevoAnterior - acumCerradoAnterior);
					}
					contador++;
				}
				itemMap.put("data", acumInicio);
			}
			listMap.add(itemMap);
		}
		return GraficoCasosTemplateResponse.builder()
				.meses(totalPorMeses.entrySet().stream().map(Entry::getKey).collect(Collectors.toList()))
				.series(listMap).build();
	}

	@Override
	public GraficoCasosTemplateResponse materiasFiscalizadas(String userName) {
		List<Caso> casos = getUserNamePrincipal(userName);
		List<CasoDto> casosDto = casos.stream().map(item -> {
			String intendencia = item.getIntendencias() != null && item.getIntendencias().size() > 0
					? item.getIntendencias().get(0).getLabel()
					: "--";
			return CasoDto.builder().idCaso(item.getId()).intendencia(intendencia)
					.idMaterias(item.getMaterias().stream().map(mat -> mat.getId()).collect(Collectors.toList()))
					.build();
		}).collect(Collectors.toList());
		Map<String, Long> casosPorIntendencia = casosDto.stream()
				.collect(Collectors.groupingBy(CasoDto::getIntendencia, Collectors.counting()));
		List<ReactSelectRequest> materias = new ArrayList<>();
		for (Caso caso : casos) {
			for (MateriaDto materia : caso.getMaterias()) {
				materias.add(new ReactSelectRequest(materia.getId(), materia.getNombreMateria(), null));
			}
		}
		materias = materias.stream().filter(distinctByKey(p -> p.getValue()))
				.sorted(Comparator.comparing(ReactSelectRequest::getLabel)).collect(Collectors.toList());
		List<String> idMaterias = materias.stream().filter(distinctByKey(p -> p.getValue()))
				.map(item -> item.getValue()).collect(Collectors.toList());
		List<Map<String, Object>> seriesMapList = new ArrayList<>();
		for (Map.Entry<String, Long> map : casosPorIntendencia.entrySet()) {
			Map<String, Object> seriesMap = new HashMap<>();
			seriesMap.put("name", map.getKey());
			List<CasoDto> listDto = casosDto.stream().filter(item -> item.getIntendencia().equals(map.getKey()))
					.collect(Collectors.toList());
			log.info("ListDto {}", listDto);
			List<Integer> totales = new ArrayList<>();
			for (String idMateria : idMaterias) {
				int contadorCaso = 0;
				for (CasoDto dto : listDto) {
					long contador = dto.getIdMaterias().stream().filter(item -> item.equals(idMateria)).count();
					if (contador > 0) {
						contadorCaso++;
					}
				}
				totales.add(contadorCaso);
			}
			seriesMap.put("data", totales);
			seriesMapList.add(seriesMap);
		}

		return GraficoCasosTemplateResponse.builder()
				.meses(materias.stream().map(item -> item.getLabel()).collect(Collectors.toList()))
				.series(seriesMapList).build();
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {

		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public DocumentoAnexoResponse cambiarUrl(DocumentoAnexoRequest request) {
		log.info("CasoServiceImpl.cambiarUrl");
		Caso caso = verPodId(request.getIdCaso());
		if (caso.getActuaciones().isEmpty()) {
			throw new NotFoundException("No hay actuaciones registradas para el caso : " + caso.getDescripcionCaso());
		}
		List<Actuacion> actuaciones = caso.getActuaciones().stream()
				.filter(actuacion -> actuacion.getIdActuacion().equals(request.getIdActuacion()))
				.collect(Collectors.toList());
		if (actuaciones.isEmpty()) {
			throw new NotFoundException("No hay actuaciones registradas con el ID  : " + request.getIdActuacion());
		}
		Actuacion actuacion = actuaciones.get(0);
		List<ArchivoAdjunto> newArchivos = actuacion.getArchivos().stream()
				.filter(item -> item.getId().equals(request.getIdArchivo())).collect(Collectors.toList());
		if (newArchivos.isEmpty()) {
			throw new NotFoundException("No hay archivos registrados con el ID  : " + request.getIdArchivo());
		}
		ArchivoAdjunto archivo = newArchivos.get(0);
		/**
		 * Generando Imagen del archivio
		 */
		log.info("Archivo {}", archivo);
		ActuacionFileResponse response = externalAws.uploadFilePngActuacion(
				ActuacionFileRequest.builder().idArchivo(archivo.getId()).nombreArchivo(archivo.getNombreArchivo())
						.type(archivo.getTipoArchivo()).bucketName("archivos-samy-v1").build());
		archivo.setUrl(response.getUrl());
		int indexArchivo = actuacion.getArchivos().indexOf(archivo);
		int indexActuacion = caso.getActuaciones().indexOf(actuacion);
		caso.getActuaciones().get(indexActuacion).getArchivos().set(indexArchivo, archivo);
		Caso caseModified = modificar(caso);
		DocumentoReponseProcessor processor = new DocumentoReponseProcessor();
		return processor.transformDocumentosAnexos(caseModified.getActuaciones().get(indexActuacion).getArchivos()).stream()
				.filter(item -> item.getIdArchivo().equals(request.getIdArchivo())).findFirst()
				.orElse(DocumentoAnexoResponse.builder().build());
	}

	private List<Caso> getUserNamePrincipal(String userName) {
		Usuario usuarioPojo = externalAws.viewByUserName(userName);
		log.info("UsuarioPojo {}", usuarioPojo);
		String usuario = "";
		if (usuarioPojo.getIdUsuario() == null) {
			ColaboradorResponse colaboradorPojo = externalAws.viewColaboratorByUserName(userName);
			usuario = colaboradorPojo.getUserName();
		} else {
			usuario = usuarioPojo.getCorreo();
		}
		List<String> colaborators = externalAws.findColaboratorsByUserName(usuario);
		List<Caso> casosConcat = colaborators.parallelStream().flatMap(item -> {
			log.info("Dentro del flatMap {}", item);
			return listarCasosPorUserName(item).stream();
		}).collect(Collectors.toList());
		List<Caso> casosUsuario = listarCasosPorUserName(usuario);
		Stream<Caso> resultingStream = Stream.concat(casosConcat.stream(), casosUsuario.stream());
		return resultingStream.collect(Collectors.toList());
	}
}
