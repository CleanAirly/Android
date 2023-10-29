package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

public class Servicios {
    private String URL;
    public Servicios(){
        // Constructor por defecto
    }

    public Servicios(String url){
        this.URL = url;
    }


    public void login(String username, String password){
        List<String> datos = new ArrayList<>();

        datos.add(username);
        datos.add(password);

        Log.d("TEST", datos.get(0));
        Log.d("TEST", datos.get(1));
    }

}
