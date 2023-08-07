package com.appsistema.sistemaga;

public class Falta {
    private String idFalta;
    private String descripcionFalta;

    public Falta(String idFalta, String descripcionFalta) {
        this.idFalta = idFalta;
        this.descripcionFalta = descripcionFalta;
    }

    public String getIdFalta() {
        return idFalta;
    }

    public String getDescripcionFalta() {
        return descripcionFalta;
    }

    @Override
    public String toString() {
        return descripcionFalta;
    }
}