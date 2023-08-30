package com.appsistema.sistemaga;

import static com.appsistema.sistemaga.MainActivity.host;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class configuracion extends AppCompatActivity {
    EditText txt_host;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        txt_host = findViewById(R.id.txt_host);
        txt_host.setText(host);

    }

    //EVENTO PARA RETROCEDER AL LAYOUT ANTERIOR
    @Override
    public void onBackPressed (){
        Intent intent2 = new Intent(configuracion.this, MainActivity.class);
        startActivity(intent2);
        finish();
    }

    public void botones(View view){
        if(view.getId()== R.id.btn_guardar){
            if(txt_host.getText().toString().isEmpty()){
                Dialogo("Alerta","Debe llenar todos los campos");
            }else{
                try {
                    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                            "GestonConducta", null, 1);
                    AdminSQLiteOpenHelper admin1 = new AdminSQLiteOpenHelper(this,

                            "GestonConducta", null, 1);
                    SQLiteDatabase bd = admin.getWritableDatabase();
                    SQLiteDatabase bd1 = admin1.getWritableDatabase();

                    // los inserto en la base de datos

                    Cursor fila = bd1.rawQuery(
                            "SELECT name FROM sqlite_master WHERE TYPE='table' AND name='configuracion'", null);
                    if (fila.moveToFirst()) {
                        bd1.execSQL("UPDATE configuracion SET host='" + txt_host.getText().toString()+"'");
                        host=txt_host.getText().toString();
                    }else{
                        bd.execSQL("create table configuracion(host text)");
                        bd1.execSQL("INSERT INTO configuracion (host) VALUES ('"+ txt_host.getText().toString()+"')");
                    }
                    Dialogo("Mensaje","Configuración guardada");
                    bd.close();
                    bd1.close();
                    admin.close();
                    admin1.close();

                }catch (Exception e ){
                    Dialogo("Alerta","Error al guardar" +e);
                }
            }
        }
    }
    public void Dialogo(final String Titulo, String Mesange){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(configuracion.this);
        // Configura el titulo.
        alertDialogBuilder.setTitle(Titulo);
        // Configura el mensaje.
        alertDialogBuilder
                .setMessage(Mesange)
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        if(Titulo.equals("Mensaje")){
                            Intent intent2 = new Intent(configuracion.this, MainActivity.class);
                            startActivity(intent2);
                            finish();
                        }
                        //Si la respuesta es afirmativa aquí agrega tu función a realizar.
                    }
                })
                .create().show();
    }
}