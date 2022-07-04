package com.samy.service.app.service;

import com.samy.service.app.model.response.DetallePorEmpresaResponse;

public interface ReporteService {
  DetallePorEmpresaResponse verPorEmpresa(String empresa);
}
