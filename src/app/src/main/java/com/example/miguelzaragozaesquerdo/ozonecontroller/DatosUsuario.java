package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import java.io.Serializable;

public class DatosUsuario implements Serializable {
    private transient PeticionarioREST elPeticionario = new PeticionarioREST();
    private String Email;
    private String Nombre;
    private String Telefono;

    public DatosUsuario(){}

    public String getEmail(){
        return this.Email;
    }
    public String getNombre(){
        return this.Nombre;
    }

    /*public String getTelefono(){
        return this.Telefono;
    }*/

    public void setEmail(String email){
        this.Email = email;
    }
    public void setNombre(String nombre){
        this.Nombre = nombre;
    }

    /*public void setTelefono(String telefono){
        this.Telefono = telefono;
    }*/

}
