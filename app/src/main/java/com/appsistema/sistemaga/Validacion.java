package com.appsistema.sistemaga;


import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class Validacion {

    // Método para validar si el texto contiene solo números
    public static boolean containsOnlyNumbers(String text) {
        return text.matches("\\d+");
    }

    // Método para validar si el texto contiene solo letras
    public static boolean containsOnlyLetters(String text) {
        return text.matches("[a-zA-Z]+");
    }
    }

