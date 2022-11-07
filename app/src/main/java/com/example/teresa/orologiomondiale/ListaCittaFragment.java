package com.example.teresa.orologiomondiale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.logging.SimpleFormatter;

import static android.R.attr.data;
import static android.view.View.VISIBLE;

public class ListaCittaFragment extends Fragment {

    //creo le variabili che associerò agli elementi dell'interfaccia
    private Button aggiungiOrologioButton;
    private ListView cittaListView;
    private CitiesSavedAdapter listAdapter;
    private ListOfCities cittaList;
    private TextView dataQuiTextView;

    boolean mDuelPane;

    Activity a;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        a=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_lista_citta, container, false);

        //associo gli elementi dell'interfaccia alle loro variabili
        cittaListView=(ListView)v.findViewById(R.id.citta_salvate_listView);
        aggiungiOrologioButton=(Button)v.findViewById(R.id.aggiungi_orologio_button);
        dataQuiTextView=(TextView)v.findViewById(R.id.data_qui_textView);

        //sono in portrait mode o in landscape mode? mDuelPane=true se in landscape
        View dettailsFrame=v.findViewById(R.id.fragment_dettagli_citta);
        mDuelPane=dettailsFrame != null && dettailsFrame.getVisibility() == View.VISIBLE;

        aggiungiOrologioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(a, InserisciCittaActivity.class);
                i.putExtra("sourceActivity", "ListaCittaFragment");
                startActivity(i);
            }
        });

        Date d=new Date();
        Calendar localTime = Calendar.getInstance();
        localTime.setTime(d); //ora di adesso qui

        Calendar calendar = new GregorianCalendar();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        formatter.setCalendar(calendar);

        dataQuiTextView.setText(formatter.format(calendar.getTime()));

        //carico le citta salvate
        String cittaSalvate=caricaCittaSalvate();

        if (!cittaSalvate.equals("")) {

            Gson gson = new Gson();
            cittaList = gson.fromJson(cittaSalvate, ListOfCities.class); //qui dentro ci sono le città che ho salvato nel file

        } else {
            cittaList=new ListOfCities();
        }

        //creo l'adapter per poter inserire dinammicamente gli elementi nella lista
        if (listAdapter==null) { //listAdapter: null continua a ricrearlo
            listAdapter = new CitiesSavedAdapter(getContext(), android.R.layout.list_content, cittaList);
            cittaListView.setAdapter(listAdapter);
            Log.d("ADAPTER1", listAdapter.toString());
        }
        listAdapter.notifyDataSetChanged();


        //collego l'adapter al singleton
        MySingleton.getInstance().setAdapter(listAdapter);
        Log.d("adapter", listAdapter.toString());

        //Intent i=getActivity().getIntent();
        //listAdapter.notifyDataSetChanged();

        //al click su un elemento della listView vado a DettagliCittaFragment (comunicazione tra due fragment)
        cittaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Citta c=(Citta)parent.getAdapter().getItem(position);
                String nome=c.getName();
                String nazione=c.getCountryName();
                String timezone=c.getTimezone();
                String lat=c.getLat();
                String lng=c.getLng();
                //l'ora corrente della città la calcolo direttamente nel fragment dei dettagli
                //per l'ora dell'alba e del tramonto dovrò fare una nuova connessione al server

                Bundle data = new Bundle();
                data.putString("nome",nome);
                data.putString("nazione", nazione);
                data.putString("timezone", timezone);
                data.putString("lat", lat);
                data.putString("lng", lng);

                FragmentTransaction ft=getFragmentManager().beginTransaction();
                DettagliCittaFragment myFrag = new DettagliCittaFragment();
                myFrag.setArguments(data);

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //landscape
                    ft.replace(R.id.fragment_dettagli_citta, myFrag).addToBackStack("tag"); //addToBackStack serve per tornare qui quando premo "indietro"
                    Log.d("orientation", "landscape");
                } else { //portrait
                    ft.replace(R.id.fragment_lista_citta, myFrag).addToBackStack("tag"); //addToBackStack serve per tornare qui quando premo "indietro"
                    Log.d("orientation", "portrait");
                }

                ft.commit();

            }
        });

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        listAdapter.notifyDataSetChanged();
        Log.d("BBB", "dentro onResume di ListaCitaFragment");
    }

    public String caricaCittaSalvate() {
        String fileName="citta3_file.txt";
        FileInputStream fis=null;
        String s="";

        try {
            fis=getContext().openFileInput(fileName);
            byte fileContent[] = new byte[fis.available()]; //quanti byte devo leggere del file (tutti, quindi quanti la sua lunghezza)
            Log.d("numero byte", ""+fis.available());
            fis.read(fileContent);
            s=new String(fileContent);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("File letto", s);
        return s;
    }
}
