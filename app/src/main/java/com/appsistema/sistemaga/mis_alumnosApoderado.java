package com.appsistema.sistemaga;

import static com.appsistema.sistemaga.MainActivity.Usuario;
import static com.appsistema.sistemaga.MainActivity.host;

import androidx.appcompat.app.AppCompatActivity;
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

public class mis_alumnosApoderado extends AppCompatActivity {

    private int selectedItemId;

    private DrawerLayout drawerLayout;
    private ListView list_alumnos;

    static String Nombre = "";
    Integer faltaleve=0;
    Integer faltagrave=0;
    Integer faltagravisima=0;
    public static String nombre_alumno = "";
    public static String rut_alumno = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_alumnos_apoderado);
        list_alumnos  =findViewById(R.id.list_alumnos);
        drawerLayout = findViewById(R.id.drawer_layout);

        //carga los alumnos
        mis_alumnosApoderado.GetFaltasxAlumno tareaCargar = new mis_alumnosApoderado.GetFaltasxAlumno();
        ProgressDialogHelper.showProgressDialog(this,"Cargando mis Estudiantes");
        tareaCargar.execute(host + "/cargaAlumnosxApoderado/");
    }

    private void showOpcionesDialog(){ //Construccion y preparacion de datos-.
        //genero el alertdialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_ver, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

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
    public void botones(View view) { //Funcion donde se indica que hará cada botón


        // Obtener la referencia a la vista del layout diferente
        View otherLayout = LayoutInflater.from(this).inflate(R.layout.dialog_ver_cre, null);

        // Obtener la referencia al botón en el layout diferente
        Button btn_listado = otherLayout.findViewById(R.id.btn_listado);
        Button btn_registrar = otherLayout.findViewById(R.id.btn_registrar);

        if (view.getId() == btn_listado.getId()) {

            if (faltaleve > 0 || faltagrave > 0 || faltagravisima > 0) {
                Intent intent2 = new Intent(mis_alumnosApoderado.this, Listado_Faltas.class);
                intent2.putExtra("rutAlumno",rut_alumno);
                intent2.putExtra("nombreAlumno",nombre_alumno);
                intent2.putExtra("rutFuncionario",0);
                intent2.putExtra("faltaLeve",String.valueOf(faltaleve));
                intent2.putExtra("faltaGrave",String.valueOf(faltagrave));
                intent2.putExtra("faltaGravisima",String.valueOf(faltagravisima));
                startActivity(intent2);
                finish();
            } else {
                Dialogo("Alerta","No hay información de Incidentes");
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

                params.put("apoderado", Usuario);

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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mis_alumnosApoderado.this);
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
    //Evento Click de retroceso.
    @Override
    public void onBackPressed (){
        Intent intent2 = new Intent(mis_alumnosApoderado.this, MainActivity.class);
        startActivity(intent2);
        finish();
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
                            ProgressDialogHelper.ocultarProgressDialog();

                        } catch (JSONException e) {
                            Toast.makeText(mis_alumnosApoderado.this, "i " + e, Toast.LENGTH_SHORT).show();
                            ProgressDialogHelper.ocultarProgressDialog();
                        }
                    }

                    ListView listView = findViewById(R.id.list_alumnos);

                    // Crear el adaptador para mostrar listado alumnos.
                    AlumnosAdapter adapter = new AlumnosAdapter(mis_alumnosApoderado.this, listaAlumnos);

                    // Establecer el adaptador en el ListView
                    listView.setAdapter(adapter);

                    // Manejar el clic en un elemento del ListView
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            AlumnoListado alumno = listaAlumnos.get(position);
                            nombre_alumno = alumno.getNombre();
                            faltaleve = alumno.getFaltaLeve();
                            faltagrave = alumno.getFaltaGrave();
                            faltagravisima = alumno.getFaltaGravisima();
                            rut_alumno = alumno.getRut();
                            showOpcionesDialog();
                        }
                    });

                } else if (jsonObject.getString("status").trim().equals("ERROR")) {
                    String mensaje = jsonObject.getString("Mensaje");
                    Dialogo("Alerta",mensaje);
                    ProgressDialogHelper.ocultarProgressDialog();
                } else {
                    ProgressDialogHelper.ocultarProgressDialog();
                    Toast.makeText(mis_alumnosApoderado.this, "i ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                ProgressDialogHelper.ocultarProgressDialog();
                Log.e("hj",""+e);
                Toast.makeText(mis_alumnosApoderado.this, "ERROR "+e, Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_menu_apoderado, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        selectedItemId = item.getItemId();

        switch (selectedItemId) {
            case R.id.menu_alumnos: // Mis alumnos
                //Toast.makeText(getApplicationContext(), "Opción Mis alumnos seleccionada", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(mis_alumnosApoderado.this, mis_alumnosApoderado.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.menu_salir: // Listado de faltas
                Intent intent2 = new Intent(mis_alumnosApoderado.this, MainActivity.class);
                startActivity(intent2);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}