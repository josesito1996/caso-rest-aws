package com.samy.service.app.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.samy.service.app.exception.NotFoundException;
import com.samy.service.app.external.EquipoDto;
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
        if (!optional.isPresent()) {
            throw new NotFoundException("Personal no existe " + id);
        }
        return optional.get();
    }

    @Override
    public List<String> listarPersonal(List<EquipoDto> equipos) {
        return equipos.stream().map(item -> verUnoPorId(item.getIdEquipo()).getDatos())
                .collect(Collectors.toList());
    }

    @Override
    public Personal registrarPersonal(Personal personal) {
        Personal personalFound = repo.findByCorreo(personal.getCorreo());
        if (personalFound != null) {
            return personalFound;
        }
        return registrar(personal);
    }

}
