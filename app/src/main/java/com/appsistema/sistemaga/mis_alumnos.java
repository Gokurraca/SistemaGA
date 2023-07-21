package com.appsistema.sistemaga;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.navigation.NavigationView;

public class mis_alumnos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_alumnos);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        Toolbar toolbar = findViewById(R.id.toolbar); // Asignar la referencia correcta a tu Toolbar

        // Establecer el listener para los eventos de selección de opciones del Navigation Drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Manejar el evento de selección de opción aquí
                int itemId = item.getItemId();

                switch (itemId) {
                    case R.id.menu_option1: //mis alumnos

                        break;
                    case R.id.menu_option2: //Registro de faltas
                        Intent intent2 = new Intent(mis_alumnos.this, modulo_docente.class);
                        startActivity(intent2);
                        finish();
                        break;
                    case R.id.menu_option3: //Listado de faltas
                        // Acción para la opción 3
                        break;
                }

                // Cerrar el Drawer después de seleccionar una opción
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        // Agregar el ícono para abrir el Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


    }
}