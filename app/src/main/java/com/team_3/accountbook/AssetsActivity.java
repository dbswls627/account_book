package com.team_3.accountbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class AssetsActivity extends AppCompatActivity implements AssetInAdapter.OnItemClickInAssetAc{
    private List<AssetNameWayNameAndBalance> ANWNList;
    private ArrayList<String> assetNameList = new ArrayList<>();
    private DecimalFormat myFormatter = new DecimalFormat("###,###");
    TextView mTotalMoney;
    RecyclerView mAssetAndWay;
    AppDatabase db;

    BottomNavigationView bottom_menu;

    protected void onStart() {
        super.onStart();
        bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setSelectedItemId(R.id.assets);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets);

        bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setOnNavigationItemSelectedListener((@NonNull MenuItem menuItem)-> {
            Intent intent;
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

        mTotalMoney = findViewById(R.id.totalMoney);
        mAssetAndWay = findViewById(R.id.rv_AssetAndWay);
        db = AppDatabase.getInstance(this);

        buildActivityScreen();
    }



    private void buildActivityScreen(){
        String formatPrice = myFormatter.format(db.dao().getTotalBalance());
        mTotalMoney.setText(formatPrice + "원");

        ANWNList = db.dao().getAnWnWb();
        for (int i = 0; i < ANWNList.size(); i++) {
            if (!assetNameList.contains(ANWNList.get(i).getAssetName())) {
                assetNameList.add(ANWNList.get(i).getAssetName());
            }
        }

        mAssetAndWay.setAdapter(new AssetOutAdapter(ANWNList, assetNameList, this));
        mAssetAndWay.setLayoutManager(new LinearLayoutManager(this));
    }



    @Override
    public void listItemClick(String wayName) {
        Intent intent = new Intent(this, ListInAssetActivity.class);
        intent.putExtra("wayName", wayName);
        Toast.makeText(this, wayName+"(으)로 이동", Toast.LENGTH_SHORT).show();   // 테스트용

        startActivityForResult(intent, 10);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 10){
            if(resultCode == RESULT_OK){
                ANWNList.clear();
                assetNameList.clear();

                buildActivityScreen();
            }
        }
    }
}