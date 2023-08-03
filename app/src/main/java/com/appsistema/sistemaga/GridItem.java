package com.appsistema.sistemaga;

public class GridItem {
    private String fecha;
    private String falta;
    private String categoria;
    private String hora;
    private String id_falta;

    public GridItem(String fecha, String falta, String categoria,String hora,String id_falta) {
        this.fecha = fecha;
        this.falta = falta;
        this.categoria = categoria;
        this.hora = hora;
        this.id_falta = id_falta;
    }

    public String getFecha() {
        return fecha;
    }

    public String getFalta() {
        return falta;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getHora() {
        return hora;
    }

    public String getIdfalta() {
        return id_falta;
    }
}