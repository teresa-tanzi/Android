package com.example.teresa.orologiomondiale;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class ConversioneActivity extends AppCompatActivity {
    Button aggiungiOrologioButton;
    Spinner citta1Spinner;
    Spinner citta2Spinner;
    EditText dataEditText;
    EditText oraEditText;
    TextView result;
    Button selectDateButton, selectHourButton;

    String tz1, tz2;
    String dataI, oraI;

    int anno, mese, giorno;
    int ora, minuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversione);

        aggiungiOrologioButton=(Button)findViewById(R.id.aggiungi_orologio_c_button);
        citta1Spinner=(Spinner)findViewById(R.id.citta1_spinner);
        citta2Spinner=(Spinner)findViewById(R.id.citta2_spinner);
        dataEditText=(EditText)findViewById(R.id.data_editText);
        oraEditText=(EditText)findViewById(R.id.ora_editText);
        result=(TextView)findViewById(R.id.risultato_textView);
        selectDateButton=(Button)findViewById(R.id.select_data_button);
        selectHourButton=(Button)findViewById(R.id.select_ora_button);

        aggiungiOrologioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), InserisciCittaActivity.class);
                i.putExtra("sourceActivity", "ConversioneActivity");
                startActivity(i);
            }
        });

        //leggo il file
        String cittaSalvate=caricaCittaSalvate();

        //trasformo da String a ListOfCities con GSON
        Gson gson = new Gson();
        ListOfCities listaCittaSalvate=gson.fromJson(cittaSalvate, ListOfCities.class);

        //associo la ListOfCities all'adapter ed inserisco l'adapter negli spinner
        CitiesConversionAdapter conversionAdapter = new CitiesConversionAdapter(this, android.R.layout.simple_spinner_item, listaCittaSalvate);
        citta1Spinner.setAdapter(conversionAdapter);
        citta2Spinner.setAdapter(conversionAdapter);

        citta1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Citta c1=(Citta)citta1Spinner.getSelectedItem();
                tz1=c1.getTimezone();
                Log.d ("timezone1", tz1);
                changeDate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        citta2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Citta c2=(Citta)citta2Spinner.getSelectedItem();
                tz2=c2.getTimezone();
                Log.d ("timezone2", tz2);
                changeDate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //apro il datePicker
                datePicker();
            }
        });

        selectHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //apro il timePiker
                timePicker();
            }
        });

        dataEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dataI = dataEditText.getText().toString();
                Log.d("data", dataI);
                changeDate();
            }
        });

        oraEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                oraI=oraEditText.getText().toString();
                Log.d ("ora", oraI);
                changeDate();
            }
        });

    }

    public String caricaCittaSalvate() {
        String fileName="citta3_file.txt";
        FileInputStream fis=null;
        String s="";

        try {
            fis=this.openFileInput(fileName);
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, ListaCittaActivity.class);
        startActivity(i);

        return;
    }

    //TODO: non voglio partire dal 1900 (magari mettere di default la data di oggi), inoltre esce la tastiera numerica
    private void datePicker() {
        Calendar c=Calendar.getInstance();
        anno=c.get(Calendar.YEAR);
        mese=c.get(Calendar.MONTH);
        giorno=c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog=new DatePickerDialog(ConversioneActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dataEditText.setText(dayOfMonth+"/"+(month+1)+"/"+year);
            }
        }, giorno, mese, anno);

        datePickerDialog.show();
    }

    private void timePicker() {
        final Calendar c=Calendar.getInstance();
        ora=c.get(Calendar.HOUR_OF_DAY);
        minuto=c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog=new TimePickerDialog(ConversioneActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                oraEditText.setText(hourOfDay+":"+minute);
            }
        }, ora, minuto, true);

        timePickerDialog.show();
    }

    public void changeDate() {
        //if (!tz1.equals("") && !tz2.equals("") && !dataI.equals("") && !oraI.equals("")) {
        if (tz1!=null && tz2!=null && dataI!=null && oraI!=null) {
            //converto i dati ottenuti
            String fullData=dataI+" "+oraI;
            Date data1=null;
            TimeZone timeZone1, timeZone2;

            timeZone1=TimeZone.getTimeZone(tz1);
            timeZone2=TimeZone.getTimeZone(tz2);

            DateFormat formatterIn=new SimpleDateFormat("dd/MM/yyyy HH:mm");
            formatterIn.setTimeZone(timeZone1);
            try {
                data1=formatterIn.parse(fullData);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (data1!=null) {
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(data1);
                TimeZone fromTimeZone = timeZone1;
                TimeZone toTimeZone = timeZone2;

                calendar.setTimeZone(fromTimeZone);
                calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);
                if (fromTimeZone.inDaylightTime(calendar.getTime())) {
                    calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
                }

                calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());
                if (toTimeZone.inDaylightTime(calendar.getTime())) {
                    calendar.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
                }

                DateFormat formatterOut = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                formatterOut.setCalendar(calendar);
                String data2 = formatterOut.format(calendar.getTime());

                Log.d("Nuova timezone", data2);
                result.setText(data2);
            }
        } else {
            //non fa nulla
        }
    }
}
