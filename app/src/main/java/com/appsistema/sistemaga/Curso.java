package com.appsistema.sistemaga;

public class Curso {
    private String codigo;
    private String descripcion;

    public Curso(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        // Sobrescribe el método toString() para que Spinner use la descripción del curso como texto en la lista desplegable
        return descripcion;
    }

}