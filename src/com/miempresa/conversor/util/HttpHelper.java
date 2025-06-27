package com.miempresa.conversor.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {

    public static String get(String urlStr) throws Exception {
        // Creamos el objeto URL
        URL url = new URL(urlStr);

        // Abrimos la conexión HTTP (como si abrieras el navegador)
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Leemos la respuesta
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        // Cerramos todo
        reader.close();
        connection.disconnect();

        // Devolvemos la respuesta como texto (JSON)
        return response.toString();
    }
}
