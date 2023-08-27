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

public class CheckConfiguration extends AppCompatActivity {
    private String configurationName;
    private String useKeyName;
    private void writeData(String key, String value) throws JSONException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput("Configurations")));
        JSONObject jsonObject = new JSONObject(bufferedReader.readLine());
        jsonObject.put(key, value);
        FileOutputStream fileOutputStream = openFileOutput("Configurations", Context.MODE_PRIVATE);
        fileOutputStream.write(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
        fileOutputStream.close();
    }
    private String readConfiguration() throws IOException, JSONException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput("Configurations")));
        String data = bufferedReader.readLine();
        JSONObject jsonData = new JSONObject(data);
        return jsonData.getString(configurationName);
    }
    private void deleteConfiguration() throws IOException, JSONException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput("Configurations")));
        String data = bufferedReader.readLine();
        bufferedReader.close();
        JSONObject jsonData = new JSONObject(data);
        jsonData.remove(configurationName);
        FileOutputStream fileOutputStream = openFileOutput("Configurations", Context.MODE_PRIVATE);
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
        setContentView(R.layout.activity_check_configuration);

        Intent intent = getIntent();
        configurationName = intent.getStringExtra("configurationName");

        TextView configurationNameTextView = findViewById(R.id.check_configurationName);
        EditText host = findViewById(R.id.check_host);
        EditText videoPort = findViewById(R.id.check_port1);
        EditText mousePort = findViewById(R.id.check_port2);
        Spinner useKeySpinner = findViewById(R.id.check_useKeySpinner);

        configurationNameTextView.setText(configurationName);

        String[] data;
        try {
            data = readConfiguration().split(",");
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
        host.setText(data[0]);
        videoPort.setText(data[1]);
        mousePort.setText(data[2]);

        useKeyName = data[3];


        Iterator<String> keysIterator = getKeys();
        ArrayList<String> keysList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(CheckConfiguration.this, android.R.layout.simple_list_item_1, keysList);
        useKeySpinner.setAdapter(arrayAdapter);
        int i = 0;
        int selected = 0;
        while(keysIterator.hasNext()){
            String keyName = keysIterator.next();
            if (keyName.equals(useKeyName)){
                selected = i;
            }else {
                i++;
            }
            arrayAdapter.add(keyName);
        }
        useKeySpinner.setSelection(selected);

        useKeySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                useKeyName = ((TextView)view).getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ;
            }
        });

        Button confirm = findViewById(R.id.check_confirm);
        Button delete = findViewById(R.id.deleteConfiguration);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText hostEditText = findViewById(R.id.check_host);
                EditText videoPort = findViewById(R.id.check_port1);
                EditText mousePort = findViewById(R.id.check_port2);
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
                    try {
                        writeData(configurationName, host+","+port1+","+port2+","+useKeyName);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteConfiguration();
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
                finish();
            }
        });

    }
}