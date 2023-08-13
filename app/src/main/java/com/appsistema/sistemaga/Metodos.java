package com.appsistema.sistemaga;

public class Metodos {
  static String categoria;
  static int cat;
    public static   String Convierte( String valor){
        switch (valor) {
            case "1":
                categoria = "Leve";
                break;
            case "2":
                categoria = "Grave";
                break;
            case "3":
                categoria = "Gravísima";
                break;

        }
        return categoria;
    }
    public static int Convierte1( String valor){
        switch (valor) {
            case "Leve":
                cat = 1;
                break;
            case "Grave":
                cat = 2;
                break;
            case "Gravísima":
                 cat = 3;
                 break;
        }
        return cat;
    }
}



