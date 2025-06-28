package com.miempresa.conversor.util;

import java.util.HashMap;
import java.util.Map;

public class MonedaInfo {

    private static final Map<String, String> descripciones = new HashMap<>();

    static {
        descripciones.put("USD", "Dólar estadounidense");
        descripciones.put("ARS", "Peso argentino");
        descripciones.put("EUR", "Euro");
        descripciones.put("BRL", "Real brasileño");
        descripciones.put("CLP", "Peso chileno");
        descripciones.put("GBP", "Libra esterlina");
        descripciones.put("JPY", "Yen japonés");
        descripciones.put("CNY", "Yuan chino");
        descripciones.put("MXN", "Peso mexicano");
        descripciones.put("CAD", "Dólar canadiense");
        descripciones.put("AUD", "Dólar australiano");
        // Agregá más si querés
    }

    public static String getDescripcion(String codigo) {
        return descripciones.getOrDefault(codigo, "Descripción no disponible");
    }
}
