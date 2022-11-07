package com.example.teresa.orologiomondiale;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InserisciCittaActivity extends AppCompatActivity {

    private String sourceActivity;
    private EditText input;
    private ListView citta_trovate;
    private ListOfCities listOfCities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserisci_citta);

        sourceActivity=getIntent().getStringExtra("sourceActivity");

        input=(EditText)findViewById(R.id.inserisci_citta_editText);
        citta_trovate=(ListView)findViewById(R.id.citta_trovate_listView);
        listOfCities=new ListOfCities();

        //creo l'adapter per poter inserire dinammicamente gli elementi nella lista
        final CitiesResearchAdapter ricercaAdapter=new CitiesResearchAdapter(this, android.R.layout.list_content, (List<Citta>) listOfCities);
        citta_trovate.setAdapter(ricercaAdapter);

        //TODO: imposto le città di base che stanno nella list view
        InizializzaRicerca();

        input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //onKey viene eseguito due volte: onKeyDown e onKeyUp: pongo la condizione di eseguirlo solo onKeyUp
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    String inputStr = input.getText().toString();
                    Log.d("AAA", inputStr);

                    //faccio la chiamata al server web tramite volley
                    //Initiate the request
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    String url = "http://api.geonames.org/search?name_startsWith=" + inputStr + "&username=teresa.tanzi@studenti.unimi.it&type=json&orderby=relevance&maxRows=10&fclass=p";

                    //Request a JSON response from the provider URL
                    JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", response.toString());

                            //lavoro sull'oggetto JSON tramite la libreria GSON
                            String responseStr=response.toString();
                            Gson gson=new Gson();
                            CittaRicercate citta=gson.fromJson(responseStr, CittaRicercate.class);
                            Log.d("prova", citta.toString());

                            //prima di aggiungere elementi all'array lo svuoto, altrimenti quelli nuovi vengono aggiunti in fondo
                            listOfCities.removeAllCities();
                            ricercaAdapter.notifyDataSetChanged();

                            //ciclo su tutte le città che ricevo
                            Citta[] geonames=citta.getGeonames();

                            for (Citta c: geonames) {
                                String name=c.getName();
                                String country=c.getCountryName();
                                String lat=c.getLat();
                                String lng=c.getLng();

                                listOfCities.add(c);
                                ricercaAdapter.notifyDataSetChanged(); //avviso l'adapter che qualcosa è cambiato e di aggiornare la view
                            }

                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AlertDialog.Builder errore = new AlertDialog.Builder(getApplicationContext());
                            errore.setMessage("That didn't work");
                            Log.d("error", "That didn't work!");
                        }

                    });

                    //Add the request to the requestQueue
                    queue.add(jsObjRequest);
                }

                return false;
            }
        });

        citta_trovate.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Citta c=(Citta)parent.getAdapter().getItem(position);
                String nome=c.getName();
                String nazione=c.getCountryName();
                String lat=c.getLat();
                String lng=c.getLng();

                Log.d("Hai clickato su", nome);

                //apro una finestra di dialogo per chiedere conferma
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InserisciCittaActivity.this);
                alertDialogBuilder.setTitle("Conferma");
                alertDialogBuilder.setMessage("Vuoi aggiungere "+nome+" ai preferiti?");

                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //prima di salvare la città devo riconnetermi al web per cercare la timezone
                        aggiungiTimezone(c);

                        //aggiorno l'adapter tramite il singleton
                        /*CitiesSavedAdapter adapter=MySingleton.getInstance().getAdapter();
                        adapter.notifyDataSetChanged();
                        Log.d ("adapter_inserisci", adapter.toString());*/
                        //MySingleton.getInstance().notifyMyAdapter();
                        CitiesSavedAdapter savedAdapter=MySingleton.getInstance().getAdapter();
                        Log.d("Inserisci citta", "sono qui");
                        Log.d("ADAPTER2",savedAdapter.toString());
                        //savedAdapter.notifyDataSetInvalidated();
                        savedAdapter.notifyDataSetChanged();

                        /*ViewPager viewPager=new ViewPager(getApplicationContext());
                        viewPager.removeAllViews();
                        //finish();
                        //startActivity(new Intent(getApplicationContext(), ListaCittaActivity.class));
                        Log.d("Nuovo?", "Yep3");
                        finishAffinity();
                        startActivity(new Intent(getApplicationContext(), ListaCittaActivity.class));*/

                        Toast toast=Toast.makeText(getApplicationContext(), "Città salvata", Toast.LENGTH_LONG);
                        toast.show();

                        //torno a listaCittaActivity
                        //onBackPressed();
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

    @Override
    public void onBackPressed() {
        if (sourceActivity.equals("ListaCittaFragment")) {
            Intent i = new Intent(this, ListaCittaActivity.class);
            startActivity(i);
        } else {
            if (sourceActivity.equals("ConversioneActivity")) {
                Intent i = new Intent(this, ConversioneActivity.class);
                startActivity(i);
            } else { //vuol dire che è il primo avvio e non arrivo da niente: vado a Lista citta
                Intent i = new Intent(this, ListaCittaActivity.class);
                startActivity(i);
            }
        }
        super.onBackPressed();
    }

    public void salvaCitta (Citta c) {
        String cittaDaSalvare="";

        String s=leggiCitta();
        //se ci sono già delle citta salvate le porto in JSON ed aggiungo la citta
        if (!(s.equals(""))) {
            //unmarshaling dei dati e creo poi la nuova stringa
            Gson gson1=new Gson();
            ListOfCities cittaList=gson1.fromJson(s, ListOfCities.class); //qui dentro ci sono le città che ho salvato nel file
            cittaList.addCity(c); //aggiungo la nuova città
            //devo riportare ora tutto a String
            Gson gson2=new Gson();
            String json=gson2.toJson(cittaList);
            cittaDaSalvare=json;
        } else {
            //la stringa da inserire è solo la città, ma devo creare l'array di citta (CittaSalvate) ed inserirvi la città
            ListOfCities cittaList=new ListOfCities();
            cittaList.addCity(c);
            Gson gson=new Gson();
            String json=gson.toJson(cittaList);
            cittaDaSalvare=json;
        }

        Log.d("Sto scrivendo", cittaDaSalvare);

        String fileName="citta3_file.txt";
        FileOutputStream fos = null;

        try {
            fos=openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(cittaDaSalvare.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //leggo il file e ritorno o la stringa o l'oggetto JSON (quindi la classe), meglio l'oggetto
    public String leggiCitta() {
        String fileName="citta3_file.txt";
        FileInputStream fis=null;
        String s="";

        try {
            fis=openFileInput(fileName);
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

    //conosco la città, ma connetto ancora al server per trovare la timezone e salvarla nel file insieme alla città
    public void aggiungiTimezone(final Citta c) {
        String lat=c.getLat();
        String lng=c.getLng();

        //faccio la chiamata al server web tramite volley
        //Initiate the request
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://api.geonames.org/timezoneJSON?lat="+lat+"&lng="+lng+"&username=teresa.tanzi@studenti.unimi.it";

        //Request a JSON response from the provider URL
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", response.toString());

                //lavoro sull'oggetto JSON tramite la libreria GSON
                String responseStr=response.toString();
                Gson gson=new Gson();
                CittaTz citta=gson.fromJson(responseStr, CittaTz.class);
                Log.d("prova", citta.toString());

                String timezone=citta.getTimezoneId();
                Log.d("timezone", timezone);
                c.setTimezone(timezone);

                //salvo la città su file
                salvaCitta(c);

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder errore = new AlertDialog.Builder(getApplicationContext());
                errore.setMessage("That didn't work");
                Log.d("error", "That didn't work!");
            }

        });

        //Add the request to the requestQueue
        queue.add(jsObjRequest);
    }

    public void InizializzaRicerca() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://api.geonames.org/search?username=teresa.tanzi@studenti.unimi.it&type=json&orderby=population&maxRows=10&cities=cities1000";

        //Request a JSON response from the provider URL
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", response.toString());

                //lavoro sull'oggetto JSON tramite la libreria GSON
                String responseStr=response.toString();
                Gson gson=new Gson();
                CittaRicercate citta=gson.fromJson(responseStr, CittaRicercate.class);
                Log.d("prova", citta.toString());

                //ciclo su tutte le città che ricevo
                Citta[] geonames=citta.getGeonames();

                for (Citta c: geonames) {
                    String name=c.getName();
                    String country=c.getCountryName();
                    String lat=c.getLat();
                    String lng=c.getLng();

                    listOfCities.add(c);
                }

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder errore = new AlertDialog.Builder(getApplicationContext());
                errore.setMessage("That didn't work");
                Log.d("error", "That didn't work!");
            }

        });

        //Add the request to the requestQueue
        queue.add(jsObjRequest);
    }
}
