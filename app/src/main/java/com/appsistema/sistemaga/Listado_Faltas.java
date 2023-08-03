package com.appsistema.sistemaga;

import static com.appsistema.sistemaga.MainActivity.Perfil;
import static com.appsistema.sistemaga.MainActivity.host;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

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
import java.util.List;
import java.util.Map;

public class Listado_Faltas extends AppCompatActivity {

    ProgressDialog progressDialog;
    GridView gridView;
    private DrawerLayout drawerLayout;
    String rut_alumno;
    String rut_funcionario;
    String nombre_alumno;
    String falta_Leve;
    String falta_Grave;
    String falta_Gravisima;
    TextView txt_FaltaLeve;
    TextView txt_FaltaGrave;
    TextView txt_FaltaGravisima;
    TextView txt_nombreAlumno;

    //Para AlertDialog
    String fecha ="";
    String hora = "";
    String des_falta = "";
    String categoria = "";
    String nombre_completo = "";
    String especialidad = "";
    String comentario = "";
    String idfalta="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_faltas);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando datos...");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        // Obtén el drawable del ProgressDialog y establece s u color
        Drawable drawable = new ProgressBar(this).getIndeterminateDrawable().mutate();
        drawable.setColorFilter(getResources().getColor(R.color.botones), PorterDuff.Mode.SRC_IN);
        progressDialog.setIndeterminateDrawable(drawable);

        txt_FaltaLeve  =findViewById(R.id.txt_FaltaLeve);
        txt_FaltaGrave  =findViewById(R.id.txt_FaltaGrave);
        txt_FaltaGravisima  =findViewById(R.id.txt_FaltaGravisima);
        txt_nombreAlumno  =findViewById(R.id.txt_nombreAlumno);
        gridView = findViewById(R.id.gridView);
        drawerLayout = findViewById(R.id.drawer_layout);

        //Recuperar datos de la vista anterior
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String Nombre = bundle.getString("nombreAlumno");
        String rutAlumno = bundle.getString("rutAlumno");
        String faltaLeve = bundle.getString("faltaLeve");
        String faltaGrave = bundle.getString("faltaGrave");
        String faltaGravisima = bundle.getString("faltaGravisima");
        String rutFuncionario = bundle.getString("rutFuncionario");
        //setea parametros
        nombre_alumno = Nombre;
        rut_alumno = rutAlumno;
        rut_funcionario = rutFuncionario;
        falta_Leve = faltaLeve;
        falta_Grave = faltaGrave;
        falta_Gravisima = faltaGravisima;

        txt_FaltaLeve.setText(falta_Leve);
        txt_FaltaGrave.setText(falta_Grave);
        txt_FaltaGravisima.setText(falta_Gravisima);
        txt_nombreAlumno  .setText(nombre_alumno);
        TraeDatos();
    }

    private void TraeDatos(){

        //carga los alumnos
        GetFaltasxAlumno tareaCargar = new GetFaltasxAlumno();
        tareaCargar.execute(host + "/cargafaltasAlumno/");

    }

    private class GetFaltasxAlumno extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrlGetFaltasxAlumno(urls[0]);
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

                    JSONArray jsonArray = jsonObject.getJSONArray("faltas_alumnos");

                    List<GridItem> items = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        try {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                           // Nombre = jsonObject1.getString("NombreApellido");
                            String fecha_det = jsonObject1.getString("fecha_det");
                            String comentario_detfala = jsonObject1.getString("comentario_detfala");
                            String id_categoria = jsonObject1.getString("cat_falta");
                            String hora_det = jsonObject1.getString("hora_det");
                            String id_falta = jsonObject1.getString("id_falta");

                            String categoriaFalta="";
                            categoriaFalta = id_categoria;
                            switch (categoriaFalta){
                                case "1":
                                    categoriaFalta = "Leve";
                                    break;
                                case "2":
                                    categoriaFalta = "Grave";
                                    break;
                                case "3":
                                    categoriaFalta = "Gravisima";
                                    break;
                            }

                            items.add(new GridItem(fecha_det, comentario_detfala, categoriaFalta, hora_det, id_falta));

                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            Toast.makeText(Listado_Faltas.this, "i " + e, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }

                    GridAdapter adapter = new GridAdapter(Listado_Faltas.this, items);
                    gridView.setAdapter(adapter);

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            GridItem item = items.get(position);
                            String horaDetFalta = item.getHora();
                            String fechaDetFalta = item.getFecha();
                            String id_falta = item.getIdfalta();
                            fecha = fechaDetFalta;
                            hora = horaDetFalta;
                            idfalta = id_falta;
                            //cargo datos para pasar al AlertDialog
                            CargaDetalle tareaCargar = new CargaDetalle();
                            tareaCargar.execute(host + "/cargadetalle/");

                        }
                    });

                } else if (jsonObject.getString("status").trim().equals("ERROR")) {
                    String mensaje = jsonObject.getString("Mensaje");
                    Dialogo("Alerta",mensaje);
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Listado_Faltas.this, "i ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                Log.e("hj",""+e);
                Toast.makeText(Listado_Faltas.this, "ERROR "+e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Genero vista del AlertDialog
    private void mostrarAlertDialogConVista(String fecha,String hora,String des_falta,String categoria,String nombre_completo,String especialidad,String comentario) {
        // Obtener la vista del diseño personalizado del AlertDialog
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_detalle_alumno, null);

        // Buscar los elementos de la vista que desees configurar
        TextView txtFecha = dialogView.findViewById(R.id.txt_fecha);
        TextView txtHora = dialogView.findViewById(R.id.txt_hora);
        TextView txtFalta = dialogView.findViewById(R.id.txt_falta);
        TextView txtCategoria = dialogView.findViewById(R.id.txt_categoria);
        TextView txtRegistra = dialogView.findViewById(R.id.txt_registra);
        TextView txtCargo = dialogView.findViewById(R.id.txt_cargo);
        TextView txtComentarios = dialogView.findViewById(R.id.txt_comentarios);
        Button btnVolver = dialogView.findViewById(R.id.btn_volver);

        // Configurar los valores de los TextViews u otros elementos
        txtFecha.setText(fecha);
        txtHora.setText(hora);
        txtFalta.setText(des_falta);
        txtCategoria.setText(categoria);
        txtRegistra.setText(nombre_completo);
        txtCargo.setText(especialidad);
        txtComentarios.setText(comentario);

        // Construir el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounder_background);
        // Configurar el botón Volver
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra el AlertDialog
                alertDialog.dismiss();
            }
        });
        // Mostrar el AlertDialog con la vista personalizada
        alertDialog.show();
    }

    private class CargaDetalle extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrlCargaDetalle(urls[0]);
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

                    JSONArray jsonArray = jsonObject.getJSONArray("DatosDetFalta");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            fecha = jsonObject1.getString("fecha");
                            hora = jsonObject1.getString("hora");
                            des_falta = jsonObject1.getString("des_falta");
                            categoria = jsonObject1.getString("categoria");
                            nombre_completo = jsonObject1.getString("nombre_completo");
                            especialidad = jsonObject1.getString("especialidad");
                            comentario = jsonObject1.getString("comentario");

                        } catch (JSONException e) {
                            Toast.makeText(Listado_Faltas.this, "error " + e, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                    switch (categoria){
                        case "1":
                            categoria = "Leve";
                            break;
                        case "2":
                            categoria = "Grave";
                            break;
                        case "3":
                            categoria = "Gravisima";
                            break;
                    }

                    switch (especialidad){
                        case "1":
                            especialidad="Docente";
                            break;
                        case "2":
                            especialidad="Asistente";
                            break;
                    }
                    //Paso parametros a la vista del AlertDialog
                    mostrarAlertDialogConVista(fecha,hora,des_falta,categoria,nombre_completo,especialidad,comentario);

                } else if (jsonObject.getString("status").trim().equals("ERROR")) {
                    String mensaje = jsonObject.getString("Mensaje");
                    Toast.makeText(Listado_Faltas.this, mensaje, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Listado_Faltas.this, "i ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                Log.e("hj", "" + e);
                Toast.makeText(Listado_Faltas.this, "ERROR " + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public JSONObject downloadUrlCargaDetalle(String myurl) throws IOException {

        try {
            InputStream is = null;
            int len = 500;

            try {
                URL url = new URL(myurl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("rutAlumno", rut_alumno);
                params.put("fecha", fecha);
                params.put("hora", hora);
                params.put("idfalta",idfalta);

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

    public JSONObject downloadUrlGetFaltasxAlumno(String myurl) throws IOException {

        try {
            InputStream is = null;
            int len = 500;

            try {
                URL url = new URL(myurl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("rut_alumno", rut_alumno);

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

    @Override
    public void onBackPressed() {
        if(Perfil.equals(1)){
            Intent intent2 = new Intent(Listado_Faltas.this, mis_alumnos.class);
            startActivity(intent2);
            finish();
        }

    }
    public void Dialogo(final String Titulo, String Mesange) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Listado_Faltas.this);
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