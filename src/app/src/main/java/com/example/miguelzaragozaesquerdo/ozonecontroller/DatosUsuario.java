package com.example.miguelzaragozaesquerdo.ozonecontroller;

public class DatosUsuario {
    private String Email;
    private String Nombre;
    private String Telefono;
    private String Sensor;

    public DatosUsuario(){}

    public DatosUsuario(String email, String nombre, String telefono, String sensor){
        this.Email = email;
        this.Nombre = nombre;
        this.Telefono = telefono;
        this.Sensor = sensor;
    }
    public String getEmail(){
        return this.Email;
    }
    public String getNombre(){
        return this.Nombre;
    }
    public String getTelefono(){
        return this.Telefono;
    }
    public String getSensor(){
        return this.Sensor;
    }

    public void setEmail(String email){
        this.Email = email;
    }
    public void setNombre(String nombre){
        this.Nombre = nombre;
    }
    public void setTelefono(String telefono){
        this.Telefono = telefono;
    }
    public void setSensor(String sensor){
        this.Sensor = sensor;
    }
    public void limpiarDatos(){
        this.Email = "";
        this.Nombre = "";
        this.Telefono = "";
        this.Sensor = "";
    }
}
