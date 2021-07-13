package com.samy.service.app.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.samy.service.app.repo.GenericRepo;
import com.samy.service.app.service.ICrud;

public abstract class CrudImpl<T, ID> implements ICrud<T, ID> {

    protected abstract GenericRepo<T, ID> getRepo();

    @Override
    public T registrar(T t) {
        return getRepo().save(t);
    };

    @Override
    public T modificar(T t) {
        return getRepo().save(t);
    };

    @Override
    public List<T> listar() {
        return StreamSupport.stream(getRepo().findAll().spliterator(), false)
                .collect(Collectors.toList());
    };

    @Override
    public Optional<T> verPorId(ID id) {
        return getRepo().findById(id);
    };

}
