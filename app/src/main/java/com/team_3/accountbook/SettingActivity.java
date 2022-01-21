package com.team_3.accountbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingActivity extends AppCompatActivity {
    TextView add,sms;
    Intent intent;
    BottomNavigationView bottom_menu;
    @Override
    protected void onStart() {
        super.onStart();
        bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setSelectedItemId(R.id.setting);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        add=findViewById(R.id.add);
        sms=findViewById(R.id.sms);

        add.setOnClickListener((view)->{
            intent = new Intent(this, AddActivity.class);
            startActivity(intent);
        });
        
        sms.setOnClickListener((view)->{
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        bottom_menu = findViewById(R.id.bottom_menu);

        bottom_menu.setOnNavigationItemSelectedListener((@NonNull MenuItem menuItem)-> {

            switch (menuItem.getItemId()) {
                case R.id.home:
                    intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.graph:
                    intent = new Intent(this, GraphActivity.class);
                    startActivity(intent);
                    break;
                case R.id.assets:
                    intent = new Intent(this, AssetsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.setting:
                    intent = new Intent(this, SettingActivity.class);
                    startActivity(intent);
                    break;
            }
            return true;
        });
    }
}