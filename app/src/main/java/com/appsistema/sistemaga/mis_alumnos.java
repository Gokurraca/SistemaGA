package com.appsistema.sistemaga;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import static com.appsistema.sistemaga.MainActivity.host;
import static com.appsistema.sistemaga.MainActivity.Usuario;
import static com.appsistema.sistemaga.MainActivity.Perfil;

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
import java.util.Map;

public class mis_alumnos extends AppCompatActivity {

    private int selectedItemId;
    ProgressDialog progressDialog;
    private DrawerLayout drawerLayout;
    ListView list_recepcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_alumnos);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Alumnos...");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        // Obtén el drawable del ProgressDialog y establece su color
        Drawable drawable = new ProgressBar(this).getIndeterminateDrawable().mutate();
        drawable.setColorFilter(getResources().getColor(R.color.botones), PorterDuff.Mode.SRC_IN);
        progressDialog.setIndeterminateDrawable(drawable);

        drawerLayout = findViewById(R.id.drawer_layout);
        //carga los alumnos
        mis_alumnos.GetFaltasxAlumno tareaCargar = new mis_alumnos.GetFaltasxAlumno();
        tareaCargar.execute(host + "/cargaAlumnosxFuncionario/");


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

                    ArrayList<String> items = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONArray("faltas_alumnos");

                    for (int i = 0; i < jsonArray.length();i++){

                        try {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String Nombre = jsonObject1.getString("NombreApellido");
                            //REVISAR
                            Integer faltaLeve = jsonObject1.getInt("cantidad_faltas_cat1");
                            Integer faltaGrave = jsonObject1.getInt("cantidad_faltas_cat2");
                            Integer faltaGravisima = jsonObject1.getInt("cantidad_faltas_cat2");

                            items.add("Nombre: " + Nombre + "\r\n" +
                                    "faltaLeve: " + faltaLeve + "\r\n" + "faltaGrave: " + faltaGrave + "\r\n" + "faltaGravisima: " + faltaGravisima);

                            //PARA DEFINIR COLOR DE TEXTO DEL LISTADO SE LLAMA AL DEVICE_NAME
                            ArrayAdapter<String> adaptador = new ArrayAdapter<String>(mis_alumnos.this, R.layout.tarjeta_alumnos, items);
                            list_recepcion.setAdapter(adaptador);
                            progressDialog.dismiss();

                        }catch (JSONException e){
                            Toast.makeText(mis_alumnos.this, "i "+e, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                } else if (jsonObject.getString("status").trim().equals("error")) {
                    String mensaje = jsonObject.getString("mensaje");
                    Toast.makeText(mis_alumnos.this,mensaje,Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "Opción Mis alumnos seleccionada", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_faltas: // Registro de faltas
                Toast.makeText(getApplicationContext(), "Opción Registro de faltas seleccionada", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mis_alumnos.this, modulo_docente.class);
                startActivity(intent);
                finish();
                break;
            case R.id.menu_listadofaltas: // Listado de faltas
                Toast.makeText(getApplicationContext(), "Opción Listado de faltas seleccionada", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}