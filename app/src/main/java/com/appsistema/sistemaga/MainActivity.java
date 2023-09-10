package com.appsistema.sistemaga;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MainActivity extends AppCompatActivity {

    static final Integer PHONESTATS = 0x1;
    public static String host = "http://host.cl/servicio/public";
    public static JSONObject jsonObject = null;
    public static String Usuario = "";
    public static Integer Perfil = 0;
    EditText passwordEditText;
    EditText userEditText;
    Button btn_salir;
    Button btn_login;
    String password="";
    String usuario= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //declara botones
        btn_login = findViewById(R.id.btn_login);
        btn_salir = findViewById(R.id.btn_salir);
        try {

            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                    "GestonConducta", null, 1);
            AdminSQLiteOpenHelper admin1 = new AdminSQLiteOpenHelper(this,
                    "GestonConducta", null, 1);
            SQLiteDatabase bd = admin.getWritableDatabase();
            SQLiteDatabase bd1 = admin1.getWritableDatabase();

            Cursor fila = bd1.rawQuery(
                    "SELECT name FROM sqlite_master WHERE TYPE='table' AND name='configuracion'", null);
            if (fila.moveToFirst()) {
                Cursor fila1 = bd1.rawQuery(
                        "SELECT * FROM configuracion", null);

                if (fila1.moveToFirst()) {
                    host = fila1.getString(0);
                }

            } else {
                //Creo tabla configuracion
                bd.execSQL("create table configuracion(host text)");
                //inserto en la base de datos
                bd1.execSQL("INSERT INTO configuracion (host) VALUES ('http://host.cl/servicio/public') ");
            }
            admin.close();
            admin1.close();
            bd.close();
            bd1.close();

            consultarPermiso(Manifest.permission.READ_PHONE_STATE, PHONESTATS);

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void botones(View view) { //Funcion donde se indica que hará cada botón

        if (view.getId() == R.id.btn_login) {
            showLoginDialog();
        }
        if (view.getId() == R.id.btn_salir) {
            //cierra la aplicacion
            //finish();
            System.exit(0);

        }
        if (view.getId() == R.id.btn_configurar) {
            Intent intent2 = new Intent(MainActivity.this, configuracion.class);
            startActivity(intent2);
            finish();
        }

        // Obtener la referencia a la vista del layout diferente
        View otherLayout = LayoutInflater.from(this).inflate(R.layout.dialog_login, null);

        // Obtener la referencia al botón en el layout diferente
        Button btnLoginFlotante = otherLayout.findViewById(R.id.btn_loginFlotante);

        if(view.getId() == btnLoginFlotante.getId()){
            ValidarAcceso();
        }
    }

    private void ValidarAcceso(){

        //seteo las variables para poder usarlas despues.
        usuario = userEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        //se valida que los campos no esten en blanco.
        if (!usuario.equals("") && !password.equals("")){
            // Creacion de ProgressDialog
            ProgressDialogHelper.showProgressDialog(this,"Validando Acceso...");
            //se concatena el host del servidor + el modulo del api (ws)
            MainActivity.ValidaLogin tarea = new MainActivity.ValidaLogin();
            tarea.execute(host + "/valida_login/");
        }else{
            Toast.makeText(this, "Complete los campos para continuar",Toast.LENGTH_LONG).show();
        }
    }
    private void showLoginDialog(){ //Construccion y preparacion de datos-.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_login, null);
        passwordEditText = dialogView.findViewById(R.id.passwordEditText);
        userEditText = dialogView.findViewById(R.id.userEditText);
        userEditText.requestFocusFromTouch();
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        //Limita cantidad de caracteres
        int maxLength = 9;

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(maxLength);
        userEditText.setFilters(filters);

        userEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > maxLength) {
                    userEditText.setText(s.subSequence(0, maxLength));
                    userEditText.setSelection(maxLength);
                }
            }
        });

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        // Cambiar el color del texto de los botones
        positiveButton.setTextColor(getResources().getColor(R.color.botones));
        negativeButton.setTextColor(getResources().getColor(R.color.botones_salir));
        // Redondear el diálogo
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }
    }

    private class ValidaLogin extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                //URL = urls[0];
                return downloadUrlUsuario(urls[0]);
            } catch (Exception e) {
                return null;
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONObject jsonObject = result;
              // Colocar el try de Sleep
                if (jsonObject.getString("status").trim().equals("OK")) {
                    //Valido el tipo de perfil para redireccionar.
                    Integer perfil = jsonObject.getInt("validacion");
                    try {
                        switch (perfil){
                            case 1:
                                Perfil = perfil;
                                Usuario = usuario;
                                ProgressDialogHelper.ocultarProgressDialog();
                                Intent intent = new Intent(MainActivity.this, mis_alumnos.class);
                                startActivity(intent);
                                finish();
                                break;
                            case 2:
                                Perfil = perfil;
                                Usuario = usuario;
                                ProgressDialogHelper.ocultarProgressDialog();
                                Intent intent2 = new Intent(MainActivity.this, mis_alumnosApoderado.class);
                                startActivity(intent2);
                                finish();
                                break;
                            case 3:
                                Perfil = perfil;
                                Usuario = usuario;
                                ProgressDialogHelper.ocultarProgressDialog();
                                Intent intent3 = new Intent(MainActivity.this, crud_superusuario.class);
                                startActivity(intent3);
                                finish();
                                break;
                        }

                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "i "+e, Toast.LENGTH_SHORT).show();
                    }
                    ProgressDialogHelper.ocultarProgressDialog();

                } else if (jsonObject.getString("status").trim().equals("ERROR")) {
                    userEditText.setText("");
                    passwordEditText.setText("");
                    userEditText.requestFocusFromTouch();
                    String Mensaje = jsonObject.getString("Mensaje");
                    ProgressDialogHelper.ocultarProgressDialog();
                    Toast.makeText(MainActivity.this, "" + Mensaje, Toast.LENGTH_SHORT).show();
                } else {
                    ProgressDialogHelper.ocultarProgressDialog();
                    Toast.makeText(MainActivity.this, "Error de servidor", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                ProgressDialogHelper.ocultarProgressDialog();
                Log.e("hj",""+e);
                Toast.makeText(MainActivity.this, "Error al conectar con el servidor  " + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public JSONObject downloadUrlUsuario(String myurl) throws IOException {

        try {
            InputStream is = null;
            int len = 500;

            try {
                URL url = new URL(myurl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("user", usuario);
                params.put("pass", password);

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0; )
                    sb.append((char) c);

                Log.e("json", sb.toString());
                return new JSONObject(sb.toString());

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } catch (Exception E) {
            return null;
        }
    }

    private void consultarPermiso(String permission, Integer requestCode) { //consulta los permisos concedidos por el usuario
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        }
    }

    public void onBackPressed() {finish();}


}