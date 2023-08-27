package com.rod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class AddConfiguration extends AppCompatActivity {
    private static final String TAG = "AddConfiguration";
    JSONObject jsonData;
    String useKeyName;
    private void writeData(String key, String value) throws JSONException, IOException {
        jsonData.put(key, value);
        Log.i(TAG, "writeData: =======================================================");
        Log.i(TAG, "writeData: "+jsonData.toString());
        FileOutputStream fileOutputStream = this.openFileOutput("Configurations", Context.MODE_PRIVATE);
        fileOutputStream.write(jsonData.toString().getBytes(StandardCharsets.UTF_8));
        fileOutputStream.close();
    }

    private Iterator<String> getKeys(){
        JSONObject rsaKeys = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput("RSAKeys")));
            rsaKeys = new JSONObject(bufferedReader.readLine());
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
        return rsaKeys.keys();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_configuration);

        useKeyName = this.getString(R.string.defaultKeyName);
        Spinner useKeySpinner = findViewById(R.id.check_useKeySpinner);
        FileInputStream fileInputStream = null;


        Iterator<String> keysIterator = getKeys();
        ArrayList<String> keysList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddConfiguration.this, android.R.layout.simple_list_item_1, keysList);
        useKeySpinner.setAdapter(arrayAdapter);
        while(keysIterator.hasNext()){
            arrayAdapter.add(keysIterator.next());
        }

        useKeySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                useKeyName = ((TextView)view).getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button confirm = findViewById(R.id.toAddConfiguration);
        Intent intent = getIntent();
        try {
            jsonData = new JSONObject(Objects.requireNonNull(intent.getStringExtra("jsonData")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText configurationName = findViewById(R.id.configurationName);
                EditText hostEditText = findViewById(R.id.host);
                EditText videoPort = findViewById(R.id.videoPort);
                EditText mousePort = findViewById(R.id.mousePort);
                String name = configurationName.getText().toString();
                String host = hostEditText.getText().toString();
                String port1 = videoPort.getText().toString();
                String port2 = mousePort.getText().toString();
//                valid parameters
                if (host.isEmpty()){
                    Context context = getApplicationContext();
                    Toast.makeText(context,context.getString(R.string.emptyHostRemind),Toast.LENGTH_SHORT).show();
                } else if (port1.isEmpty()) {
                    Context context = getApplicationContext();
                    Toast.makeText(context,context.getString(R.string.emptyPort1Remind),Toast.LENGTH_SHORT).show();
                } else if (port2.isEmpty()) {
                    Context context = getApplicationContext();
                    Toast.makeText(context,context.getString(R.string.emptyPort2Remind),Toast.LENGTH_SHORT).show();
                } else if (host.contains(",")){
                    Context context = getApplicationContext();
                    Toast.makeText(context,context.getString(R.string.host)+context.getString(R.string.noCommaRemind),Toast.LENGTH_SHORT).show();
                }else{
                    if (name.isEmpty()){
                        name = host+":"+port1+"&"+port2+"--"+useKeyName;
                    }
                    try {
                        writeData(name, host+","+port1+","+port2+","+useKeyName);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            }
        });
    }
}