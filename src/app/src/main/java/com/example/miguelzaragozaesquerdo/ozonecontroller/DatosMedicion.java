package com.example.miguelzaragozaesquerdo.ozonecontroller;

/**
 * Clase que representa los datos de una medición de contaminantes.
 * Autor: Mario Merenciano
 */
public class DatosMedicion {

    private int idMedicion;
    private String instante;
    private String lugar;
    private double valor;
    private int idContaminante;

    /**
     * Constructor por defecto de la clase DatosMedicion.
     */
    public DatosMedicion(){}

    /**
     * Obtiene el valor de la medición.
     * @return El valor de la medición.
     */
    public double getValor() {
        return valor;
    }


    /**
     * Establece el valor de la medición.
     * @param valor El valor de la medición.
     */
    public void setValor(double valor) {
        this.valor = valor;
    }
}