package com.example.criborm.cleanairly;

/**
 * Clase que representa los datos de una medición de contaminantes.
 * Autor: Mario Merenciano
 */
public class DatosMedicion {

    private int idMedicion;
    private String instante;
    private String lugar;
    private int valor;
    private int idContaminante;

    /**
     * Constructor por defecto de la clase com.example.criborm.cleanairly.DatosMedicion.
     */
    public DatosMedicion(){}

    /**
     * Obtiene el valor de la medición.
     * @return El valor de la medición.
     */
    public int getValor() {
        return valor;
    }

    /**
     * Establece el valor de la medición.
     * @param valor El valor de la medición.
     */
    public void setValor(int valor) {
        this.valor = valor;
    }

    public int getIdMedicion() {
        return idMedicion;
    }

    public void setIdMedicion(int idMedicion) {
        this.idMedicion = idMedicion;
    }

    public String getInstante() {
        return instante;
    }

    public void setInstante(String instante) {
        this.instante = instante;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public int getIdContaminante() {
        return idContaminante;
    }

    public void setIdContaminante(int idContaminante) {
        this.idContaminante = idContaminante;
    }
}