package com.appsistema.sistemaga;

import static com.appsistema.sistemaga.MainActivity.Usuario;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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

public class mis_alumnos extends AppCompatActivity{  //implements AlumnosAdapter.OnAlumnoClickListener{

    private int selectedItemId;
    ProgressDialog progressDialog;
    private DrawerLayout drawerLayout;
    private ListView list_alumnos;

    private List<String> items;
    static String Nombre = "";

    public static String nombre_alumno = "";
    public static String rut_alumno = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_alumnos);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Alumnos...");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        // Obtén el drawable del ProgressDialog y establece s u color
        Drawable drawable = new ProgressBar(this).getIndeterminateDrawable().mutate();
        drawable.setColorFilter(getResources().getColor(R.color.botones), PorterDuff.Mode.SRC_IN);
        progressDialog.setIndeterminateDrawable(drawable);
        list_alumnos  =findViewById(R.id.list_alumnos);
        drawerLayout = findViewById(R.id.drawer_layout);

        //carga los alumnos
        GetFaltasxAlumno tareaCargar = new GetFaltasxAlumno();
        tareaCargar.execute(host + "/cargaAlumnosxFuncionario/");
    }


    private class GetFaltasxAlumno extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {

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

                    List<AlumnoListado> listaAlumnos = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        try {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            Nombre = jsonObject1.getString("NombreApellido");
                            String txt_rutAlumno = jsonObject1.getString("rut_est");
                            String faltaLeve = jsonObject1.getString("cantidad_cat1");
                            String faltaGrave = jsonObject1.getString("cantidad_cat2");
                            String faltaGravisima = jsonObject1.getString("cantidad_cat3");

                            AlumnoListado alumno = new AlumnoListado(Nombre, Integer.parseInt(faltaLeve), Integer.parseInt(faltaGrave), Integer.parseInt(faltaGravisima),txt_rutAlumno);
                            listaAlumnos.add(alumno);
                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            Toast.makeText(mis_alumnos.this, "i " + e, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }

                    ListView listView = findViewById(R.id.list_alumnos);

                    // Crear el adaptador para mostrar listado alumnos.
                    AlumnosAdapter adapter = new AlumnosAdapter(mis_alumnos.this, listaAlumnos);

                    // Establecer el adaptador en el ListView
                    listView.setAdapter(adapter);



                } else if (jsonObject.getString("status").trim().equals("ERROR")) {
                    String mensaje = jsonObject.getString("Mensaje");
                    Dialogo("Alerta",mensaje);
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(mis_alumnos.this, "i ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                Log.e("hj",""+e);
                Toast.makeText(mis_alumnos.this, "ERROR "+e, Toast.LENGTH_SHORT).show();
            }
        }
    }


    public JSONObject downloadUrlGetFaltasxAlumno(String myurl) throws IOException {

        try {
            InputStream is = null;
            int len = 500;

            try {
                URL url = new URL(myurl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("funcionario", Usuario);

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

    public void Dialogo(final String Titulo, String Mesange) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mis_alumnos.this);
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

    @Override
    public void onBackPressed (){
        Intent intent2 = new Intent(mis_alumnos.this, MainActivity.class);
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

        switch (selectedItemId) {
            case R.id.menu_alumnos: // Mis alumnos
                //Toast.makeText(getApplicationContext(), "Opción Mis alumnos seleccionada", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(mis_alumnos.this, mis_alumnos.class);
                startActivity(intent1);
                finish();
                break;

           case R.id.menu_salir: // Salir
               Intent intent2 = new Intent(mis_alumnos.this, MainActivity.class);
               startActivity(intent2);
               finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}