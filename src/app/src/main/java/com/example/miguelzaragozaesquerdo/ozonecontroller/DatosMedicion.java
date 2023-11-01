package com.example.miguelzaragozaesquerdo.ozonecontroller;

public class DatosMedicion {

    private int idMedicion;
    private String instante;
    private String lugar;
    private double valor;
    private int idContaminante;

    public DatosMedicion(){}

    public double getValor() {
        return valor;
    }
    public void setValor(double valor) {
        this.valor = valor;
    }
}
