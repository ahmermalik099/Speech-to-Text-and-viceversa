package com.example.proj;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText outputText;
    String selectedLanguageCodeK="en-GB";
    ProgressDialog progressDialog2;
    public String outputString2;
    private String inputCode2, outputCode2;
    private String tranlateFromText2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        outputText=(EditText) findViewById(R.id.txt_output);

        //new dialog
        progressDialog2 = new ProgressDialog(MainActivity2.this);
        progressDialog2.setIndeterminate(true);
        progressDialog2.setTitle("Converting !");
        progressDialog2.setMessage("Please Wait");
        progressDialog2.setCancelable(true);

        //new dialog ends

        Spinner spinner =findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }



    public void setLanguage(Activity activity, String language, String languageCode)
    {
        selectedLanguageCodeK=languageCode;
        Locale locale= new Locale(language);
        Locale.setDefault(locale);
        Resources resources=activity.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());
    }

    public void btnspeech(View view)
    {
        Intent intent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"speak");
        try {
            startActivityForResult(intent, 1);
        }
        catch(ActivityNotFoundException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    public void translatetext(View view)
    {
        tranlateFromText2 = outputText.getText().toString();
        if (tranlateFromText2.equals("")) {
            Toast.makeText(MainActivity2.this, "Type something first", Toast.LENGTH_SHORT).show();
        } else {
            new MainActivity2.TranslateAsynctask().execute(tranlateFromText2);
            outputText.setText("");
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 1:
                if(resultCode==RESULT_OK && null!=data)
                {
                    ArrayList<String> result= data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    outputText.setText(result.get(0));
                }
                break;
        }

    }

    public void activity3(View view)
    {
        Intent intent=new Intent(MainActivity2.this,MainActivity3.class);
        startActivity(intent);
    }

    public void activity4(View view)
    {
        Intent intent=new Intent(MainActivity2.this,MainActivity4.class);
        startActivity(intent);
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


    //new language code

    public class TranslateAsynctask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog2.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            //  callUrlAndParseResult(params[0]);
            try {
                callUrlAndParseResult(inputCode2, outputCode2, params[0]);
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

        String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + "en-GB" + "&tl=" + selectedLanguageCodeK + "&dt=t&q=" + URLEncoder.encode(word, "UTF-8");


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
        outputString2 = response;
        outputText.setText(outputString2);
        progressDialog2.dismiss();
        return response;


    }
}