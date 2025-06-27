package com.miempresa.conversor.service;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;


import com.miempresa.conversor.util.HttpHelper;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConversorService {

    // 🔐 Reemplazá con tu clave real de la API
    private static final String API_KEY = cargarApiKey();

    private static String cargarApiKey() {
        try {
            Properties props = new Properties();
            InputStream input = new FileInputStream("config.properties");
            props.load(input);
            return props.getProperty("api_key");
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar la API key: " + e.getMessage());
        }
    }


    /**
     * Convierte un monto desde una moneda a otra usando la API.
     */
    public double convertir(String desde, String hacia, double monto) throws Exception {
        String url = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/" + desde;

        String response = HttpHelper.get(url);

        JSONObject json = new JSONObject(response);

        if (!json.getString("result").equals("success")) {
            throw new RuntimeException("Error en la API: " + json.getString("error-type"));
        }

        JSONObject rates = json.getJSONObject("conversion_rates");
        double tasa = rates.getDouble(hacia);

        return monto * tasa;
    }

    /**
     * Obtiene todas las tasas de cambio desde una moneda base.
     */
    public Map<String, Double> obtenerTasas(String base) throws Exception {
        String url = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/" + base;

        String response = HttpHelper.get(url);

        JSONObject json = new JSONObject(response);

        if (!json.getString("result").equals("success")) {
            throw new RuntimeException("Error en la API: " + json.getString("error-type"));
        }

        JSONObject rates = json.getJSONObject("conversion_rates");

        Map<String, Double> mapa = new HashMap<>();
        for (String key : rates.keySet()) {
            mapa.put(key, rates.getDouble(key));
        }

        return mapa;
    }
}
