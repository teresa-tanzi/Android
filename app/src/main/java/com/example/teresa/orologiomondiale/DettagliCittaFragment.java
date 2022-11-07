package com.example.teresa.orologiomondiale;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DettagliCittaFragment extends Fragment {

    Button deleteButton;
    TextView nomeTextView;
    TextView nazioneTextView;
    TextView timezoneTextView;
    TextView oraTextView;
    TextView albaTextView;
    TextView tramontoTextView;

    Activity a;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        a=getActivity();
        Log.d ("Activity in DettFrag", a.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_dettagli_citta, container, false);

        deleteButton=(Button)v.findViewById(R.id.elimina_citta_button);
        nomeTextView=(TextView)v.findViewById(R.id.nome_dettaglio_textView);
        nazioneTextView=(TextView)v.findViewById(R.id.nazione_dettaglio_textView);
        timezoneTextView=(TextView)v.findViewById(R.id.timezone_dettaglio_textView);
        oraTextView=(TextView)v.findViewById(R.id.ora_dettaglio_textView);
        albaTextView=(TextView)v.findViewById(R.id.alba_dettaglio_textView);
        tramontoTextView=(TextView)v.findViewById(R.id.tramonto_dettaglio_textView);

        //ricevo i dati dal bundle
        Bundle extras=getArguments();
        if (extras!=null) {

            final String nome = extras.getString("nome");
            final String nazione = extras.getString("nazione");
            String timezone = extras.getString("timezone");
            final String lat = extras.getString("lat");
            final String lng = extras.getString("lng");

            //scrivo i dati ricevuti nelle textView
            nomeTextView.setText(nome);
            nazioneTextView.setText(nazione);
            timezoneTextView.setText("Timezone: "+timezone);

            //calcolo l'ora in questa città e la metto in oraTextView
            Date d = new Date();
            Calendar localTime = Calendar.getInstance();
            localTime.setTime(d); //ora di adesso qui

            Calendar calendar = new GregorianCalendar();
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            formatter.setCalendar(calendar);
            formatter.setTimeZone(TimeZone.getTimeZone(timezone));

            oraTextView.setText("Ora corrente: "+formatter.format(calendar.getTime()));

            //connetto al server per ricavare alba e tramonto e metto i valori nelle textView
            //TODO: se voglio implementare uno spinner o qualcosa mentre aspetto i dati devo usare AsyncTask (in effetti ci mette davvero tanto)
            //Initiate the request
            RequestQueue queue = Volley.newRequestQueue(getContext());
            String url = "http://api.geonames.org/timezoneJSON?lat=" + lat + "&lng=" + lng + "&username=teresa.tanzi@studenti.unimi.it";

            //Request a JSON response from the provider URL
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d("response", response.toString());

                    //lavoro sull'oggetto JSON tramite la libreria GSON
                    String responseStr = response.toString();
                    Gson gson = new Gson();
                    CittaTz citta = gson.fromJson(responseStr, CittaTz.class);
                    Log.d("prova", citta.toString());

                    String albaS = citta.getSunrise();
                    Log.d("alba", albaS);
                    String tramontoS = citta.getSunset();
                    Log.d("tramonto", tramontoS);

                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-DD HH:mm");
                        Date alba = formatter.parse(albaS);
                        formatter.applyPattern("HH:mm");
                        String newAlba = formatter.format(alba);
                        albaTextView.setText("Ora alba: "+newAlba);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-DD HH:mm");
                        Date tramonto = formatter.parse(tramontoS);
                        formatter.applyPattern("HH:mm");
                        String newTramonto = formatter.format(tramonto);
                        tramontoTextView.setText("Ora tramonto: "+newTramonto);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialog.Builder errore = new AlertDialog.Builder(getContext());
                    errore.setMessage("That didn't work");
                    Log.d("error", "That didn't work!");
                }

            });

            //Add the request to the requestQueue
            queue.add(jsObjRequest);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    //apro una finestra di dialogo per chiedere conferma
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setTitle("Conferma");
                    alertDialogBuilder.setMessage("Vuoi eliminare " + nome + "?");

                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            cancellazioneCitta(lat, lng);
                            //TODO: aggiorno l'adapter tramite il singleton NON FUNZIONA
                            //CitiesSavedAdapter adapter = MySingleton.getInstance().getAdapter();
                            //adapter.notifyDataSetChanged();

                            //ListView list = (ListView) getActivity().findViewById(R.id.citta_salvate_listView);
                            //((BaseAdapter)list.getAdapter()).notifyDataSetChanged();

                            ViewPager viewPager=new ViewPager(getContext());
                            viewPager.removeAllViews();
                            a.finish();
                            startActivity(new Intent(a, ListaCittaActivity.class));

                            Toast toast=Toast.makeText(getContext(), "Città eliminata", Toast.LENGTH_LONG);
                            toast.show();

                            //TODO: non elimina la città! mando la città indietro e la cancello dalla main activity (?)
                            //torno a ListaCitta
                            //FragmentManager fm = getActivity().getSupportFragmentManager();
                            //fm.popBackStack();



                        }
                    });

                    alertDialogBuilder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //non faccio niente (non so però se mi si chiude da solo
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        }

        return v;
    }

    public void cancellazioneCitta(String lat, String lng) {
        //leggo il file
        String fileLetto=caricaCittaSalvate();

        //converto la stringa letta in ListOfCities con GSON
        Gson gson=new Gson();
        ListOfCities cittaList=gson.fromJson(fileLetto, ListOfCities.class);

        //elimino la città dall'oggetto ListOfCities (devo creare il metodo)
        cittaList.removeCity(lat, lng);

        //converto la nuova lista in stringa son GSON
        String fileDaScrivere=gson.toJson(cittaList);

        //riscrivo il file
        salvaCitta(fileDaScrivere);
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

    public void salvaCitta (String cittaDaSalvare) {
        Log.d("Sto scrivendo", cittaDaSalvare);

        String fileName="citta3_file.txt";
        FileOutputStream fos=null;

        try {
            fos=getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(cittaDaSalvare.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        /*if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }*/
    }
}


