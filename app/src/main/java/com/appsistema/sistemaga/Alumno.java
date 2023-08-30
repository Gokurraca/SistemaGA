package com.appsistema.sistemaga;

class Alumno {
    private String nombreCompleto;
    private String rut;

    public Alumno(String nombreCompleto, String rut) {
        this.nombreCompleto = nombreCompleto;
        this.rut = rut;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getRut() {
        return rut;
    }

    @Override
    public String toString() {
        return nombreCompleto; // Esto es lo que se mostrará en el Spinner
    }
}