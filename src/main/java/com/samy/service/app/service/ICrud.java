package com.samy.service.app.service;

import java.util.List;
import java.util.Optional;

public interface ICrud <T,ID>{

  public T registrar(T t);
  
  public T modificar(T t);
  
  public List<T> listar();
  
  public Optional<T> verPorId(ID id);
  
}
