package com.appsistema.sistemaga;

import static com.appsistema.sistemaga.MainActivity.host;

import androidx.appcompat.app.AppCompatActivity;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import static com.appsistema.sistemaga.MainActivity.Usuario;

public class agregar_falta_masivo extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    TextView lbl_alumno;
    Spinner sp_Curso;
    Spinner sp_alumnos;
    Spinner sp_CategoriaFalta;
    Spinner sp_falta;
    String rut_alumno;
    String rut_funcionario;
    ProgressDialog progressDialog;
    TextView txt_descripcion;
    Integer opcion_CategoriaFalta = 0;
    String opcion_IDfalta = "";
    Button btn_registrarFalta;
    String opcion_idCurso="";
    Button btn_limpiar;

    private ArrayAdapter<String> adapter;

    private int selectedItemId;
    String selectedRutEst;

    //para reconocimiento de voz
    private ImageButton btnMic;
    private SpeechRecognizer speechRecognizer;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_falta_masivo);
        rut_funcionario = Usuario;
        sp_Curso = findViewById(R.id.sp_Curso);
        sp_alumnos = findViewById(R.id.sp_alumnos);
        sp_CategoriaFalta = findViewById(R.id.sp_CategoriaFalta);
        sp_falta = findViewById(R.id.sp_falta);
        txt_descripcion = findViewById(R.id.txt_descripcion);
        btn_registrarFalta = findViewById(R.id.btn_registrarFalta);
        sp_CategoriaFalta.setOnItemSelectedListener(this);
        sp_falta.setOnItemSelectedListener(this);
        btnMic = findViewById(R.id.btnMic);
        btn_limpiar = findViewById(R.id.btn_limpiar);


        // Inicializar el reconocedor de voz
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        // Configurar el listener para recibir los resultados del reconocimiento de voz
        speechRecognizer.setRecognitionListener(new MyRecognitionListener());

        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });

        //carga los alumnos
        agregar_falta_masivo.GetCurso tareaCargar = new agregar_falta_masivo.GetCurso();
        tareaCargar.execute(host + "/datoscurso/");

        //crea lista para spinner de categoria de faltas
        ArrayList<String> listaCategorias = new ArrayList<>();
        listaCategorias.add("Seleccione Categoria");
        listaCategorias.add("Leve");
        listaCategorias.add("Grave");
        listaCategorias.add("Gravísima");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaCategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_CategoriaFalta.setAdapter(adapter);

        btn_registrarFalta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Código a ejecutar cuando se hace clic en el botón
                registrarFalta();
            }
        });
        btn_limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Código a ejecutar cuando se hace clic en el botón
                txt_descripcion.setText("");
            }
        });
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
                if (!txt_descripcion.getText().toString().trim().equals("")){
                    txt_descripcion.append(spokenText + " ");}
                if(txt_descripcion.getText().toString().trim().equals("")){
                    spokenText= spokenText.substring(0,1).toUpperCase()+spokenText.substring(1).toLowerCase(); // Primera letra en mayuscula
                    txt_descripcion.append(spokenText + " ");}
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

    private void registrarFalta(){


        if(opcion_idCurso.equals("")) {
            Toast.makeText(agregar_falta_masivo.this, "Debe seleccionar un Curso", Toast.LENGTH_SHORT).show();
            return;
        }
            if(opcion_IDfalta.equals(null) || opcion_CategoriaFalta.equals(0)){
                Toast.makeText(agregar_falta_masivo.this, "Debe seleccionar una categoria", Toast.LENGTH_SHORT).show();
                return;
            }
                if (txt_descripcion.getText().toString().trim().equals("")){
                    Toast.makeText(agregar_falta_masivo.this, "Debe ingresar una descripcion par este Incidente", Toast.LENGTH_SHORT).show();
                     return;
                }
                if (containsOnlyNumbers(txt_descripcion.getText().toString().replaceAll("\\s",""))) {
                    Toast.makeText(agregar_falta_masivo.this, "La descripcion no puedo tener solo numeros"+txt_descripcion.getText(), Toast.LENGTH_SHORT).show();
                    return;
                }

                agregar_falta_masivo.GuardarRegistro tareaCargar = new agregar_falta_masivo.GuardarRegistro();
                ProgressDialogHelper.showProgressDialog(this,"Procesando...");
                tareaCargar.execute(host + "/guardarRegistro/");
    }

    // Método para validar si el texto contiene solo números
    public static boolean containsOnlyNumbers(String text) {
        return text.matches("\\d+");
    }

    // Método para validar si el texto contiene solo letras
    public static boolean containsOnlyLetters(String text) {
        return text.matches("[a-zA-Z]+");
    }



    private class GuardarRegistro extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                //URL = urls[0];
                return downloadUrlGuardarRegistro(urls[0]);
            } catch (Exception e) {
                return null;
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            if(result.equals("ERROR")){
                ProgressDialogHelper.ocultarProgressDialog();
            }else {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getString("status").trim().equals("OK")) {
                        ProgressDialogHelper.ocultarProgressDialog();
                        DialogoExito("Alerta","Registro guardado correctamente");

                    } else if (jsonObject.getString("status").trim().equals("ERROR")) {
                        String mensaje = jsonObject.getString("Mensaje");
                        Toast.makeText(agregar_falta_masivo.this, mensaje, Toast.LENGTH_SHORT).show();
                        ProgressDialogHelper.ocultarProgressDialog();
                        Toast.makeText(agregar_falta_masivo.this, "error ", Toast.LENGTH_SHORT).show();
                    } else {
                        ProgressDialogHelper.ocultarProgressDialog();
                        Toast.makeText(agregar_falta_masivo.this, "i ", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    ProgressDialogHelper.ocultarProgressDialog();
                    Log.e("hj", "" + e);
                    Toast.makeText(agregar_falta_masivo.this, "Error " + e, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public String downloadUrlGuardarRegistro(String myurl) throws IOException {
        int len = 0;

        InputStream is = null;
        try {

            len++;

            StringBuilder sb = new StringBuilder();

            URL url = new URL(myurl);
            len++;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            len++;
            conn.setRequestMethod("POST");
            len++;
            conn.setRequestProperty("Content-Type", "application/raw");

            Calendar calendar = Calendar.getInstance();

            // Obtener el año, mes y día
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Los meses empiezan en 0, se suma 1 para obtener el número correcto
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Obtener la hora, minutos y segundos
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            // Formatear la fecha y hora como cadenas de texto
            String fechaActual = String.format("%04d-%02d-%02d", year, month, day);
            String horaActual = String.format("%02d:%02d:%02d", hour, minute, second);

            JSONObject Json = new JSONObject();
            //GUARDO EL PICKING PARA PASARLO A LA API
            JSONArray Registro = new JSONArray();

            JSONObject Detalle = new JSONObject();
            Detalle.put("rut_est",selectedRutEst);
            Detalle.put("id_falta", opcion_IDfalta);
            Detalle.put("rut_fun", rut_funcionario);
            Detalle.put("comentario_detfala", txt_descripcion.getText().toString().trim());
            Detalle.put("fecha_det_falta", fechaActual);
            Detalle.put("hora_det_falta", horaActual);
            Detalle.put("cod_curso",String.valueOf(opcion_idCurso));
            Registro.put(Detalle);

            Json.put("Detalle", Registro);

            Log.e("json",Json.toString());

            len++;
            conn.setDoOutput(true);
            len++;
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            len++;
            out.write(Json.toString());
            len++;
            out.close();
            len++;
            int HttpResult =conn.getResponseCode();
            len++;

            if(HttpResult ==HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                System.out.println(""+sb.toString());
                return sb.toString();
            }else{
                Toast.makeText(agregar_falta_masivo.this, "" + "Error en el servidor", Toast.LENGTH_SHORT).show();
                System.out.println(conn.getResponseMessage());
                return conn.getResponseMessage();
            }

        }catch (Exception E){
            return "ERROR " + E;
        }
    }
    private class GetCurso extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrlGetCurso(urls[0]);
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

                    JSONArray jsonArray = jsonObject.getJSONArray("datosCurso");

                    ArrayList<Curso> listaCursos = new ArrayList<>();
                    listaCursos.add(new Curso("", "Seleccione Curso"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String cod_curso = jsonObject1.getString("cod_curso");
                            String desc_curso = jsonObject1.getString("desc_curso");
                            Curso curso = new Curso(cod_curso, desc_curso);
                            listaCursos.add(curso);
                        } catch (JSONException e) {
                            Toast.makeText(agregar_falta_masivo.this, "i " + e, Toast.LENGTH_SHORT).show();
                            ProgressDialogHelper.ocultarProgressDialog();
                        }
                    }
                    ArrayAdapter<Curso> adapterCursos = new ArrayAdapter<>(agregar_falta_masivo.this,
                            android.R.layout.simple_spinner_item, listaCursos);
                    // Define cómo se mostrarán los elementos en la lista desplegable
                    adapterCursos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Establece el adaptador en el Spinner
                    sp_Curso.setAdapter(adapterCursos);
                    sp_Curso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Curso cursoSeleccionado = (Curso) parent.getItemAtPosition(position);
                            String codigoCursoSeleccionado = cursoSeleccionado.getCodigo();
                            if (cursoSeleccionado.equals("Seleccione Curso")) {
                                opcion_idCurso = "0";
                            } else {
                                opcion_idCurso = codigoCursoSeleccionado;
                                //carga las alumno según curso
                                if(!opcion_idCurso.equals("")){
                                    agregar_falta_masivo.GetAlumnoxCurso tareaCargar1 = new agregar_falta_masivo.GetAlumnoxCurso();
                                    tareaCargar1.execute(host + "/alumnosxcurso/");
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                } else if (jsonObject.getString("status").trim().equals("error")) {
                    String mensaje = jsonObject.getString("mensaje");
                    Toast.makeText(agregar_falta_masivo.this, mensaje, Toast.LENGTH_SHORT).show();
                    ProgressDialogHelper.ocultarProgressDialog();
                } else {
                    ProgressDialogHelper.ocultarProgressDialog();
                    Toast.makeText(agregar_falta_masivo.this, "i ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                ProgressDialogHelper.ocultarProgressDialog();
                Log.e("hj", "" + e);
                Toast.makeText(agregar_falta_masivo.this, "ERROR " + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public JSONObject downloadUrlGetCurso(String myurl) throws IOException {

        try {
            InputStream is = null;
            int len = 500;

            try {
                URL url = new URL(myurl);
                Map<String, Object> params = new LinkedHashMap<>();

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
    //Boton volver
    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(agregar_falta_masivo.this, mis_alumnos.class);
        startActivity(intent2);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        selectedItemId = item.getItemId();
        //Seleccion de items
        switch (selectedItemId) {
            case R.id.menu_alumnos: // Mis alumnos
                //Toast.makeText(getApplicationContext(), "Opción Mis alumnos seleccionada", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(agregar_falta_masivo.this, mis_alumnos.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.menu_faltas: // Registro de faltas
                //Toast.makeText(getApplicationContext(), "Opción Registro de faltas seleccionada", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(agregar_falta_masivo.this, agregar_falta_masivo.class);
                startActivity(intent);
                finish();
                break;

            case R.id.menu_salir: // Listado de faltas
                Intent intent2 = new Intent(agregar_falta_masivo.this, MainActivity.class);
                startActivity(intent2);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Verifica qué Spinner se activó
        if (parent.getId() == R.id.sp_CategoriaFalta) {
            String opcionSeleccionada = parent.getItemAtPosition(position).toString();
            if (opcionSeleccionada.equals("Seleccione Categoria")) {
                opcion_CategoriaFalta = 0;
            } else {
                switch (opcionSeleccionada) {
                    case "Leve":
                        opcion_CategoriaFalta = 1;
                        //carga las falta según categoria
                        agregar_falta_masivo.GetFalta tareaCargar = new agregar_falta_masivo.GetFalta();
                        tareaCargar.execute(host + "/faltas/");
                        break;
                    case "Grave":
                        opcion_CategoriaFalta = 2;
                        //carga las falta según categoria
                        agregar_falta_masivo.GetFalta tareaCargar1 = new agregar_falta_masivo.GetFalta();
                        tareaCargar1.execute(host + "/faltas/");
                        break;
                    case "Gravísima":
                        opcion_CategoriaFalta = 3;
                        //carga las falta según categoria
                        agregar_falta_masivo.GetFalta tareaCargar2 = new agregar_falta_masivo.GetFalta();
                        tareaCargar2.execute(host + "/faltas/");
                        break;
                }
                return;
            }
        }
        if (parent.getId() == R.id.sp_falta) {
            // Obtener la falta seleccionada
            Falta faltaSeleccionada = (Falta) parent.getItemAtPosition(position);
            // Obtener el ID de la falta
            String idFalta = faltaSeleccionada.getIdFalta();
            opcion_IDfalta = idFalta;
            txt_descripcion.requestFocus();
            // Hacer algo con la opción seleccionada
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
                ArrayList<Falta> listaFalta = new ArrayList<>();

                if (jsonObject.getString("status").trim().equals("OK")) {

                    JSONArray jsonArray = jsonObject.getJSONArray("datoFalta");



                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String idFalta = jsonObject1.getString("id_falta");
                            String descripcionFalta = jsonObject1.getString("des_falta");

                            // Crea un objeto Falta y añádelo a la lista
                            Falta falta = new Falta(idFalta, descripcionFalta);
                            listaFalta.add(falta);
                        } catch (JSONException e) {
                            Toast.makeText(agregar_falta_masivo.this, "error " + e, Toast.LENGTH_SHORT).show();
                            ProgressDialogHelper.ocultarProgressDialog();
                        }
                    }

                    // Crea un adaptador personalizado utilizando la lista de objetos Falta
                    ArrayAdapter<Falta> adapterFalta = new ArrayAdapter<>(agregar_falta_masivo.this, android.R.layout.simple_spinner_item, listaFalta);
                    adapterFalta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Establece el adaptador en el Spinner
                    sp_falta.setAdapter(adapterFalta);


                } else if (jsonObject.getString("status").trim().equals("ERROR")) {
                    String mensaje = jsonObject.getString("Mensaje");
                    Toast.makeText(agregar_falta_masivo.this, mensaje, Toast.LENGTH_SHORT).show();
                    ProgressDialogHelper.ocultarProgressDialog();
                    listaFalta.clear();
                    ArrayAdapter<Falta> adapterFalta = new ArrayAdapter<>(agregar_falta_masivo.this, android.R.layout.simple_spinner_item, listaFalta);
                    adapterFalta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_falta.setAdapter(adapterFalta);
                } else {
                    ProgressDialogHelper.ocultarProgressDialog();
                    Toast.makeText(agregar_falta_masivo.this, "i ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                ProgressDialogHelper.ocultarProgressDialog();
                Log.e("hj", "" + e);
                Toast.makeText(agregar_falta_masivo.this, "ERROR " + e, Toast.LENGTH_SHORT).show();
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
    private class GetAlumnoxCurso extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrlGetAlumnoxCurso(urls[0]);
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

                    JSONArray jsonArray = jsonObject.getJSONArray("datoAlumnosxCurso");

                    ArrayList<Alumno> listaAlumno = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String nombre_completo = jsonObject1.getString("nombre_completo");
                            String rut = jsonObject1.getString("rut_est");

                            listaAlumno.add(new Alumno(nombre_completo,rut));

                        } catch (JSONException e) {
                            Toast.makeText(agregar_falta_masivo.this, "error " + e, Toast.LENGTH_SHORT).show();
                            ProgressDialogHelper.ocultarProgressDialog();
                        }
                    }

                    // Crea un adaptador personalizado utilizando la lista de objetos Falta
                    ArrayAdapter<Alumno> adapterAlumno = new ArrayAdapter<>(agregar_falta_masivo.this, android.R.layout.simple_spinner_item, listaAlumno);
                    adapterAlumno.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Establece el adaptador en el Spinner
                    sp_alumnos.setAdapter(adapterAlumno);

                    sp_alumnos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Alumno selectedAlumno = (Alumno) parent.getSelectedItem();
                            selectedRutEst = selectedAlumno.getRut();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selectedRutEst = "";
                        }
                    });

                } else if (jsonObject.getString("status").trim().equals("ERROR")) {
                    String mensaje = jsonObject.getString("Mensaje");
                    recreate();
                    sp_CategoriaFalta.setSelection(0);
                    txt_descripcion.setText("");
                    Toast.makeText(agregar_falta_masivo.this, mensaje, Toast.LENGTH_SHORT).show();
                    ProgressDialogHelper.ocultarProgressDialog();
                } else {
                    ProgressDialogHelper.ocultarProgressDialog();
                    Toast.makeText(agregar_falta_masivo.this, "i ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                ProgressDialogHelper.ocultarProgressDialog();
                Log.e("hj", "" + e);
                Toast.makeText(agregar_falta_masivo.this, "ERROR " + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public JSONObject downloadUrlGetAlumnoxCurso(String myurl) throws IOException {

        try {
            InputStream is = null;
            int len = 500;

            try {
                URL url = new URL(myurl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("cod_curso", String.valueOf(opcion_idCurso));

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


    public void DialogoExito(final String Titulo, String Mesange) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(agregar_falta_masivo.this);
        // Configura el titulo.
        alertDialogBuilder.setTitle(Titulo);
        // Configura el mensaje.
        alertDialogBuilder
                .setMessage(Mesange)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(agregar_falta_masivo.this, mis_alumnos.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .create().show();
    }

    public void Dialogo(final String Titulo, String Mesange) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(agregar_falta_masivo.this);
        // Configura el titulo.
        alertDialogBuilder.setTitle(Titulo);
        // Configura el mensaje.
        alertDialogBuilder
                .setMessage(Mesange)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .create().show();
    }
}