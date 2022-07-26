package com.samy.service.app.service.impl;

import static com.samy.service.app.util.Utils.convertActualZoneLocalDateTime;
import static com.samy.service.app.util.Utils.fechaFormateadaEstandar;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.samy.service.app.model.Caso;
import com.samy.service.app.model.response.DetallePorEmpresaResponse;
import com.samy.service.app.model.response.ReporteCasosEmpresa;
import com.samy.service.app.repo.CasoRepo;
import com.samy.service.app.service.ReporteService;
import com.samy.service.app.util.Utils;
import com.samy.service.samiusers.service.api.SeguimientoControllerApi;
import com.samy.service.samiusers.service.api.UsuarioControllerApi;
import com.samy.service.samiusers.service.model.Seguimiento;
import com.samy.service.samiusers.service.model.UsuariosPorEmpresaResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReporteServiceImpl implements ReporteService {

  @Autowired
  private UsuarioControllerApi usuarioApi;

  @Autowired
  private CasoRepo casoRepo;

  @Autowired
  private SeguimientoControllerApi seguimientoApi;


  @Override
  public DetallePorEmpresaResponse verPorEmpresa(String empresa) {
    return trasnformToResponse(usuarioApi.verPorEmpresa(empresa));
  }

  private DetallePorEmpresaResponse trasnformToResponse(UsuariosPorEmpresaResponse response) {
    List<Caso> casosPorEmpresa = casoRepo.findByEmpresa(response.getEmpresa());
    Long cantidadActuaciones = casosPorEmpresa.stream().flatMap(caso -> {
      return caso.getActuaciones().stream();
    }).count();

    log.info("Usuarios : {}", response.getUsuarios());
    return DetallePorEmpresaResponse.builder().empresa(response.getEmpresa())
        .fechaCreacion(response.getFechaCreacion()).cantidadCasos(casosPorEmpresa.size())
        .cantidadActuaciones(cantidadActuaciones.intValue())
        .usuarios(transformToMap(response.getUsuarios(), response.getEmpresa())).build();
  }

  private List<Map<String, Object>> transformToMap(List<String> usuarios, String empresa) {
    return usuarios.parallelStream().map(usuario -> {
      Map<String, Object> map = new HashMap<>();
      List<Caso> casosPorUsuario = casoRepo.findByUsuario(usuario);
      Long cantidadActuaciones = casosPorUsuario.stream().flatMap(caso -> {
        return caso.getActuaciones().stream();
      }).count();
      Seguimiento seguimiento = seguimientoApi.listarPorEmpresaYUsuario(empresa, usuario);
      LocalDateTime fechaLogueo = seguimiento.getFechaIngreso();
      String fechaFormateada = fechaLogueo == null ? ""
          : fechaFormateadaEstandar(convertActualZoneLocalDateTime(seguimiento.getFechaIngreso()));
      map.put("usuario", usuario);
      map.put("cantidadCasos", casosPorUsuario.size());
      map.put("ultimoLogueo", fechaFormateada);
      map.put("cantidadActuaciones", cantidadActuaciones.intValue());
      return map;
    }).collect(Collectors.toList());
  }

  @Override
  public ReporteCasosEmpresa reportePorEmpresa(String empresa) {
    List<Caso> casosPorEmpresa = casoRepo.findByEmpresa(empresa);
    int contadorMulta = 0;
    double sumaMulta = 0;
    for (Caso caso : casosPorEmpresa) {
      if (caso.getMultaPotencial() != null) {
        contadorMulta++;
        sumaMulta = sumaMulta + caso.getMultaPotencial().doubleValue();
      }
    }
    return ReporteCasosEmpresa.builder().conMulta(contadorMulta)
        .sumaMulta(Utils.redondearDecimales(sumaMulta, 2)).build();
  }

}
