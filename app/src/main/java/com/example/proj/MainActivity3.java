package com.example.proj;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

public class MainActivity3 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText etInput;
    Button btConvert, btClear;
    ProgressDialog progressDialog;
    TextToSpeech textToSpeech;
    private String tranlateFromText = "";
    public String outputString;
    private String inputCode, outputCode;
    Intent intent;
    String recievedCode;
    String selectedLanguageCodeK2="en-GB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        intent = getIntent();
        recievedCode = intent.getStringExtra("languageCode");
        progressDialog = new ProgressDialog(MainActivity3.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Converting !");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(true);

        etInput = findViewById(R.id.et_input);
        btConvert = findViewById(R.id.bt_convert);
        btClear = findViewById(R.id.bt_clear);


        //new sppiner
        Spinner spinner =findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.languages, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter2);
        spinner.setOnItemSelectedListener(this);

        //new ends


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        btConvert.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             tranlateFromText = etInput.getText().toString();
                                             if (tranlateFromText.equals("")) {
                                                 Toast.makeText(MainActivity3.this, "Type something first", Toast.LENGTH_SHORT).show();
                                             } else {
                                                 new TranslateAsynctask().execute(tranlateFromText);
                                                 etInput.setText("");
                                             }
                                         }
                                     }

        );

        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etInput.setText("");
            }
        });
    }
    public void activity2(View view)
    {
        Intent intent=new Intent(MainActivity3.this,MainActivity2.class);
        startActivity(intent);
    }

    public void activity4(View view)
    {
        Intent intent=new Intent(MainActivity3.this,MainActivity4.class);
        startActivity(intent);
    }

    //new language code
    public void setLanguage(Activity activity, String language, String languageCode2)
    {
        selectedLanguageCodeK2=languageCode2;
        Locale locale= new Locale(language);
        Locale.setDefault(locale);
        Resources resources=activity.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        String text=parent.getItemAtPosition(position).toString();
        // Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
        if(position==0)
        {
            setLanguage(this,"en","en-GB");
            Toast.makeText(this, "english", Toast.LENGTH_SHORT).show();
        }
        else if(position==1)
        {
            setLanguage(this,"ur","ur-PK");
            Toast.makeText(this, "urdu", Toast.LENGTH_SHORT).show();

        }

        else if(position==2)
        {
            setLanguage(this,"es","es-ES");
            Toast.makeText(this, "spanish", Toast.LENGTH_SHORT).show();

        }

        else if(position==3)
        {
            setLanguage(this,"en_gb","en-GB");
            Toast.makeText(this, "gb", Toast.LENGTH_SHORT).show();

        }

        else if(position==4)
        {
            setLanguage(this,"zh","zh");
            Toast.makeText(this, "china", Toast.LENGTH_SHORT).show();

        }

        else if(position==5)
        {
            setLanguage(this,"ar","ar-SA");
            Toast.makeText(this, "arab", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    //new lang code ends

    public class TranslateAsynctask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            //  callUrlAndParseResult(params[0]);
            try {
                callUrlAndParseResult(inputCode, outputCode, params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


        }
    }

    private String callUrlAndParseResult(String langFrom, String langTo,
                                         String word) throws Exception {

        String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + "en-GB" + "&tl=" + selectedLanguageCodeK2 + "&dt=t&q=" + URLEncoder.encode(word, "UTF-8");


        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return parseResult(response.toString());
    }

    private String parseResult(String inputJson) throws Exception {

        JSONArray jsonArray = new JSONArray(inputJson);
        JSONArray jsonArray2 = (JSONArray) jsonArray.get(0);


        String response = "";
        for (int i = 0; i < jsonArray2.length(); i++) {
            response = response + ((JSONArray) jsonArray2.get(i)).get(0).toString();
        }
        Log.e("response", "Result" + response);
        outputString = response;
        etInput.setText(outputString);
        progressDialog.dismiss();
        return response;


    }
}