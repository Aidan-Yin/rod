package com.rod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.button);
        EditText hostEditText = findViewById(R.id.editText);
        EditText portEditText = findViewById(R.id.editTextNumber);
        EditText portEditText2 = findViewById(R.id.editTextNumber2);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String host = hostEditText.getText().toString();
                int port1;
                int port2;
                try {
                    port1 = Integer.parseInt(portEditText.getText().toString());
                }catch (NumberFormatException e) {
                    port1 = 8080;
                }
                try {
                    port2 = Integer.parseInt(portEditText2.getText().toString());
                }catch (NumberFormatException e) {
                    port2 = 8081;
                }
                Intent intent = new Intent(MainActivity.this,ControlPanel.class);
                intent.putExtra("host",host);
                intent.putExtra("port1",port1);
                intent.putExtra("port2",port2);
                startActivity(intent);
            }
        });
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