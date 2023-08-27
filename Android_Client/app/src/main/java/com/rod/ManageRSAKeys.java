package com.rod;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class ManageRSAKeys extends AppCompatActivity {
    JSONObject jsonData;
    FileInputStream fileInputStream = null;
    ListView listView;
    ImageButton addButton;
    private void fresh(){
        String data = "{}";
        try {
            fileInputStream = openFileInput("RSAKeys");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            try {
                data = bufferedReader.readLine();
            }catch (IOException e){
                e.printStackTrace();
            }
            fileInputStream.close();
        } catch (FileNotFoundException e) { // "RSAKey" doesn't exist
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            jsonData = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Iterator<String> keysIterator = jsonData.keys();
        ArrayList<String> keys = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ManageRSAKeys.this, android.R.layout.simple_list_item_1, keys);
        listView = findViewById(R.id.RSAKeys);
        listView.setAdapter(arrayAdapter);
        while(keysIterator.hasNext()){
            arrayAdapter.add(keysIterator.next());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rsakeys);

        addButton = findViewById(R.id.addKey);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageRSAKeys.this, AddKey.class);
                intent.putExtra("jsonData",jsonData.toString());
                startActivity(intent);
            }
        });

        listView = findViewById(R.id.RSAKeys);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String keyName = ((TextView) view).getText().toString();
                Intent intent = new Intent(ManageRSAKeys.this, CheckRSAKey.class);
                intent.putExtra("keyName",keyName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fresh();
    }
}