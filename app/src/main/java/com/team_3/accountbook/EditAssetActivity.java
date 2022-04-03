package com.team_3.accountbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class EditAssetActivity extends AppCompatActivity implements AssetInAdapter.OnItemClickInAssetAc{
    private BottomNavigationView mBottom_menu;
    private Intent intent;
    private RecyclerView mAssetRV;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_asset);

        mAssetRV = findViewById(R.id.rv_Asset);
        db = AppDatabase.getInstance(this);

        settingBottomMenu();
        setRV();
    }



    private void setRV(){
        List<String> assetNameList = db.dao().getAssetName();

        mAssetRV.setAdapter(new AssetInAdapter(assetNameList, this, this));
        mAssetRV.setLayoutManager(new LinearLayoutManager(this));
    }



    private void settingBottomMenu(){
        mBottom_menu = findViewById(R.id.bottom_menu);
        mBottom_menu.setSelectedItemId(R.id.setting);
        mBottom_menu.setOnNavigationItemSelectedListener((@NonNull MenuItem menuItem)-> {

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



    @SuppressWarnings("deprecation")
    @SuppressLint("NonConstantResourceId")
    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.toBack_editAsset:
                finish();
                overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)

                break;

            case R.id.addAsset:
                Intent intent = new Intent(this, EditItemNameActivity.class);
                intent.putExtra("flag", "new_assetName");
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.bottom_in_activity, R.anim.hold_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)

                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                setRV();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void listItemClick(String assetName) {
        Intent intent = new Intent(this, EditItemNameActivity.class);
        intent.putExtra("itemName", assetName);
        intent.putExtra("flag", "modify_assetName");
        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.left_in_activity, R.anim.hold_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
    }



    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();

        overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
    }



}