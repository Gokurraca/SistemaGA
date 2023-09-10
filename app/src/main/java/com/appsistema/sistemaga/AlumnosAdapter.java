package com.appsistema.sistemaga;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;


public class AlumnosAdapter extends ArrayAdapter<AlumnoListado> {
    private Context context;
    private List<AlumnoListado> items;
    private OnAlumnoClickListener onAlumnoClickListener;

    public AlumnosAdapter(Context context, List<AlumnoListado> items) {
        super(context, R.layout.tarjeta_alumnos, items);
        this.context = context;
        this.items = items;
    }

    public void setOnAlumnoClickListener(OnAlumnoClickListener listener) {
        this.onAlumnoClickListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tarjeta_alumnos, parent, false);

        try {
            TextView lblFaltaLeve = view.findViewById(R.id.lbl_FaltaLeve);
            TextView lblFaltaGrave = view.findViewById(R.id.lbl_FaltaGrave);
            TextView lblFaltaGravisima = view.findViewById(R.id.lbl_faltaGravisima);
            TextView txtFaltaLeve = view.findViewById(R.id.txt_FaltaLeve);
            TextView txtFaltaGrave = view.findViewById(R.id.txt_FaltaGrave);
            TextView txtFaltaGravisima = view.findViewById(R.id.txt_FaltaGravisima);
            TextView txtNombreAlumno = view.findViewById(R.id.txt_nombreAlumno);
            TextView txtrut = view.findViewById(R.id.txt_rutAlumno);

            if (position < items.size()) {
                // Obtener el objeto Alumno en la posición actual
                AlumnoListado alumno = items.get(position);

                // Asignar los valores a los TextView correspondientes
                lblFaltaLeve.setText("F.Leves: ");
                lblFaltaGrave.setText("F.Grave: ");
                lblFaltaGravisima.setText("F.Gravísimas: ");
                txtFaltaLeve.setText(String.valueOf(alumno.getFaltaLeve()));
                txtFaltaGrave.setText(String.valueOf(alumno.getFaltaGrave()));
                txtFaltaGravisima.setText(String.valueOf(alumno.getFaltaGravisima()));
                txtNombreAlumno.setText(alumno.getNombre());
                txtrut.setText(alumno.getRut());
            } else {
                // Si no hay suficientes elementos en la lista, ocultar la vista
                view.setVisibility(View.GONE);
            }

            ImageView imagenAlumnos = view.findViewById(R.id.imagen_alumnos);
            // Agregar el click listener al item
            imagenAlumnos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAlumnoClickListener != null) {
                        onAlumnoClickListener.onAlumnoClick(items.get(position));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    // Interfaz para el listener
    public interface OnAlumnoClickListener {
        void onAlumnoClick(AlumnoListado alumno);
    }
}