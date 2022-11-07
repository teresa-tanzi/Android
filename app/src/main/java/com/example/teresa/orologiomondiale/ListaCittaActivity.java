package com.example.teresa.orologiomondiale;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.support.v4.app.FragmentActivity;
import android.app.ListFragment;

public class ListaCittaActivity extends AppCompatActivity {

    Button conversioneButton;

    private android.support.v4.app.FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_citta);

        //controllo se ci sono già i fragment (serve per la rotazione)
        /*String tag = "lista_land";
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.findFragmentByTag(tag) == null) {
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            ListaCittaFragment listFragment = new ListaCittaFragment();
            fragmentTransaction.add(R.id.fragment_lista_citta, listFragment, tag);
            fragmentTransaction.commit();
        }*/

        //non va
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (savedInstanceState == null) {
            ListaCittaFragment lvfrag = new ListaCittaFragment();
            fragmentTransaction.replace(R.id.fragment_lista_citta, lvfrag, "lvfrag");
            fragmentTransaction.commit();
        }

        //leggo nelle shared preferences se c'è quella che segnala il primo
        Boolean isFirstRun=getSharedPreferences("FIRSTRUN", MODE_PRIVATE).getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show start activity
            getSharedPreferences("FIRSTRUN", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
            startActivity(new Intent(ListaCittaActivity.this, InserisciCittaActivity.class));
            Log.d("First run?", "true");
        } else {
            Log.d("First run?", "false");
        }

        conversioneButton=(Button)findViewById(R.id.conversione_button);

        conversioneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ConversioneActivity.class);
                startActivity(i);
            }
        });
    }
}
