package com.rod;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Parameter;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button startButton;
    private Spinner useConfiguration;
    private Button toSettingButton;
    private TextView toSettingRemind;
    String useConfigurationName = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.button);
        useConfiguration = findViewById(R.id.useConfiguration);
        toSettingButton = findViewById(R.id.toSetting);
        toSettingRemind = findViewById(R.id.toSettingRemind);

        //        first time to start the app
//        create "RSAKeys" and "Configurations"
        File file0 = new File(getFilesDir()+"/RSAKeys");
        if (!file0.exists()){
            try {
                FileOutputStream fileOutputStream0 = openFileOutput("RSAKeys", MODE_PRIVATE);
                fileOutputStream0.write(("{\""+this.getString(R.string.defaultKeyName)+"\":\"" +
                        "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAlXFBhw7G0bpbtUOe5BPMxq11WtpJkVWOx5biiPX7GySeCHV6uoCip127E6j1L/XqHTDzErm93G/6eEtntjt7IvD9RoYkloBZLfevNuo7NzqFuIJu3y/y/u73mHC9sVU4mNuse93yPuMio+GEiKaDcrIFZmxXkeyhVnCxcXA+ISc2I/Ripwo19oahlihSMgNZP5IPDCGahASk3414eyk/W8asGTvN11PiEW+8xNFfwjDF9pBiIAOZaRk6HvhLQTJhQKN9sKy+333FffihS387R8IU1WevF8q8CUkNbq7FBFFUJDketaALABXSGgExW9KQdaHiDyps7XNHcQzOxre7VjPUACHuUgefS7a6bM5zVU3bjizD0PtaglwFNje47D4jvF6qgu8MSf1ZTqv5EWH/PpW/T39K11tJFGI3yAfyQzmyFqPOZqq1zBNcC5F2CFKp26td9z1+a86jBTqJ8crfeqOE26LdiB2esBAC0E1oIpC/HAvMZoMo2Cb320rL6zHwfaqmV3yE8M2BNj1RODmgZTVsOSmoLgPijdigB/PIEXt2OwLuBq4WXO5AH095xy591BISeTYaePyMOlKsGZtR2wnlcbNRx2D+gi9pkRWbbFhaHwy8RP8oOjf4qV1DHTrutB3d/KLo7CpCAN8dOftmpAswc6+++kVnV/SqW7FDCWMCAwEAAQ==" +
                        "," +
                        "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\"}"
                ).getBytes());
                FileOutputStream fileOutputStream1 = openFileOutput("Configurations", MODE_PRIVATE);
                fileOutputStream1.write("{}".getBytes());
                startButton.setVisibility(View.GONE);
                useConfiguration.setVisibility(View.GONE);
                toSettingButton.setVisibility(View.VISIBLE);
                toSettingRemind.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JSONObject jsonObject;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader( openFileInput("Configurations")));
            jsonObject = new JSONObject(bufferedReader.readLine());
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }

        Iterator<String> iterator = jsonObject.keys();
        ArrayList<String> keysList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, keysList);
        useConfiguration.setAdapter(arrayAdapter);
        while(iterator.hasNext()){
            arrayAdapter.add(iterator.next());
        }

        useConfiguration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                useConfigurationName = ((TextView)view).getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ;
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (useConfigurationName==null){
                    Context context = getApplicationContext();
                    Toast.makeText(context,context.getString(R.string.chooseConfigurationRemind),Toast.LENGTH_SHORT).show();
                }else {
                    String[] data;
                    try {
                       data = jsonObject.getString(useConfigurationName).split(",");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    String host = data[0];
                    int port1 = Integer.parseInt(data[1]);
                    int port2 = Integer.parseInt(data[2]);
                    String useKeyName = data[3];
                    Intent intent = new Intent(MainActivity.this,ControlPanel.class);
                    intent.putExtra("host",host);
                    intent.putExtra("port1",port1);
                    intent.putExtra("port2",port2);
                    intent.putExtra("useKeyName", useKeyName);
                    startActivity(intent);
                }
            }
        });

        toSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onResume() {

        JSONObject jsonObject;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput("Configurations")));
            jsonObject = new JSONObject(bufferedReader.readLine());
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
        if (jsonObject.toString().equals("{}")){ // haven't create any configuration
            startButton.setVisibility(View.GONE);
            useConfiguration.setVisibility(View.GONE);
            toSettingButton.setVisibility(View.VISIBLE);
            toSettingRemind.setVisibility(View.VISIBLE);
        }else { // normal display
            startButton.setVisibility(View.VISIBLE);
            useConfiguration.setVisibility(View.VISIBLE);
            toSettingButton.setVisibility(View.GONE);
            toSettingRemind.setVisibility(View.GONE);
        }

        super.onResume();
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}