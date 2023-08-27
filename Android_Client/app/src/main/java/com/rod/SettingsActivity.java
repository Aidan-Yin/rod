package com.rod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ListView listview = findViewById(R.id.SettingList);

        String ManageRSAKeys = this.getString(R.string.ManageRSAKeys);
        String ManageConfigurations = this.getString(R.string.ManageConfigurations);

        String[] options = {ManageRSAKeys,ManageConfigurations};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(SettingsActivity.this, android.R.layout.simple_list_item_1,options);
        listview.setAdapter(arrayAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (((TextView) view).getText().toString().equals(ManageRSAKeys)){
                    Intent intent0 = new Intent(SettingsActivity.this, com.rod.ManageRSAKeys.class);
                    startActivity(intent0);
                }else if(((TextView) view).getText().toString().equals(ManageConfigurations)){
                    Intent intent1 = new Intent(SettingsActivity.this, ManageConfigurations.class);
                    startActivity(intent1);
                }
            }
        });
    }
}