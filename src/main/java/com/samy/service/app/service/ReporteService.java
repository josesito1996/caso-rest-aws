package com.samy.service.app.service;

import com.samy.service.app.model.response.DetallePorEmpresaResponse;
import com.samy.service.app.model.response.ReporteCasosEmpresa;

public interface ReporteService {
  DetallePorEmpresaResponse verPorEmpresa(String empresa);
  
  ReporteCasosEmpresa reportePorEmpresa(String empresa);
}
