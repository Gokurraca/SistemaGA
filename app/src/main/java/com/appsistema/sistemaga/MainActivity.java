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
    // Declaracion e inicalizacion de variables
    static final Integer PHONESTATS = 0x1;
    public static String host = "http://10.10.10.112/APISistemaGA/public";
    public static JSONObject jsonObject = null;
    public static String Usuario = "";
    public static Integer Perfil = 0;

    ProgressDialog progressDialog;
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
        //Creacion de progressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Validando....");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        //SETEAR COLOR PROGRESSDIALOG
        Drawable drawable = new ProgressBar(this).getIndeterminateDrawable().mutate();
        drawable.setColorFilter(getResources().getColor(R.color.botones), PorterDuff.Mode.SRC_IN);
        progressDialog.setIndeterminateDrawable(drawable);

    }

    public void botones(View view) { //Funcion donde se indica que hará cada botón

        if (view.getId() == R.id.btn_login) {
            showLoginDialog();
        }
        if (view.getId() == R.id.btn_salir) {
            //cierra la aplicacion
            finishAffinity();
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

        //Acceso de prueba:
        //se valida que los campos no esten en blanco.
        if (!usuario.equals("") && !password.equals("")){

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

    private class ValidaLogin extends AsyncTask<String, Void, JSONObject> { // Tarea Asincrona
        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
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
                if (jsonObject.getString("status").trim().equals("OK")) {
                    //Valido el tipo de perfil para redireccionar.
                    Integer perfil = jsonObject.getInt("validacion");
                    try {
                        if (perfil.equals(1)) {
                            Perfil = perfil;
                            Usuario = usuario;
                            progressDialog.dismiss();
                            Intent intent2 = new Intent(MainActivity.this, mis_alumnos.class);
                            startActivity(intent2);
                            finish();
                        }else{
                            progressDialog.dismiss();
                            Intent intent2 = new Intent(MainActivity.this, mis_alumnos.class);
                            startActivity(intent2);
                            finish();
                        }

                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "i "+e, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();

                } else if (jsonObject.getString("status").trim().equals("ERROR")) {
                    userEditText.setText("");
                    passwordEditText.setText("");
                    userEditText.requestFocusFromTouch();
                    String Mensaje = jsonObject.getString("Mensaje");
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "" + Mensaje, Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Error de servidor", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                progressDialog.dismiss();
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

    public void Dialogo(final String Titulo, String Mesange) { // Funcion para alertas Dialogo flotante

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle(Titulo);
        alertDialogBuilder
                .setMessage(Mesange)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .create().show();
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

}