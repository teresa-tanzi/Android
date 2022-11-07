package com.example.teresa.orologiomondiale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class CitiesSavedAdapter extends ArrayAdapter<Citta> {
    //devo definire questi due costruttori
    public CitiesSavedAdapter(Context context, int
            textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CitiesSavedAdapter(Context context, int
            resource, List<Citta> items) {
        super(context, resource, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.salvate_list_item, null);
        }

        Citta p = getItem(position);
        if (p != null) {
            TextView nomeTextView = (TextView) v.findViewById(R.id.nome_textView);
            nomeTextView.setText(p.getName());
            TextView nazioneTextView = (TextView) v.findViewById(R.id.nazione_textView);
            nazioneTextView.setText(p.getCountryName());
            TextView oraTextView = (TextView) v.findViewById(R.id.ora_textView);

            String tz=p.getTimezone();
            Date d=new Date();
            Calendar localTime=Calendar.getInstance();
            localTime.setTime(d); //ora di adesso qui

            Calendar calendar = new GregorianCalendar();
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            formatter.setCalendar(calendar);
            formatter.setTimeZone(TimeZone.getTimeZone(tz));

            oraTextView.setText(formatter.format(calendar.getTime()));
        }
        return v;
    }
}
