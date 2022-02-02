package com.samy.service.app.service.impl;

import static com.samy.service.app.service.impl.ServiceUtils.cantidadDocumentos;
import static com.samy.service.app.service.impl.ServiceUtils.estaVencido;
import static com.samy.service.app.service.impl.ServiceUtils.etapaActuacion;
import static com.samy.service.app.service.impl.ServiceUtils.fechaActuacion;
import static com.samy.service.app.service.impl.ServiceUtils.nroOrdenEtapaActuacion;
import static com.samy.service.app.service.impl.ServiceUtils.siguienteVencimientoDelCaso;
import static com.samy.service.app.service.impl.ServiceUtils.tipoActuacion;
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
import static com.samy.service.app.util.Contants.diasPlazoVencimiento;
import static com.samy.service.app.util.Contants.fechaActual;
import static com.samy.service.app.util.ListUtils.listArchivoAdjunto;
import static com.samy.service.app.util.ListUtils.orderByDesc;
import static com.samy.service.app.util.Utils.añoFecha;
import static com.samy.service.app.util.Utils.diaFecha;
import static com.samy.service.app.util.Utils.fechaFormateada;
import static com.samy.service.app.util.Utils.fechaFormateadaOther;
import static com.samy.service.app.util.Utils.fechaFormateadaYYYMMDD;
import static com.samy.service.app.util.Utils.formatMoney;
import static com.samy.service.app.util.Utils.formatMoneyV2;
import static com.samy.service.app.util.Utils.getPorcentaje;
import static com.samy.service.app.util.Utils.mesAñoFecha;
import static com.samy.service.app.util.Utils.mesFecha;
import static com.samy.service.app.util.Utils.nombrePersona;
import static com.samy.service.app.util.Utils.randomBetWeen;
import static com.samy.service.app.util.Utils.round;
import static com.samy.service.app.util.Utils.transformToLocalTime;
import static com.samy.service.app.util.Utils.toLocalDateYYYYMMDD;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.samy.service.app.aws.AnalisisRiesgoPojo;
import com.samy.service.app.aws.EtapaPojo;
import com.samy.service.app.aws.ExternalDbAws;
import com.samy.service.app.aws.InfraccionItemPojo;
import com.samy.service.app.aws.InspectorPojo;
import com.samy.service.app.aws.MateriaPojo;
import com.samy.service.app.aws.UsuarioPojo;
import com.samy.service.app.exception.BadRequestException;
import com.samy.service.app.exception.NotFoundException;
import com.samy.service.app.external.ArchivoAdjunto;
import com.samy.service.app.external.EstadoCasoDto;
import com.samy.service.app.external.FuncionarioDto;
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
import com.samy.service.app.model.response.FuncionarioResponse;
import com.samy.service.app.model.response.GraficoImpactoCarteraResponse;
import com.samy.service.app.model.response.HomeCaseResponse;
import com.samy.service.app.model.response.ItemsPorCantidadResponse;
import com.samy.service.app.model.response.MainActuacionResponse;
import com.samy.service.app.model.response.MateriaResponse;
import com.samy.service.app.model.response.MiCarteraResponse;
import com.samy.service.app.model.response.NotificacionesVencimientosResponse;
import com.samy.service.app.model.response.ResponseBar;
import com.samy.service.app.model.response.SaveTareaResponse;
import com.samy.service.app.model.response.SubMateriaResponse;
import com.samy.service.app.model.response.UpdateCasoResumenResponse;
import com.samy.service.app.model.response.UpdateTareaResponse;
import com.samy.service.app.repo.CasoRepo;
import com.samy.service.app.repo.GenericRepo;
import com.samy.service.app.restTemplate.ExternalEndpoint;
import com.samy.service.app.restTemplate.model.ActuacionFileRequest;
import com.samy.service.app.restTemplate.model.ActuacionFileResponse;
import com.samy.service.app.service.CasoService;
import com.samy.service.app.service.LambdaService;
import com.samy.service.app.util.Contants;
import com.samy.service.app.util.ListPagination;

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
	
	@Autowired
	private ExternalEndpoint externalEndpoint;

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
		Caso casoRegistrado = registrar(builder.transformFromBody(request));
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
		return transformMap(registrar(builder.transformActuacion(caso, request)));
	}

	private ActuacionResponseX2 transformMap(Caso caso) {
		List<Actuacion> actuaciones = caso.getActuaciones();
		int ultimoItem = actuaciones.isEmpty() ? 0 : actuaciones.size() - 1;
		return ActuacionResponseX2.builder().id(actuaciones.get(ultimoItem).getIdActuacion())
				.archivos(archivos(actuaciones.get(ultimoItem).getArchivos())).build();
	}

	private Map<String, Object> transformMapTarea(Caso caso, TareaArchivoBody tareaArchivoBody) {
		Map<String, Object> newMap = new HashMap<String, Object>();
		List<Actuacion> actuaciones = caso.getActuaciones().stream()
				.filter(actu -> actu.getIdActuacion().equals(tareaArchivoBody.getId_actuacion()))
				.collect(Collectors.toList());
		if (!actuaciones.isEmpty()) {
			List<Tarea> tareas = actuaciones.get(0).getTareas().stream()
					.filter(tarea -> tarea.getIdTarea().equals(tareaArchivoBody.getId_tarea()))
					.collect(Collectors.toList());
			if (!tareas.isEmpty()) {
				List<ArchivoAdjunto> archivos = tareas.get(0).getArchivos();
				int indice = archivos.size() > 0 ? archivos.size() - 1 : 0;
				newMap.put("id", tareaArchivoBody.getId_tarea());
				newMap.put("archivos", archivos(Arrays.asList(archivos.get(indice))));
			}
		}
		return newMap;
	}

	private List<ArchivoAdjunto> archivos(List<ArchivoAdjunto> archivos) {
		return archivos.stream().map(this::transform).collect(Collectors.toList());
	}

	private ArchivoAdjunto transform(ArchivoAdjunto archivo) {
		return ArchivoAdjunto.builder()
				// .id(archivo.getId().concat(getExtension(archivo.getNombreArchivo())))
				.id(archivo.getId()).nombreArchivo(archivo.getNombreArchivo()).tipoArchivo(archivo.getTipoArchivo())
				.build();
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
		return registraTareaResponse(request, idActuacion, caso);
	}

	private SaveTareaResponse registraTareaResponse(TareaBody request, String idActuacion, Caso caso) {
		Caso casoRegistrado = registrar(builder.transformTarea(caso, request, idActuacion));
		List<Actuacion> actuaciones = casoRegistrado.getActuaciones().stream()
				.filter(item -> item.getIdActuacion().equals(idActuacion)).collect(Collectors.toList());
		List<Tarea> tareas = actuaciones.get(0).getTareas();
		int tareasSize = tareas.size();
		Tarea tarea = tareas.get(tareasSize - 1);
		return SaveTareaResponse.builder().idTarea(tarea.getIdTarea()).tipoTarea(tarea.getTipoTarea().getNombreTipo())
				.descripcion(tarea.getDenominacion())
				.destinatario(
						request.getEquipos().stream().map(item -> item.getDestinatario()).collect(Collectors.toList()))
				.correo(request.getEquipos().stream().map(item -> item.getCorreo()).collect(Collectors.toList()))
				.recordatorio(request.getRecordatorio().getDia()).mensaje(request.getMensaje())
				.fechaRegistro(fechaFormateada(tarea.getFechaRegistro()))
				.fechaVencimiento(fechaFormateada(tarea.getFechaVencimiento())).build();
	}

	/**
	 * Metodo que registra el archivo de una tarea ya registrada previamente.
	 */
	@Override
	public Map<String, Object> registrarArchivoTarea(TareaArchivoBody tareaArchivoBody) {
		Caso caso = verPodId(tareaArchivoBody.getId_caso());
		return transformMapTarea(registrar(builder.transformUpdateTarea(caso, tareaArchivoBody)), tareaArchivoBody);
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
		return transformFromCaso(registrar(caso));
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
		List<Caso> lista = listarCasosPorUserName(userName);
		return ListPagination.getPage(
				orderByDesc(lista.stream().map(this::transformToHomeCase).collect(Collectors.toList())), pageNumber,
				pageSize);
	}

	/**
	 * Metodo que muestra el detalle de un caso en especifico
	 */
	@Override
	public DetailCaseResponse mostratDetalleDelCasoPorId(String idCaso) {
		return transformFromCaso(verPodId(idCaso));
	}

	/**
	 * Metodo que lista las Notificacion y Vencimientos
	 */
	@Override
	public List<NotificacionesVencimientosResponse> listarNotificacionesVencimientos(String userName) {
		/*
		 * return listarCasosPorUserName(userName).stream()
		 * .map(this::transformNotificacionesVencimientosResponse)
		 * .collect(Collectors.toList());
		 */
		return test(listarCasosPorUserName(userName));
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
		List<Caso> casos = listarCasosPorUserName(userName);
		int totalCasos = casos.size();
		int totalCasosActivos = totalCasosPorEstado(casos, true);
		return MiCarteraResponse.builder().hasta(fechaFormateada(fechaActual))
				.casosActivos(String.valueOf(totalCasosActivos)).casosRegistrados(String.valueOf(totalCasos))
				.etapas(transformToMap(casos)).build();
	}

	@Override
	public CriticidadCasosResponse verCriticidadResponse(String userName) {
		List<Caso> casos = listarCasosPorUserName(userName);
		if (casos.isEmpty()) {
			return CriticidadCasosResponse.builder().build();
		}
		BigDecimal suma = casos.stream().map(item -> item.getMultaPotencial()).reduce(BigDecimal.ZERO, BigDecimal::add);
		double mayor = casos.stream().max(Comparator.comparing(Caso::getMultaPotencial)).get().getMultaPotencial()
				.doubleValue();
		return CriticidadCasosResponse.builder().total(formatMoney(suma.doubleValue()))
				.detalles(transformToMapCritidicidad(casos, mayor)).build();
	}

	@Override
	public List<Map<String, Object>> verCasosPorMateria(String userName) {
		List<Caso> casos = listarCasosPorUserName(userName);
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
		List<Caso> casos = listarCasosPorUserName(userName);
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
		UsuarioPojo usuarioPojo = externalAws.tableUsuario(caso.getUsuario());
		List<Actuacion> actuaciones = caso.getActuaciones();
		List<ActuacionResponseX3> response = actuaciones.stream()
				// .peek(item -> System.out.println(item))
				.map(item -> transformActuacionResponseX3(item,
						nombrePersona(usuarioPojo.getNombres(), usuarioPojo.getApellidos())))
				// .peek(item -> System.out.println(item.getFechaRegistro() + " - " +
				// item.getFechaActuacion()))
				.sorted(Comparator.comparing(ActuacionResponseX3::getFechaRegistro).reversed())
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
		return transformDocumentosAnexos(caseModified.getActuaciones().get(indexActuacion).getArchivos());
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

	private ActuacionResponseX3 transformActuacionResponseX3(Actuacion actuacion, String usuario) {
		List<ArchivoAdjunto> archivos = actuacion.getArchivos();
		String nombreArchivo = "";
		if (!archivos.isEmpty()) {
			for (ArchivoAdjunto adj : archivos) {
				if (adj.isEsPrincipal()) {
					nombreArchivo = adj.getNombreArchivo();
					break;
				}
			}
		}
		return ActuacionResponseX3.builder().idActuacion(actuacion.getIdActuacion()).documentoPrincipal(nombreArchivo)
				.fechaRegistro(fechaFormateadaOther(transformToLocalTime(actuacion.getFechaActuacion(),
						actuacion.getFechaRegistro().toLocalTime())))
				.fechaActuacion(fechaFormateada(actuacion.getFechaActuacion()))
				.nombreActuacion(actuacion.getDescripcion()).descripcion(actuacion.getDescripcionAux())
				.subidoPor(usuario)
				.anexos((int) actuacion.getArchivos().stream().filter(item -> !item.isEsPrincipal()).count())
				.tipoActuacion(actuacion.getTipoActuacion().getNombreTipoActuacion())
				.funcionarios(
						transformListFuncionarioMap(actuacion.getFuncionario(), actuacion.getEtapa().getNombreEtapa()))
				.vencimientos(transformListVencimientoMap(actuacion.getTareas()))// Asumo que son de
																					// las
																					// tareas.
				.documentosAnexos(transformDocumentosAnexos(actuacion.getArchivos())).build();
	}

	private List<DocumentoAnexoResponse> transformDocumentosAnexos(List<ArchivoAdjunto> archivos) {
		return archivos.stream().map(this::transfomrDocumentoAnexoResponse)
				.sorted(Comparator.comparing(DocumentoAnexoResponse::isEsPrincipal).reversed())
				.collect(Collectors.toList());
	}

	private DocumentoAnexoResponse transfomrDocumentoAnexoResponse(ArchivoAdjunto archivo) {
		String fechaRegistro = archivo.getFechaRegistro() != null ? fechaFormateada(archivo.getFechaRegistro()) : null;
		return DocumentoAnexoResponse.builder().idArchivo(archivo.getId()).type(archivo.getTipoArchivo())
				.nombreArchivo(archivo.getNombreArchivo()).tamaño(archivo.getTamaño()).fechaRegistro(fechaRegistro)
				.esPrincipal(archivo.isEsPrincipal()).url(archivo.getUrl()).build();
	}

	private List<Map<String, Object>> transformListFuncionarioMap(List<FuncionarioDto> funcionarios, String etapa) {
		return funcionarios.stream().map(item -> transformFuncionarioMap(item, etapa)).collect(Collectors.toList());
	}

	private Map<String, Object> transformFuncionarioMap(FuncionarioDto dto, String etapa) {
		InspectorPojo inspectorPojo = externalAws.tableInspector(dto.getId());
		Map<String, Object> mapFuncionario = new HashMap<String, Object>();
		mapFuncionario.put("idFuncionario", dto.getId());
		mapFuncionario.put("nombre", dto.getDatosFuncionario());
		mapFuncionario.put("cargo", inspectorPojo.getCargo());
		mapFuncionario.put("etapa", etapa);
		return mapFuncionario;
	}

	private List<Map<String, Object>> transformListVencimientoMap(List<Tarea> tareas) {
		return tareas.stream().filter(tarea -> !tarea.isEliminado())
				.sorted(Comparator.comparing(Tarea::getFechaVencimiento)).map(this::transformVencimientoMap)
				.collect(Collectors.toList());
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

	private List<Map<String, Object>> transformToMapCritidicidad(List<Caso> casos, double suma) {
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapa;
		for (Caso caso : casos) {
			mapa = new HashMap<String, Object>();
			mapa.put("nombreCaso", caso.getDescripcionCaso());
			mapa.put("montoMulta", formatMoney(caso.getMultaPotencial().doubleValue()));
			mapa.put("porcentaje", getPorcentaje(caso.getMultaPotencial().doubleValue(), suma));
			listMap.add(mapa);
		}
		return listMap;
	}

	private List<Map<String, Object>> transformToMap(List<Caso> casos) {
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapa;
		for (EtapaPojo etapa : externalAws.getTableEtapa().stream().sorted(Comparator.comparing(EtapaPojo::getNroOrden))
				.collect(Collectors.toList())) {
			List<ResponseBar> listResoluciones = new ArrayList<>();
			if (!etapa.getNroOrden().equals(4)) {
				int contadorPrimera = 0;
				int contadorSegunda = 0;
				int contadorTercera = 0;
				mapa = new HashMap<String, Object>();
				if (etapa.getNroOrden() == 3) {
					for (Caso caso : casos) {
						List<Actuacion> actuacionesPorEtapa = caso.getActuaciones().stream()
								.filter(item -> (item.getEtapa().getId().equals(etapa.getId_etapa())))
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
				String idEtapa = etapa.getId_etapa();
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

	private DetailCaseResponse transformFromCaso(Caso caso) {
		List<String> idMaterias = caso.getMaterias().stream().map(item -> item.getId()).collect(Collectors.toList());
		AnalisisRiesgoPojo mapInfraccion = externalAws.tableInfraccion(caso.getId());
		Integer totalMaterias = 0;
		Integer totalSubMaterias = 0;
		// List<MateriaResponse> materias =
		// materiasResponseBuild(transformToDto(caso.getMaterias()),
		// subMateriasBuild(caso.getMaterias()));
		List<MateriaResponse> materias = materias(mapInfraccion.getInfracciones());
		List<String> idMateriasV2 = materias.stream().map(item -> item.getIdMateria()).collect(Collectors.toList());
		List<String> unionId = Stream.concat(idMaterias.stream(), idMateriasV2.stream()).distinct()
				.collect(Collectors.toList());
		List<MateriaResponse> materiasNew = new ArrayList<>();
		for (String id : unionId) {
			MateriaResponse materiaResponse = materias.stream().filter(mat -> mat.getIdMateria().equals(id)).findFirst()
					.orElse(new MateriaResponse());
			if (materiaResponse.getIdMateria() != null) {
				materiasNew.add(materiaResponse);
			} else {
				MateriaPojo matPojo = externalAws.getTable(id);
				materiasNew.add(MateriaResponse.builder().idMateria(matPojo.getIdMateria())
						.nombreMateria(matPojo.getNombreMateria()).color(matPojo.getColor()).icono(matPojo.getIcono())
						.subMaterias(new ArrayList<>()).build());
			}
		}
		totalMaterias = materiasNew.size();
		for (MateriaResponse response : materiasNew) {
			totalSubMaterias = totalSubMaterias + response.getSubMaterias().size();
		}
		List<Actuacion> actuaciones = caso.getActuaciones();
		int sizeActuaciones = actuaciones.size();
		String etapaActuacion = actuaciones.isEmpty() ? ""
				: actuaciones.get(sizeActuaciones - 1).getEtapa().getNombreEtapa();
		Map<String, Object> mapEstado = new HashMap<String, Object>();
		EstadoCasoDto estado = actuaciones.isEmpty() ? EstadoCasoDto.builder().build()
				: actuaciones.get(sizeActuaciones - 1).getEstadoCaso();
		mapEstado.put("numero", estado.getOrden());
		mapEstado.put("estadoCaso", estado.getNombreEstado());

		return DetailCaseResponse.builder().idCaso(caso.getId()).nombreCaso(caso.getDescripcionCaso())
				.descripcion(caso.getDescripcionAdicional()).fechaCreacion(fechaFormateada(caso.getFechaInicio()))
				.ordenInspeccion(caso.getOrdenInspeccion()).tipoActuacion(tipoActuacion(caso.getActuaciones()))
				.cantidadDocumentos(cantidadDocumentos(caso.getActuaciones()))
				.funcionarios(funcionariosResponseList(caso))
				.trabajadoresInvolucrados(mapInfraccion.getCantidadInvolucrados())
				.sumaMultaPotencial(mapInfraccion.getSumaMultaPotencial())
				.sumaProvision(mapInfraccion.getSumaProvision()).riesgo(mapInfraccion.getNivelRiesgo())
				.origen(mapInfraccion.getOrigenCaso()).materiasResponse(materiasNew).totalMaterias(totalMaterias)
				.totalSubMaterias(totalSubMaterias).etapa(etapaActuacion).estadoCaso(mapEstado).build();
	}

	private List<MateriaResponse> materias(List<InfraccionItemPojo> items) {
		List<MateriaResponse> materias = new ArrayList<>();
		for (InfraccionItemPojo item : items) {
			MateriaPojo materiaPojo = externalAws.getTable(item.getMateria().getValue());
			ReactSelectRequest materia = item.getMateria();
			ReactSelectRequest subMateria = item.getSubMaterias();
			materias.add(MateriaResponse
					.builder().idMateria(materia.getValue()).color(materiaPojo.getColor()).icono(materiaPojo.getIcono())
					.nombreMateria(materia.getLabel()).subMaterias(Arrays.asList(SubMateriaResponse.builder()
							.idSubMateria(subMateria.getValue()).nombreSubMAteria(subMateria.getLabel()).build()))
					.build());
		}
		List<MateriaResponse> newMaterias = new ArrayList<>();
		for (MateriaResponse mat : materias) {
			List<SubMateriaResponse> subMaterias = new ArrayList<>();
			if (!validateMat(newMaterias, mat.getIdMateria())) {
				for (SubMateriaResponse subMateria : mat.getSubMaterias()) {
					if (!validateSub(subMaterias, subMateria.getIdSubMateria())) {
						subMaterias.add(subMateria);
					}
				}
				mat.setSubMaterias(subMaterias);
				newMaterias.add(mat);
			} else {
				int index = 0;
				for (int i = 0; i <= newMaterias.size(); i++) {
					if (newMaterias.get(i).getIdMateria().equals(mat.getIdMateria())) {
						index = i;
						break;
					}
				}
				List<SubMateriaResponse> lisAux = newMaterias.get(index).getSubMaterias();
				for (SubMateriaResponse subMateria : mat.getSubMaterias()) {
					if (!validateSub(lisAux, subMateria.getIdSubMateria())) {
						lisAux.add(subMateria);
					}
				}
				mat.setSubMaterias(lisAux);
				newMaterias.set(index, mat);
			}
		}
		return newMaterias;
	}

	private boolean validateMat(List<MateriaResponse> materias, String idMateria) {
		for (MateriaResponse mat : materias) {
			if (mat.getIdMateria().equals(idMateria)) {
				return true;
			}
		}
		return false;
	}

	private boolean validateSub(List<SubMateriaResponse> subMaterias, String idSubMateria) {
		for (SubMateriaResponse sub : subMaterias) {
			if (sub.getIdSubMateria().equals(idSubMateria)) {
				return true;
			}
		}
		return false;
	}

	private List<FuncionarioResponse> funcionariosResponseList(Caso caso) {
		List<Actuacion> actuaciones = caso.getActuaciones();
		List<FuncionarioResponse> funcionarios = new ArrayList<>();
		actuaciones.sort(Comparator.comparing(Actuacion::getFechaRegistro).reversed());
		for (Actuacion actuacion : actuaciones) {
			for (FuncionarioDto func : actuacion.getFuncionario()) {
				List<FuncionarioResponse> funcis = funcionarios.stream()
						.filter(item -> item.getIdFuncionario().equals(func.getId())).collect(Collectors.toList());
				InspectorPojo inspectorPojo = externalAws.tableInspector(func.getId());
				log.info("Table" + inspectorPojo);
				if (!funcis.isEmpty()) {
					funcionarios.add(FuncionarioResponse.builder().idFuncionario(inspectorPojo.getId())
							.nombreFuncionario(inspectorPojo.getNombreInspector()).cargo(inspectorPojo.getCargo())
							.etapaActuacion(actuacion.getEtapa().getNombreEtapa()).build());
				} else {
					funcionarios.add(FuncionarioResponse.builder().idFuncionario(func.getId())
							.nombreFuncionario(inspectorPojo.getNombreInspector()).cargo(inspectorPojo.getCargo())
							.etapaActuacion(actuacion.getEtapa().getNombreEtapa()).build());
				}
			}
		}
		return funcionarios.stream().distinct().collect(Collectors.toList());
	}

	private HomeCaseResponse transformToHomeCase(Caso caso) {
		String nombreEmpresa = caso.getEmpresas().stream().map(item -> item.getLabel()).findFirst().orElse("");
		AnalisisRiesgoPojo pojoAnalisis = externalAws.tableInfraccion(caso.getId());
		String nivelRiesgo = pojoAnalisis.getIdAnalisis() != null ? pojoAnalisis.getNivelRiesgo().getLabel() : "";
		String colorRiesgo = Contants.mapRiesgo.get(nivelRiesgo);
		String siguienteVencimiento = siguienteVencimientoDelCaso(caso.getActuaciones());
		return HomeCaseResponse.builder().idCaso(caso.getId()).idCaso(caso.getId()).nombreEmpresa(nombreEmpresa)
				.nombreCaso(caso.getDescripcionCaso()).nroOrdenInspeccion(caso.getOrdenInspeccion())
				.utltimaActuacion(fechaActuacion(caso.getActuaciones()))
				.tipoActuacion(tipoActuacion(caso.getActuaciones()))
				.nroEtapa(nroOrdenEtapaActuacion(caso.getActuaciones()))
				.etapaActuacion(etapaActuacion(caso.getActuaciones())).riesgo(nivelRiesgo).colorRiesgo(colorRiesgo)
				.siguienteVencimiento(siguienteVencimiento).estaVencido(estaVencido(siguienteVencimiento)).build();
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

	private List<NotificacionesVencimientosResponse> test(List<Caso> casos) {
		List<Caso> listaCaso = casos;
		List<NotificacionesVencimientosResponse> notiVenci = new ArrayList<NotificacionesVencimientosResponse>();
		for (Caso caso : listaCaso) {
			List<Actuacion> actuaciones = caso.getActuaciones();
			for (Actuacion actuacion : actuaciones) {
				List<Tarea> tareas = actuacion.getTareas();
				for (Tarea tarea : tareas) {
					LocalDate fechaVencimiento = tarea.getFechaVencimiento().toLocalDate();
					LocalDate fechaAumentada = fechaActual.plusDays(diasPlazoVencimiento);
					/**
					 * Validar este tema
					 */
					if (fechaVencimiento.isAfter(fechaActual) && fechaVencimiento.isBefore(fechaAumentada)) {
						notiVenci.add(NotificacionesVencimientosResponse.builder().idCaso(caso.getId())
								.idActuacion(actuacion.getIdActuacion()).idTarea(tarea.getIdTarea())
								.fechaVencimiento(fechaFormateada(tarea.getFechaVencimiento()))
								.nombreCaso(caso.getDescripcionCaso()).descripcion(getObject(tarea)).build());
					}
				}
			}
		}
		return notiVenci.stream().sorted(Comparator.comparing(NotificacionesVencimientosResponse::getFechaVencimiento))
				.collect(Collectors.toList());
	}

	public Map<String, Object> getObject(Tarea tarea) {
		Map<String, Object> lista = new HashMap<String, Object>();
		lista.put("cabecera", "Tarea por vencer");
		lista.put("contenido", tarea.getDenominacion());
		return lista;
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
		return transformActuacionResponseX3(casoEdit.getActuaciones().get(indexActuacion), casoEdit.getUsuario());
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
		Supplier<Stream<ActuacionResponseX3>> stream = () -> actuaciones.stream()
				.filter(item -> evaluateArrays(item, params))
				.map(item -> transformActuacionResponseX3(item, caso.getUsuario()));
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
		return SaveTareaResponse.builder().idTarea(tarea.getIdTarea()).tipoTarea(tarea.getTipoTarea().getNombreTipo())
				.descripcion(tarea.getDenominacion())
				.destinatario(tarea.getEquipos().stream().map(item -> item.getNombre()).collect(Collectors.toList()))
				.correo(tarea.getEquipos().stream().map(item -> item.getCorreo()).collect(Collectors.toList()))
				.recordatorio(tarea.getRecordatorio().getDia()).mensaje(tarea.getMensaje())
				.fechaRegistro(fechaFormateada(tarea.getFechaRegistro()))
				.fechaVencimiento(fechaFormateada(tarea.getFechaVencimiento())).build();
	}

	@Override
	public List<ItemsPorCantidadResponse> casosPorEmpresa(String userName) {
		List<Caso> casosPorUsuario = listarCasosPorUserName(userName);
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
		List<Caso> casosPorUsuario = listarCasosPorUserName(userName);

		List<CasoDto> casosDto = casosPorUsuario.stream().map(item -> {
			AnalisisRiesgoPojo analisis = externalAws.tableInfraccion(item.getId());
			return CasoDto.builder().idCaso(item.getId()).trabajadoresAfectados(analisis.getCantidadInvolucrados())
					.nombreCaso(item.getDescripcionCaso()).build();
		}).collect(Collectors.toList());

		int mayorTrabajadoresInvolucrados = casosDto.stream().map(item -> item.getTrabajadoresAfectados())
				.max(Comparator.naturalOrder()).get();

		return casosDto.parallelStream().map(item -> {
			Integer trabajadoresAfectados = item.getTrabajadoresAfectados();
			return ItemsPorCantidadResponse.builder().nombreItem(item.getNombreCaso())
					.cantidadNumber(getPorcentaje(trabajadoresAfectados, mayorTrabajadoresInvolucrados))
					.cantidad(item.getTrabajadoresAfectados()).build();
		}).collect(Collectors.toList());
	}

	@Override
	public GraficoImpactoCarteraResponse verGraficoImpactoResponse(String userName) {
		List<Caso> casosPorUsuario = listarCasosPorUserName(userName).stream().collect(Collectors.toList());
		List<CasoDto> casosDto = casosPorUsuario.stream().map(item -> {
			return CasoDto.builder().idCaso(item.getId()).mesCaso(mesAñoFecha(item.getFechaInicio()))
					.multaPotencial(item.getMultaPotencial().doubleValue()).fechaRegistro(item.getFechaInicio())
					.build();
		}).sorted(Comparator.comparing(CasoDto::getFechaRegistro)).collect(Collectors.toList());
		Map<String, Long> mapCantidadMes = casosDto.stream()
				.collect(Collectors.groupingBy(CasoDto::getMesCaso, LinkedHashMap::new, Collectors.counting()));
		Map<String, Double> mapSumaMulta = casosDto.stream().collect(
				Collectors.groupingBy(CasoDto::getMesCaso, Collectors.summingDouble(item -> item.getMultaPotencial())));
		log.info("MapCantidades {}", mapCantidadMes);
		log.info("MapSumaMulta {}", mapSumaMulta);
		/*
		 * Para las series
		 */
		Map<String, Object> mapSerie = new HashMap<>();
		mapSerie.put("casosActivos",
				mapCantidadMes.entrySet().stream().map(item -> item.getValue()).collect(Collectors.toList()));
		mapSerie.put("multaPotencialAcumulada",
				mapSumaMulta.entrySet().stream().map(item -> round(item.getValue(), 2)).collect(Collectors.toList()));
		mapSerie.put("provisiones", mapCantidadMes.entrySet().stream().map(item -> {
			return randomBetWeen(1500, 5000);
		}).collect(Collectors.toList()));
		return GraficoImpactoCarteraResponse.builder().series(mapSerie)
				.xAxisCategories(
						mapCantidadMes.entrySet().stream().map(item -> item.getKey()).collect(Collectors.toList()))
				.build();
	}

	@Override
	public List<CasosConRiesgoResponse> dataTableCasosRiesgoResponse(String userName) {
		List<Caso> casosPorUsuario = listarCasosPorUserName(userName);
		return casosPorUsuario.stream().map(item -> {
			String color = "";
			double multa = item.getMultaPotencial().doubleValue();
			if (multa > 0 && multa <= 3000) {
				color = "green";
			} else if (multa >= 3001 && multa <= 5000) {
				color = "red";
			} else {
				color = "yellow";
			}
			return CasosConRiesgoResponse.builder().nombreCaso(item.getDescripcionCaso())
					.multaPotencial(formatMoneyV2(round(item.getMultaPotencial().doubleValue(), 2)))
					.provisiones(formatMoneyV2(randomBetWeen(1500, 5000))).color(color).build();
		}).collect(Collectors.toList());
	}

	@Override
	public GraficoCasosTemplateResponse evolucionCarteraResponse(String userName, String desde, String hasta) {
		LocalDate dateDesde = toLocalDateYYYYMMDD(desde);
		LocalDate dateHasta = toLocalDateYYYYMMDD(hasta);
		List<Caso> casosPorUsuario = listarCasosPorUserName(userName).stream()
				.filter(item -> item.getFechaInicio().isAfter(dateDesde.minusMonths(1))
						&& item.getFechaInicio().isBefore(dateHasta))
				.sorted(Comparator.comparing(Caso::getFechaInicio)).collect(Collectors.toList());
		List<CasoDto> casosDto = casosPorUsuario.stream().map(item -> {
			String fecha = mesAñoFecha(item.getFechaInicio());
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
							.filter(itemCaso -> !itemCaso.getEstado() && fecha.equals(itemCaso.getMesCaso())).count();
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
				.meses(totalPorMeses.entrySet().stream().map(item -> item.getKey()).collect(Collectors.toList()))
				.series(listMap).build();
	}

	@Override
	public GraficoCasosTemplateResponse materiasFiscalizadas(String userName) {
		List<Caso> casos = listarCasosPorUserName(userName);
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
		ActuacionFileResponse response = externalEndpoint.uploadFilePngActuacion(ActuacionFileRequest.builder()
        		.idArchivo(archivo.getId())
        		.nombreArchivo(archivo.getNombreArchivo())
        		.type(archivo.getTipoArchivo())
        		.build());
		archivo.setUrl(response.getUrl());
		int indexArchivo = actuacion.getArchivos().indexOf(archivo);
		int indexActuacion = caso.getActuaciones().indexOf(actuacion);
		caso.getActuaciones().get(indexActuacion).getArchivos().set(indexArchivo, archivo);
		Caso caseModified = modificar(caso);
		return transformDocumentosAnexos(caseModified.getActuaciones().get(indexActuacion).getArchivos()).stream()
				.filter(item -> item.getIdArchivo().equals(request.getIdArchivo())).findFirst()
				.orElse(DocumentoAnexoResponse.builder().build());
	}
}
