package com.samy.service.app.util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Contants {

	public static final LocalDate fechaActual = LocalDate.now();

	public static final int diasPlazoVencimiento = 8;

	public static final String REGEX_UUID = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$";

	public static final String REGEX_EMAIL = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)"
			+ "+[a-zA-Z]{2,6}$";

	public static final String REGEX_DDMMYYYY = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";

	public static final String lambdaMailNombre = "lambda-mail-service";

	public static final String lambdaMailSenderNombre = "lambda-mailSender-service";

	public static final String passwordCaso = "51st3ma$2021.";

	public static Map<String, String> mapRiesgo = new HashMap<String, String>() {
		private static final long serialVersionUID = 2806985167992013988L;
		{
			put("Bajo", "green");
			put("Medio", "yellow");
			put("Alto", "red");
		}
	};
}
