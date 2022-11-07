package com.example.teresa.orologiomondiale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CitiesResearchAdapter extends ArrayAdapter<Citta> {

    //devo definire questi due costruttori
    public CitiesResearchAdapter(Context context, int
            textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CitiesResearchAdapter(Context context, int
            resource, List<Citta> items) {
        super(context, resource, items);
    }

    //getView() va sovrascritto per dire all'adapter quali dati deve inserire in una cella di una lista
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //position: indice dell'elemento che deve essere popolato

        View v = convertView;
        //convertView: view che stiamo riciclando (utile per liste molto lunghe)
        if (v == null) { //v è null se non stiamo riciclando nessuna view
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.ricerca_list_item, null);
        }

        Citta p = getItem(position);
        if (p != null) {
            TextView nomeTextView = (TextView) v.findViewById(R.id.nome_textView);
            nomeTextView.setText(p.getName());
            TextView nazioneTextView = (TextView) v.findViewById(R.id.nazione_textView);
            nazioneTextView.setText(p.getCountryName());
        }
        return v;
    }
}
