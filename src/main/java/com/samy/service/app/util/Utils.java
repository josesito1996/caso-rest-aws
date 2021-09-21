package com.samy.service.app.util;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

    public static String uuidGenerado() {
        return UUID.randomUUID().toString();
    }

    public static <T> T convertFromString(String cadena, Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(cadena, clazz);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static String fechaFormateadaYYYMMDD(LocalDateTime fecha) {
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY
        // hh:mm:s");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
        return fecha.format(formatter);
    }

    public static String fechaFormateada(LocalDateTime fecha) {
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY
        // hh:mm:s");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        return fecha.format(formatter);
    }

    public static String fechaFormateada(LocalDate fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        return fecha.format(formatter);
    }

    public static String añoFecha(LocalDate fecha) {
        return String.valueOf(fecha.getYear());
    }

    public static String diaFecha(LocalDate fecha) {
        return String.valueOf(fecha.getDayOfMonth());
    }

    public static String mesFecha(LocalDate fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM");
        return formatMes(fecha.format(formatter));
    }

    private static String formatMes(String mes) {
        String primeraLetra = mes.substring(0, 1).toUpperCase();
        String resto = mes.substring(1, mes.length());
        return primeraLetra.concat(resto);
    }

    public static String getExtension(String fileName) {
        return fileName.substring(fileName.indexOf("."), fileName.length());
    }

    public static String base64Text(String base64) {
        return base64.isEmpty() || base64 == null ? "" : base64.split(",")[1];
    }

    public static double getPorcentaje(double cantidad, double total) {
        return round((float) cantidad / (float) total * 100, 2);
    }

    private static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String formatMoney(double money) {
        DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
        return df.format(money);
    }

    public static Boolean dniValido(String dni) {
        String regex = "[0-9]+";
        return dni == null ? false : dni.matches(regex) && dni.length() == 8;
    }

    public static Boolean telefonoValido(String telefono) {
        String regex = "^\\(?(\\d{2})\\)?[-]?(\\d{7})$";
        return telefono == null ? false : telefono.matches(regex);
    }

    public static Boolean noTieneEspaciosEnBlanco(String... items) {
        if (items == null)
            return false;
        if (items.length > 0) {
            for (String item : items) {
                System.out.println(item);
                if (item.trim().contains(" ")) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void registrarData() {
        String nombres = "JOSE";
        String apellidoPaterno = "CASTILLO";
        String apellidoMaterno = "CHALQUE";
        String direccion = "MZ C LOTE 1 - URB. CIUDAD DE LOS CONSTRUCTORES";
        String telefono = "01-2867385";
        String dni = "75624412";

        if (noTieneEspaciosEnBlanco(nombres, apellidoPaterno, apellidoMaterno)) {
            System.out.println("Cumple con la condicion");
        }

    }
    public static void main(String...args) {
        registrarData();
    }
}
