package com.team_3.accountbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class AssetsActivity extends AppCompatActivity implements AssetInAdapter.OnItemClickInAssetAc{
    private List<AssetNameWayNameAndBalance> ANWNList;
    private ArrayList<String> assetNameList = new ArrayList<>();
    private DecimalFormat myFormatter = new DecimalFormat("###,###");
    private LinearLayout mAutoSaveZone;
    private ImageView mExistAutoData;
    private TextView mTotalMoney, mAutoBalance;
    private RecyclerView mAssetAndWay;
    private AppDatabase db;

    private BottomNavigationView bottom_menu;

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
        mAutoBalance = findViewById(R.id.autoBalance);
        mAssetAndWay = findViewById(R.id.rv_AssetAndWay);
        mAutoSaveZone = findViewById(R.id.zoneOfAutoSave);
        mExistAutoData = findViewById(R.id.existAutoData_auto);
        db = AppDatabase.getInstance(this);


        buildActivityScreen();
    }



    @SuppressLint("SetTextI18n")
    private void buildActivityScreen(){
        if(db.dao().getAutoState() || (!db.dao().getAutoState() && db.dao().getAutoBalance() != 0)){
            mAutoSaveZone.setVisibility(View.VISIBLE);
        }
        else if(!db.dao().getAutoState()){
            mAutoSaveZone.setVisibility(View.GONE);
        }

        int totalPrice, autoPrice;
        String total = "0";

        totalPrice = db.dao().getTotalBalance();
        autoPrice = db.dao().getAutoBalance();

        try { total = myFormatter.format(totalPrice+autoPrice); }
        catch (Exception ignore){  }
        mTotalMoney.setText(total+"원");

        if(db.dao().getAutoBalance() == 0){
            mAutoBalance.setText("0원");
            mExistAutoData.setVisibility(View.INVISIBLE);
        }
        else{
            mAutoBalance.setText(myFormatter.format(db.dao().getAutoBalance())+"원");
            mExistAutoData.setVisibility(View.VISIBLE);
        }


        // RecyclerView
        ANWNList = db.dao().getAnWnWb();
        for (int i = 0; i < ANWNList.size(); i++) {
            if(!ANWNList.get(i).getAssetName().equals(getResources().getString(R.string.auto_assetName))){      // 자산명 '자동저장' 제거
                if (!assetNameList.contains(ANWNList.get(i).getAssetName())) {
                    assetNameList.add(ANWNList.get(i).getAssetName());
                }
            }
        }

        mAssetAndWay.setAdapter(new AssetOutAdapter(ANWNList, assetNameList, this));
        mAssetAndWay.setLayoutManager(new LinearLayoutManager(this));
    }



    @SuppressLint("NonConstantResourceId")
    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.zoneOfAutoSave:
                listItemClick("(Auto)", "", null, null);

                break;
        }
    }



    @Override
    public void listItemClick(String wayName, String doFlag, ImageView imageView, ImageView imageView_final) {
        Intent intent = new Intent(this, ListInAssetActivity.class);
        intent.putExtra("wayName", wayName);

        startActivityForResult(intent, 10);
        overridePendingTransition(R.anim.left_in_activity, R.anim.hold_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
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