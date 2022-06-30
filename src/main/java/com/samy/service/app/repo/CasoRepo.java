package com.samy.service.app.repo;

import java.time.LocalDate;
import java.util.List;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;

import com.samy.service.app.model.Caso;

@EnableScan // Si no lo pones sale Error u.u
public interface CasoRepo extends GenericRepo<Caso, String> {

  List<Caso> findByUsuario(String nombreUsuario);

  List<Caso> findByUsuarioAndFechaInicio(String nombreUsuario, LocalDate fechaInicio);

  List<Caso> findByEmpresa(String empresa);
}
