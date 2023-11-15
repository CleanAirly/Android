package com.example.criborm.cleanairly;

import java.io.Serializable;

/**
 * Clase que representa los datos de un usuario.
 * Autor: Mario Merenciano
 */
public class DatosUsuario implements Serializable {
    private String Email;
    private String Nombre;

    /**
     * Constructor por defecto de la clase com.example.criborm.cleanairly.DatosUsuario.
     */
    public DatosUsuario(){}

    /**
     * Obtiene el correo electrónico del usuario.
     * @return El correo electrónico del usuario.
     */
    public String getEmail(){
        return this.Email;
    }

    /**
     * Obtiene el nombre del usuario.
     * @return El nombre del usuario.
     */
    public String getNombre(){
        return this.Nombre;
    }

    /**
     * Establece el correo electrónico del usuario.
     * @param email El correo electrónico del usuario.
     */
    public void setEmail(String email){
        this.Email = email;
    }

    /**
     * Establece el nombre del usuario.
     * @param nombre El nombre del usuario.
     */
    public void setNombre(String nombre){
        this.Nombre = nombre;
    }
}