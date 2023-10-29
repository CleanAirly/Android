package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

public class Servicios {
    private String url = "http://192.168.1.47:3001/api/sensor";
    public Servicios(){
        // Constructor por defecto
    }

    public void login(String username, String password){

        Log.d("TEST - USUARIO", username);
        Log.d("TEST - CONTRASEÑA", password);

        try{
            Log.d("TEST - CRASH", "CRASH 1");
            // Crear la conexión con el servidor
            URL server = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) server.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            Log.d("TEST - CRASH", "CRASH 2");
            // Crear el cuerpo del POST para realizar el login
            //String postData = "{\"email\": \"" + username + "\", \"password\": " + password + "}";
            String postData = "{\"email\": \"asd@test.com\", \"password\": 1234}";

            Log.d("TEST - CRASH", "CRASH 3");
            // Escribe los datos en el cuerpo de la solicitud
            try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
                out.writeBytes(postData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("TEST - CRASH", "CRASH 4");
            // Recoger el codigo de respuesta del servidor
            int codigoDeRespuesta = connection.getResponseCode();
            Log.d("TEST - CODIGO DE RESPUESTA", String.valueOf(codigoDeRespuesta));

            Log.d("TEST - CRASH", "CRASH 5");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                // Muestra la respuesta del servidor
                //System.out.println("Respuesta del servidor: " + response.toString());
                Log.d("TEST - RESPUESTA DEL SERVIDOR", String.valueOf(response));
            }
        }
        catch (IOException e) {
            Log.d("TEST - CRASH", "CRASH");
            throw new RuntimeException(e);
        }
    }
}