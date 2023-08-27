package com.rod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class AddKey extends AppCompatActivity {

    private JSONObject jsonData;
    private void writeData(String key, String value) throws JSONException, IOException {
        jsonData.put(key, value);
        FileOutputStream fileOutputStream = this.openFileOutput("RSAKeys", Context.MODE_PRIVATE);
        fileOutputStream.write(jsonData.toString().getBytes(StandardCharsets.UTF_8));
        fileOutputStream.close();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_key);
        Button confirm = findViewById(R.id.toAddKey);
        Intent intent = getIntent();
        try {
            jsonData = new JSONObject(Objects.requireNonNull(intent.getStringExtra("jsonData")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText keyName = findViewById(R.id.keyName);
                String name = keyName.getText().toString();
                if (name.isEmpty()){
                    Context context = getApplicationContext();
                    Toast.makeText(context,context.getString(R.string.emptyNameRemind),Toast.LENGTH_SHORT).show();
                } else if (name.contains(",")) {
                    Context context = getApplicationContext();
                    Toast.makeText(context,context.getString(R.string.theNameOfTheKey)+context.getString(R.string.noCommaRemind),Toast.LENGTH_SHORT).show();
                } else{
                    RSA rsa = new RSA(4096);
                    try {
                        writeData(name, rsa.getBase64PublicKey()+","+rsa.getBase64PrivateKey());
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            }
        });
    }
}