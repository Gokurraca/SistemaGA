package com.appsistema.sistemaga;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private List<GridItem> items;

    public GridAdapter(Context context, List<GridItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //virifico que la vista no sea nula
        if (convertView == null) {
            //inflo el contexto (muestro)
            LayoutInflater inflater = LayoutInflater.from(context);
            //convierto la vista con el diseño predeterminado
            convertView = inflater.inflate(R.layout.diseno_grilla, parent, false);
        }

        //seteo los parametros a la grilla.
        GridItem item = items.get(position);
        //seteo lo componentes
        TextView textFecha = convertView.findViewById(R.id.textFecha);
        TextView textFalta  = convertView.findViewById(R.id.textFalta);
        TextView textCategoria = convertView.findViewById(R.id.textCategoria);
        //seteo las variables correspondientes en la vista.
        textFecha.setText(item.getFecha());
        textFalta.setText(item.getFalta());
        textCategoria.setText(item.getCategoria());

        return convertView;
    }
}

