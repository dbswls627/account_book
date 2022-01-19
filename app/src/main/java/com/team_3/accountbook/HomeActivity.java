package com.team_3.accountbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setOnNavigationItemSelectedListener((@NonNull MenuItem menuItem)-> {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        break;
                    case R.id.graph:
                        break;
                    case R.id.assets:
                        break;
                    case R.id.setting:
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
        });
    }
}