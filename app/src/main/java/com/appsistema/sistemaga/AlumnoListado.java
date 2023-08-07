package com.appsistema.sistemaga;

public class AlumnoListado {
    private String nombre;
    private int faltaLeve;
    private int faltaGrave;
    private int faltaGravisima;
    private String rut;

    public AlumnoListado(String nombre, int faltaLeve, int faltaGrave, int faltaGravisima, String rut) {
        this.nombre = nombre;
        this.faltaLeve = faltaLeve;
        this.faltaGrave = faltaGrave;
        this.faltaGravisima = faltaGravisima;
        this.rut = rut;
    }

    // Getters y setters (opcional, depende de tus necesidades)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getFaltaLeve() {
        return faltaLeve;
    }

    public void setFaltaLeve(int faltaLeve) {
        this.faltaLeve = faltaLeve;
    }

    public int getFaltaGrave() {
        return faltaGrave;
    }

    public void setFaltaGrave(int faltaGrave) {
        this.faltaGrave = faltaGrave;
    }

    public int getFaltaGravisima() {
        return faltaGravisima;
    }

    public void setFaltaGravisima(int faltaGravisima) {
        this.faltaGravisima = faltaGravisima;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

}
