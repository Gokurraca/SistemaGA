package com.appsistema.sistemaga;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class modulo_docente extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo_docente);


    }
    @Override
    public void onBackPressed (){
        Intent intent2 = new Intent(modulo_docente.this, mis_alumnos.class);
        startActivity(intent2);
        finish();
    }
    public void showMenu(View view) {


    }

    private void RegistrarFalta(){

    }
}