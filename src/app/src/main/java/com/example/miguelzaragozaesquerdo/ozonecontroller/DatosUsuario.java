package com.example.miguelzaragozaesquerdo.ozonecontroller;
import java.io.Serializable;

/**
 * Clase que representa los datos de un usuario.
 * Autor: Mario Merenciano
 */
public class DatosUsuario implements Serializable {
    private String Email;
    private String Nombre;
    private String Password;
    private String Telefono;

    /**
     * Constructor por defecto de la clase DatosUsuario.
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
     * Obtiene la password del usuario.
     * @return La password del usuario.
     */
    public String getPassword(){
        return this.Password;
    }

    /**
     * Obtiene el telefono del usuario.
     * @return El telefono del usuario.
     */
    public String getTelefono(){
        return this.Telefono;
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

    /**
     * Establece el password del usuario.
     * @param password El password del usuario.
     */
    public void setPassword(String password){
        this.Password = password;
    }

    /**
     * Establece el telefono del usuario.
     * @param telefono El telefono del usuario.
     */
    public void setTelefono(String telefono){
        this.Telefono = telefono;
    }
}