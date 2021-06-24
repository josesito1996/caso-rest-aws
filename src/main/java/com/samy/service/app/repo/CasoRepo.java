package com.samy.service.app.repo;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;

import com.samy.service.app.model.Caso;

@EnableScan//Si no lo pones sale Error u.u
public interface CasoRepo extends GenericRepo<Caso, String> {

}
