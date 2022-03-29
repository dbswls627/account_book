package com.team_3.accountbook;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingActivity extends AppCompatActivity {
    TextView add,list,sqlTest,wear,amountGoal_text;
    Intent intent;
    AppDatabase db;
    BottomNavigationView bottom_menu;
    @Override
    protected void onStart() {

        super.onStart();
        bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setSelectedItemId(R.id.setting);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        db = AppDatabase.getInstance(this);
        wear = findViewById(R.id.wear);
        add = findViewById(R.id.add);
        list = findViewById(R.id.list);
        sqlTest = findViewById(R.id.sqlTest);
        amountGoal_text = findViewById(R.id.amountGoal_text);


        list.setOnClickListener((view)->{
            intent = new Intent(this, ListActivity.class);
            startActivity(intent);
        });

        sqlTest.setOnClickListener((view)->{
            intent = new Intent(this, SqlTestActivity.class);
            startActivity(intent);
        });
        wear.setOnClickListener((view)->{
            intent = new Intent(this, WearActivity.class);
            startActivity(intent);
        });
        amountGoal_text.setOnClickListener((view)->{
            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_watch_amountgoal);

            showDialogOfAmountGoal(dialog);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDialogOfAmountGoal(Dialog dialog) {
        dialog.show();
        EditText amountGoal;
        TextView cancel,save;
        amountGoal = dialog.findViewById(R.id.amountGoal);
        cancel = dialog.findViewById(R.id.cancel);
        save = dialog.findViewById(R.id.save);

        amountGoal.setText(db.dao().getAmountGoal());

       cancel.setOnClickListener((view)->{
           dialog.dismiss();
       });
        save.setOnClickListener((view)->{
            dialog.dismiss();
            ListenerService LS = new ListenerService();
            LS.bluetooth(SettingActivity.this, amountGoal.getText()+"!");
            db.dao().updateAmountGoal(amountGoal.getText().toString());
        });
    }



    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.listSetting:
                Toast.makeText(this, "자산/수단/분류 설정", Toast.LENGTH_SHORT).show();

                break;

            case R.id.watchSetting:
                Toast.makeText(this, "목표금액/경고% 설정", Toast.LENGTH_SHORT).show();

                break;

            case R.id.autoSaveSetting:
                Toast.makeText(this, "자동저장 ON/OFF 설정", Toast.LENGTH_SHORT).show();

                break;

            case R.id.help:
                Toast.makeText(this, "도움말", Toast.LENGTH_SHORT).show();

                break;
        }
    }


}