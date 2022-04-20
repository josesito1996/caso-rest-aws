package com.samy.service.app.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 
 * @author Joselo
 *
 * @param <T>
 * @param <ID>
 */
@NoRepositoryBean
public interface GenericRepo<T, ID> extends CrudRepository<T, ID> {

}
