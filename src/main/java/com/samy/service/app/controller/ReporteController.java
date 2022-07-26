package com.samy.service.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.samy.service.app.model.response.DetallePorEmpresaResponse;
import com.samy.service.app.model.response.ReporteCasosEmpresa;
import com.samy.service.app.service.ReporteService;

@RestController
@RequestMapping("/api-reporte")
public class ReporteController {

  @Autowired
  private ReporteService service;
  
  @GetMapping("/verPorEmpresa/{empresa}")
  public DetallePorEmpresaResponse verPorEmpresa(@PathVariable String empresa) {
    return service.verPorEmpresa(empresa);
  }
  
  @GetMapping("/verReportePorEmpresa/{empresa}")
  public ReporteCasosEmpresa reportePorEmpresa(@PathVariable String empresa) {
    return service.reportePorEmpresa(empresa);
  }
  
}
