package com.samy.service.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CasoServiceAwsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CasoServiceAwsApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception {
//	Caso casito = new Caso(null,
//			LocalDate.now(),
//			"000321",
//			new InspectorDto(null,"INSPECTOR EJEMPLO","AUXILIAR"),
//			new InspectorDto(null,"INSPECTOR EJEMPLO","AUXILIAR"),
//			"EJEMPLO DE MATERIAS",
//			"EJEMPLO DE DENOMINIACION",
//			new Actuacion(LocalDate.now(),
//					LocalDateTime.now(),
//					"Descripcion",
//					new FuncionarioDto("", "FUNIONARIO DAME"),
//					new TipoActuacionDto("", "TIPO ACTUACION_EXAMPLE"),
//					"INICIO",
//					Arrays.asList(new ArchivoAdjunto("0001","NOMBRE","TIPO","URL")),
//					null));
//	repo.save(casito);
//		
//	}

}
