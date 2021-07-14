package com.samy.service.app.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.samy.service.app.model.Personal;
import com.samy.service.app.repo.GenericRepo;
import com.samy.service.app.repo.PersonalRepo;
import com.samy.service.app.service.PersonalService;

@Service
public class PersonalServiceImpl extends CrudImpl<Personal, String> implements PersonalService {

    @Autowired
    private PersonalRepo repo;

    @Override
    protected GenericRepo<Personal, String> getRepo() {
        return repo;
    }

    @Override
    public Personal verUnoPorId(String id) {
        Optional<Personal> optional = verPorId(id);
        return optional.isPresent() ? optional.get() : new Personal();
    }

}
