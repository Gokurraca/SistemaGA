package com.appsistema.sistemaga;

import static com.appsistema.sistemaga.MainActivity.host;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class crud_superusuario extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    ProgressDialog progressDialog;
    Button btn_limpiar;
    Button btn_modificar;
    //ImageButton btn_modificar;
    Button btn_agregar;
    Button btn_eliminar;
    Button btn_guardar;
    Button btn_eliminarFalta;
    Integer opcion_CategoriaFalta = 0;
    Spinner sp_categoriaFalta;
    Spinner sp_falta;
    TextView txt_descripcion;
    TextView lbl_idFalta;
    String opcion_IDfalta="";
    Integer TipoAccion=0;

    private int selectedItemId;
    private ImageButton btnMic;
    private SpeechRecognizer speechRecognizer;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud_superusuario);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Alumnos...");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        // Obtén el drawable del ProgressDialog y establece su color
        Drawable drawable = new ProgressBar(this).getIndeterminateDrawable().mutate();
        drawable.setColorFilter(getResources().getColor(R.color.botones), PorterDuff.Mode.SRC_IN);
        progressDialog.setIndeterminateDrawable(drawable);

        btn_modificar = findViewById(R.id.btn_modificar);
        btn_agregar = findViewById(R.id.btn_agregar);
        btn_eliminar = findViewById(R.id.btn_eliminar);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_menu_superusuario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        selectedItemId = item.getItemId();

        switch (selectedItemId) {
            case R.id.menu_salir: // Listado de faltas
                Intent intent2 = new Intent(crud_superusuario.this, MainActivity.class);
                startActivity(intent2);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void botones(View view) { //Funcion donde se indica que hará cada botón
        if (view.getId() == btn_modificar.getId()) {
            ShowdialogModificaFalta();
        }
        if (view.getId() == btn_agregar.getId()) {
            ShowdialogAgregarFalta();
        }
        if (view.getId() == btn_eliminar.getId()) {
            ShowdialogEliminarFalta();
        }
    }

    @Override
    public void onBackPressed (){ // Funcion de Boton atras
        Intent intent2 = new Intent(crud_superusuario.this, MainActivity.class);
        startActivity(intent2);
        finish();
    }

    private void ShowdialogModificaFalta(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_modificarfalta, null);
        // Obtener la referencia al botón en el layout diferente
        btn_modificar = dialogView.findViewById(R.id.btn_modificar);
        btn_limpiar = dialogView.findViewById(R.id.btn_limpiar);
        sp_categoriaFalta = dialogView.findViewById(R.id.sp_categoriaFalta);
        sp_falta = dialogView.findViewById(R.id.sp_falta);
        btnMic = dialogView.findViewById(R.id.btnMic);
        txt_descripcion = dialogView.findViewById(R.id.txt_descripcion);
        lbl_idFalta = dialogView.findViewById(R.id.lbl_idFalta);
        builder.setView(dialogView);
        btn_modificar.setEnabled(false);
        // Inicializar el reconocedor de voz
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        // Configurar el listener para recibir los resultados del reconocimiento  de voz
        speechRecognizer.setRecognitionListener(new MyRecognitionListener());

        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });

        //crea lista para spinner de categoria de faltas
        ArrayList<String> listaCategorias = new ArrayList<>();
        listaCategorias.add("Seleccione una opción");
        listaCategorias.add("Leve");
        listaCategorias.add("Grave");
        listaCategorias.add("Gravísima");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaCategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_categoriaFalta.setAdapter(adapter);
        AlertDialog dialog = builder.create();
        TipoAccion = 1;
        sp_categoriaFalta.setOnItemSelectedListener(this);
        sp_falta.setOnItemSelectedListener(this);
        dialog.show();

        // Redondear el diálogo
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }

        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });
        btn_limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_descripcion.setText("");
            }
        });
        btn_modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoModificar("Modificar Falta","¿Esta seguro de modificar la falta?");
            }
        });

    }

    private void ShowdialogAgregarFalta(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_agregarfalta, null);
        // Obtener la referencia al botón en el layout diferente
        btn_guardar = dialogView.findViewById(R.id.btn_guardar);
        btn_limpiar = dialogView.findViewById(R.id.btn_limpiar);
        sp_categoriaFalta = dialogView.findViewById(R.id.sp_categoriaFalta);
        btnMic = dialogView.findViewById(R.id.btnMic);
        txt_descripcion = dialogView.findViewById(R.id.txt_descripcion);
        builder.setView(dialogView);
        btn_guardar.setEnabled(false);
        // Inicializar el reconocedor de voz
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        // Configurar el listener para recibir los resultados del reconocimiento  de voz
        speechRecognizer.setRecognitionListener(new MyRecognitionListener());

        //crea lista para spinner de categoria de faltas
        ArrayList<String> listaCategorias = new ArrayList<>();
        listaCategorias.add("Seleccione una opción");
        listaCategorias.add("Leve");
        listaCategorias.add("Grave");
        listaCategorias.add("Gravísima");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaCategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_categoriaFalta.setAdapter(adapter);
        AlertDialog dialog = builder.create();
        TipoAccion = 2;
        sp_categoriaFalta.setOnItemSelectedListener(this);
        dialog.show();

        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });
        btn_limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_descripcion.setText("");
            }
        });
        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_descripcion.getText().toString().trim().equals("")){
                    Toast.makeText(crud_superusuario.this, "Debe agregar una descripcion", Toast.LENGTH_SHORT).show();
                }else{
                    Agregar tareaCargar = new Agregar();
                    tareaCargar.execute(host + "/guardarfalta/");

                    }
            }
        });
    }

    private void ShowdialogEliminarFalta(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_eliminarfalta, null);
        // Obtener la referencia al botón en el layout diferente
        btn_eliminarFalta = dialogView.findViewById(R.id.btn_eliminarfalta);
        sp_categoriaFalta = dialogView.findViewById(R.id.sp_categoriaFalta);
        lbl_idFalta = dialogView.findViewById(R.id.lbl_idFalta);
        sp_falta = dialogView.findViewById(R.id.sp_falta);
        builder.setView(dialogView);
        btn_eliminarFalta.setEnabled(false);

        //crea lista para spinner de categoria de faltas
        ArrayList<String> listaCategorias = new ArrayList<>();
        listaCategorias.add("Seleccione una opción");
        listaCategorias.add("Leve");
        listaCategorias.add("Grave");
        listaCategorias.add("Gravísima");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaCategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_categoriaFalta.setAdapter(adapter);
        AlertDialog dialog = builder.create();
        TipoAccion = 3;
        sp_categoriaFalta.setOnItemSelectedListener(this);
        sp_falta.setOnItemSelectedListener(this);
        dialog.show();

        btn_eliminarFalta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoEliminar("Eliminar Falta","¿Está seguro de eliminar la falta?");
            }
        });
    }
    public void DialogoEliminar(final String Titulo, String Mesange) { //Dialogo flotante

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(crud_superusuario.this);
        alertDialogBuilder.setTitle(Titulo);
        alertDialogBuilder
                .setMessage(Mesange)
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Eliminar tareaCargar = new Eliminar();
                        tareaCargar.execute(host + "/eliminarfalta/");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Acción a realizar cuando se selecciona "No" o se cancela el diálogo
                        dialog.cancel(); // Cierra el diálogo sin realizar ninguna acción
                    }
                })
                .create().show();
    }
    public void DialogoModificar(final String Titulo, String Mesange) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(crud_superusuario.this);
        alertDialogBuilder.setTitle(Titulo);
        alertDialogBuilder
                .setMessage(Mesange)
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Acción a realizar cuando se selecciona "Sí"
                        Modificar tareaCargar = new Modificar();
                        tareaCargar.execute(host + "/modificarfalta/");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Acción a realizar cuando se selecciona "No" o se cancela el diálogo
                        dialog.cancel(); // Cierra el diálogo sin realizar ninguna acción
                    }
                })
                .create().show();
    }
    private class Modificar extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                //URL = urls[0];
                return downloadUrlModificar(urls[0]);
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
                    String mensaje = jsonObject.getString("RespuestaUpdate");
                    Toast.makeText(crud_superusuario.this, mensaje,Toast.LENGTH_LONG).show();
                    btn_modificar.setEnabled(false);
                    txt_descripcion.setText("");
                    sp_categoriaFalta.setSelection(0);
                } else if (jsonObject.getString("status").trim().equals("ERROR")) {
                    String Mensaje = jsonObject.getString("Mensaje");
                    progressDialog.dismiss();
                    Toast.makeText(crud_superusuario.this, "" + Mensaje, Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(crud_superusuario.this, "Error de servidor", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                Log.e("hj",""+e);
                Toast.makeText(crud_superusuario.this, "Error al conectar con el servidor  " + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public JSONObject downloadUrlModificar(String myurl) throws IOException {

        try {
            InputStream is = null;
            int len = 500;

            try {
                URL url = new URL(myurl);
                Map<String, Object> params = new LinkedHashMap<>();
                String descripcion = txt_descripcion.getText().toString().trim();
                params.put("idfalta", opcion_IDfalta);
                params.put("cat_falta", opcion_CategoriaFalta);
                params.put("des_falta", descripcion);

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

    private class Agregar extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                //URL = urls[0];
                return downloadUrlAgregar(urls[0]);
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

            JSONObject jsonObject = result;
            try {
                 if (jsonObject.getString("status").trim().equals("OK")) {
                    //Valido el tipo de perfil para redireccionar.
                    String mensaje = jsonObject.getString("Mensaje");
                    Toast.makeText(crud_superusuario.this, mensaje,Toast.LENGTH_LONG).show();
                    btn_guardar.setEnabled(false);
                    txt_descripcion.setText("");
                    sp_categoriaFalta.setSelection(0);
                } else if (jsonObject.getString("status").trim().equals("ERROR")) {
                    String Mensaje = jsonObject.getString("Mensaje");
                    progressDialog.dismiss();
                    Toast.makeText(crud_superusuario.this, "" + Mensaje, Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(crud_superusuario.this, "Error de servidor", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                Log.e("hj",""+e);
                Toast.makeText(crud_superusuario.this, "Error al conectar con el servidor  " + e, Toast.LENGTH_SHORT).show();
            }

        }
    }

    public JSONObject downloadUrlAgregar(String myurl) throws IOException {

        try {
            InputStream is = null;
            int len = 500;

            try {
                URL url = new URL(myurl);
                Map<String, Object> params = new LinkedHashMap<>();
                String descripcion = txt_descripcion.getText().toString().trim();
                params.put("cat_falta", opcion_CategoriaFalta);
                params.put("des_falta", descripcion);

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
    private class Eliminar extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                //URL = urls[0];
                return downloadUrlEliminar(urls[0]);
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
                    String mensaje = jsonObject.getString("Mensaje");
                    Toast.makeText(crud_superusuario.this, mensaje,Toast.LENGTH_LONG).show();
                    btn_eliminarFalta.setEnabled(false);
                } else if (jsonObject.getString("status").trim().equals("ERROR")) {
                    String Mensaje = jsonObject.getString("Mensaje");
                    Toast.makeText(crud_superusuario.this, "" + Mensaje, Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(crud_superusuario.this, "Error de servidor", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                Log.e("hj",""+e);
                Toast.makeText(crud_superusuario.this, "Error al conectar con el servidor  " + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public JSONObject downloadUrlEliminar(String myurl) throws IOException {

        try {
            InputStream is = null;
            int len = 500;

            try {
                URL url = new URL(myurl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("idfalta", opcion_IDfalta);

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
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Verifica qué Spinner se activó
        if (parent.getId() == R.id.sp_categoriaFalta) {
            String opcionSeleccionada = parent.getItemAtPosition(position).toString();
            if (opcionSeleccionada.equals("Seleccione una opción")) {
                opcion_CategoriaFalta = 0;
                if(TipoAccion.equals(1)){
                    lbl_idFalta.setText("");
                    txt_descripcion.setText("");
                    sp_categoriaFalta.setSelection(0);
                    btn_modificar.setEnabled(false);
                }
                    if(TipoAccion.equals(2) ){
                        btn_guardar.setEnabled(false);
                    }
                    if(TipoAccion.equals(3)){
                        btn_eliminarFalta.setEnabled(false);
                    }
            } else {
                switch (opcionSeleccionada) {
                    case "Leve":
                        opcion_CategoriaFalta = 1;
                        break;
                    case "Grave":
                        opcion_CategoriaFalta = 2;
                        break;
                    case "Gravísima":
                        opcion_CategoriaFalta = 3;
                        break;
                }
                switch (TipoAccion){
                    case 1:
                        GetFalta tareaCargar = new GetFalta();
                        tareaCargar.execute(host + "/faltas/");
                        txt_descripcion.requestFocus();
                        btn_modificar.setEnabled(true);
                        break;
                    case 2:
                        txt_descripcion.requestFocus();
                        btn_guardar.setEnabled(true);
                        break;
                    case 3:
                        GetFalta tareaCargar1 = new GetFalta();
                        tareaCargar1.execute(host + "/faltas/");
                }
                return;
            }
        }

        if (parent.getId() == R.id.sp_falta) {
            if(TipoAccion.equals(1)){
                // Obtener la falta seleccionada
                Falta faltaSeleccionada = (Falta) parent.getItemAtPosition(position);
                // Obtener el ID de la falta
                String idFalta = faltaSeleccionada.getIdFalta();
                String falta = faltaSeleccionada.getDescripcionFalta();
                opcion_IDfalta = idFalta;
                lbl_idFalta.setText(opcion_IDfalta);
                txt_descripcion.setText(falta);
                txt_descripcion.requestFocus();
            }
            if(TipoAccion.equals(3)){
                // Obtener la falta seleccionada
                Falta faltaSeleccionada = (Falta) parent.getItemAtPosition(position);
                // Obtener el ID de la falta
                String idFalta = faltaSeleccionada.getIdFalta();
                opcion_IDfalta = idFalta;
                lbl_idFalta.setText(opcion_IDfalta);
                btn_eliminarFalta.setEnabled(true);
            }

        }
    }

    // Método para iniciar el reconocimiento de voz
    private void startSpeechToText() {
        // Crear un Intent para el reconocimiento de voz
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        // Iniciar la actividad del reconocimiento de voz
        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            // Obtener los resultados del reconocimiento de voz en forma de lista
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (result != null && result.size() > 0) {
                // Obtener el texto hablado y mostrarlo en el EditText
                String spokenText = result.get(0);
                txt_descripcion.append(spokenText + " ");
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private class GetFalta extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrlGetFalta(urls[0]);
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

                    JSONArray jsonArray = jsonObject.getJSONArray("datoFalta");

                    ArrayList<Falta> listaFalta = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String idFalta = jsonObject1.getString("id_falta");
                            String des_falta = jsonObject1.getString("des_falta");

                            // Crea un objeto Falta y añádelo a la lista
                            Falta falta = new Falta(idFalta, des_falta);
                            listaFalta.add(falta);
                        } catch (JSONException e) {
                            Toast.makeText(crud_superusuario.this, "error " + e, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }

                    // Crea un adaptador personalizado utilizando la lista de objetos Falta
                    ArrayAdapter<Falta> adapterFalta = new ArrayAdapter<>(crud_superusuario.this, android.R.layout.simple_spinner_item, listaFalta);
                    adapterFalta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Establece el adaptador en el Spinner
                    sp_falta.setAdapter(adapterFalta);

                } else if (jsonObject.getString("status").trim().equals("ERROR")) {
                    String mensaje = jsonObject.getString("Mensaje");
                    Toast.makeText(crud_superusuario.this, mensaje, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(crud_superusuario.this, "i ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                Log.e("hj", "" + e);
                Toast.makeText(crud_superusuario.this, "ERROR " + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public JSONObject downloadUrlGetFalta(String myurl) throws IOException {

        try {
            InputStream is = null;
            int len = 500;

            try {
                URL url = new URL(myurl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("categoriaFalta", opcion_CategoriaFalta);

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

            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } catch (Exception E) {
            return null;
        }
    }

}