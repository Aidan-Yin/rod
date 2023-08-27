package com.rod;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CheckRSAKey extends AppCompatActivity {
    String keyName = "";
    private String readPublicKey() throws IOException, JSONException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput("RSAKeys")));
        String data = bufferedReader.readLine();
        JSONObject jsonData = new JSONObject(data);
        String keys = jsonData.getString(keyName);
        return keys.split(",")[0];
    }
    private void deleteKey() throws IOException, JSONException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput("RSAKeys")));
        String data = bufferedReader.readLine();
        bufferedReader.close();
        JSONObject jsonData = new JSONObject(data);
        jsonData.remove(keyName);
        FileOutputStream fileOutputStream = openFileOutput("RSAKeys", Context.MODE_PRIVATE);
        fileOutputStream.write(jsonData.toString().getBytes(StandardCharsets.UTF_8));
        fileOutputStream.close();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_rsakey);
        Intent intent = getIntent();
        keyName = intent.getStringExtra("keyName");
        Button showKeyButton = findViewById(R.id.showKey);
        Button deleteKeyButton = findViewById(R.id.deleteKey);
        ImageButton copyButton = findViewById(R.id.copy);
        TextView textView = findViewById(R.id.textView);
        showKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String publicKey = "";
                try {
                    publicKey = readPublicKey();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                textView.setText(publicKey);
                copyButton.setVisibility(View.VISIBLE);
            }
        });

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String publicKey = textView.getText().toString();
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Label", publicKey);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(CheckRSAKey.this, getApplication().getString(R.string.copied), Toast.LENGTH_SHORT).show();
            }
        });

        deleteKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckRSAKey.this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(getApplication().getString(R.string.deleteRemindTitle))
                        .setMessage(getApplication().getString(R.string.deleteRemindMessage))
                        .setPositiveButton(getApplication().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    deleteKey();
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                                finish();
                            }
                        })
                        .setNegativeButton(getApplication().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ;
                            }
                        });
                builder.create().show();
            }
        });
    }
}