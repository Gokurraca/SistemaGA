package com.appsistema.sistemaga;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static final Integer PHONESTATS = 0x1;
    public static String Token = "";
    public static String Exp_Token = "";
    public static String host = "https://AWS.miapp.cl/public/valida_login_docente/";
    private static String Password = "";

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

        btn_login = findViewById(R.id.btn_login);
        btn_salir = findViewById(R.id.btn_salir);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Validando....");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        //SETEAR COLOR PROGRESSDIALOG
        Drawable drawable = new ProgressBar(this).getIndeterminateDrawable().mutate();
        drawable.setColorFilter(getResources().getColor(R.color.botones), PorterDuff.Mode.SRC_IN);
        progressDialog.setIndeterminateDrawable(drawable);

        try{
            consultarPermiso(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
        }catch (Exception e){
            Toast.makeText(MainActivity.this,"Error " + e,Toast.LENGTH_LONG).show();
        }
    }
    public void botones(View view) {

        if (view.getId() == R.id.btn_login) {
            showLoginDialog();
        }
        if (view.getId() == R.id.btn_salir) {
            finishAffinity();
        }
        if(view.getId() == R.id.btn_loginFlotante){
            usuario = userEditText.getText().toString().trim();
            password = passwordEditText.getText().toString().trim();
            ValidarAcceso();

        }
    }
    private void ValidarAcceso(){
        MainActivity.ValidaLogin tarea = new MainActivity.ValidaLogin();
        tarea.execute(host + "/valida_login_docente/");
    }
    private void showLoginDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_login, null);
        passwordEditText = dialogView.findViewById(R.id.passwordEditText);
        userEditText = dialogView.findViewById(R.id.userEditText);
        userEditText.requestFocusFromTouch();
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

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
    public static JSONObject jsonObject = null;
    private class ValidaLogin extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
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
                    progressDialog.dismiss();
                    Intent intent2 = new Intent(MainActivity.this, modulo_docente.class);
                    startActivity(intent2);
                    finish();

                } else if (jsonObject.getString("status").trim().equals("ERROR")) {
                    progressDialog.dismiss();
                    //Dialogo("Alerta","ACCESO DENEGADO");
                    Toast.makeText(MainActivity.this, "Accedo denegado", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "i ", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                progressDialog.dismiss();
                Log.e("hj",""+e);
                Toast.makeText(MainActivity.this, "Error "+e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void Dialogo(final String Titulo, String Mesange) {

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

    public JSONObject downloadUrl(String myurl) throws IOException {

        try {
            InputStream is = null;
            int len = 500;

            try {
                URL url = new URL(myurl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("user", userEditText.getText().toString());
                params.put("pass", passwordEditText.getText().toString());

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

    private void consultarPermiso(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        }
    }
}