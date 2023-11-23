package com.example.miguelzaragozaesquerdo.ozonecontroller;
import java.io.Serializable;

/**
 * Clase que representa los datos de un usuario.
 * Autor: Mario Merenciano
 */
public class DatosUsuario implements Serializable {
    private String Email;
    private String Nombre;
    private String IdSonda;
    private String Telefono;
    private String Password;

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
     * Obtiene la id sonda del usuario.
     * @return La id sonda del usuario.
     */
    public String getIdSonda(){
        return this.IdSonda;
    }

    /**
     * Obtiene el telefono del usuario.
     * @return El telefono del usuario.
     */
    public String getTelefono(){
        return this.Telefono;
    }

    /**
     * Obtiene el password del usuario.
     * @return El password del usuario.
     */
    public String getPassword(){
        return this.Password;
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
     * Establece el idSonda del usuario.
     * @param idSonda El password del usuario.
     */
    public void setIdSonda(String idSonda){
        this.IdSonda = idSonda;
    }

    /**
     * Establece el telefono del usuario.
     * @param telefono El telefono del usuario.
     */
    public void setTelefono(String telefono){
        this.Telefono = telefono;
    }

    /**
     * Establece el telefono del usuario.
     * @param password El telefono del usuario.
     */
    public void setPassword(String password){
        this.Password = password;
    }
}