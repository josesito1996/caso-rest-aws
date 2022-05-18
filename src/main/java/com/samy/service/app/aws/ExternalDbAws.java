package com.samy.service.app.aws;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.samy.service.samifiles.service.api.ProcessControllerApi;
import com.samy.service.samifiles.service.model.ActuacionFileRequest;
import com.samy.service.samifiles.service.model.ActuacionFileResponse;
import com.samy.service.samiprimary.service.api.AnalisisRiesgoControllerApi;
import com.samy.service.samiprimary.service.api.EtapaControllerApi;
import com.samy.service.samiprimary.service.api.InspectorControllerApi;
import com.samy.service.samiprimary.service.api.MateriaControllerApi;
import com.samy.service.samiprimary.service.model.AnalisisRiesgo;
import com.samy.service.samiprimary.service.model.EtapaResponse;
import com.samy.service.samiprimary.service.model.InspectorResponse;
import com.samy.service.samiprimary.service.model.MateriaResponse;
import com.samy.service.samiusers.service.api.UsuarioControllerApi;
import com.samy.service.samiusers.service.model.ColaboradorResponse;
import com.samy.service.samiusers.service.model.UserResponseBody;
import com.samy.service.samiusers.service.model.Usuario;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExternalDbAws {

	@Autowired
	private MateriaControllerApi materiaApi;

	@Autowired
	private AnalisisRiesgoControllerApi analisisRiesgoApi;

	@Autowired
	private EtapaControllerApi etapaApi;

	@Autowired
	private InspectorControllerApi inspectorApi;

	@Autowired
	private ProcessControllerApi processApi;

	@Autowired
	private UsuarioControllerApi usuarioApi;

	public MateriaResponse getTable(String idMateria) {
		MateriaResponse materia = materiaApi.buscarPorId1(idMateria);
		return materia == null ? new MateriaResponse() : materia;
	}

	public List<AnalisisRiesgo> tableInfraccion(String idCaso) {
		log.info("ExternalDbAws.tableInfraccion");
		List<AnalisisRiesgo> analisisList = analisisRiesgoApi.listarPorIdCaso(idCaso);
		if (analisisList == null || analisisList.isEmpty()) {
			return new ArrayList<>();
		}
		return analisisList;
	}

	public List<EtapaResponse> listEtapas() {
		log.info("ExternalDbAws.listEtapas");
		List<EtapaResponse> etapas = etapaApi.listarTodos5();
		if (etapas == null || etapas.isEmpty()) {
			return new ArrayList<>();
		}
		log.info("Etpas {}", etapas);
		return etapas;
	}

	public InspectorResponse tableInspector(String idInspector) {
		log.info("ExternalDbAws.tableInspector");
		InspectorResponse response = inspectorApi.buscarPorId2(idInspector);
		if (response == null) {
			return InspectorResponse.builder().build();
		}
		return response;
	}

	public ActuacionFileResponse uploadFilePngActuacion(ActuacionFileRequest request) {
		log.info("ExternalDbAws.uploadFilePngActuacion {} ", new Gson().toJson(request));
		return processApi.uploadFile(request);
	}

	public Usuario viewByUserName(String userName) {
		log.info("ExternalDbAws.viewByUserName {}", userName);
		Usuario usuario = usuarioApi.findByUserName(userName);
		return usuario == null ? new Usuario() : usuario;
	}

	public ColaboradorResponse viewColaboratorByUserName(String userName) {
		log.info("ExternalDbAws.viewColaboratorByUserName {}", userName);
		ColaboradorResponse colaborador = usuarioApi.findColaboratorByUserName(userName);
		return colaborador == null ? ColaboradorResponse.builder().build() : colaborador;
	}

	public List<String> findColaboratorsByUserName(String userName) {
		log.info("ExternalDbAws.findColaboratorsByUserName {}", userName);
		return usuarioApi.findColaboratorsByUserName(userName);
	}

	public UserResponseBody getUser(String userName) {
		log.info("ExternalDbAws.getUser {}", userName);
		UserResponseBody userResponse = usuarioApi.verUsuarioPorUserName(userName);
		return userResponse == null ? UserResponseBody.builder().build() : userResponse;
	}
}
