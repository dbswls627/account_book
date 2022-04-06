package com.team_3.accountbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class EditAssetOrSortActivity extends AppCompatActivity implements AssetInAdapter.OnItemClickInAssetAc{
    private BottomNavigationView mBottom_menu;
    private TextView mAssetOrSortSetting;
    private RecyclerView mAssetRV;
    private AppDatabase db;
    private String forWhat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_asset_or_sort);

        mAssetOrSortSetting = findViewById(R.id.assetOrSortSetting);
        mAssetRV = findViewById(R.id.rv_Asset);
        db = AppDatabase.getInstance(this);

        Intent intent = getIntent();
        forWhat = intent.getStringExtra("forWhat");     // 자산설정 or 분류설정 구분자
        if(forWhat.equals("asset")){ mAssetOrSortSetting.setText("자산설정"); }

        settingBottomMenu();
        setRV();
    }



    private void setRV(){
        if(forWhat.equals("asset")){
            List<String> assetNameList = db.dao().getAssetName();

            mAssetRV.setAdapter(new AssetInAdapter(assetNameList, this, this));
            mAssetRV.setLayoutManager(new LinearLayoutManager(this));
        }
    }



    private void settingBottomMenu(){
        mBottom_menu = findViewById(R.id.bottom_menu);
        mBottom_menu.setSelectedItemId(R.id.setting);
        mBottom_menu.setOnNavigationItemSelectedListener((@NonNull MenuItem menuItem)-> {
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
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    private void showDialog(String name){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.round_dialog));

        dialog.show();

        TextView mDeleteMassage, mCancel, mAccept;

        mDeleteMassage = dialog.findViewById(R.id.deleteMassage);
        mCancel = dialog.findViewById(R.id.tv_cancel);
        mAccept = dialog.findViewById(R.id.tv_accept);

        if(forWhat.equals("asset")){ mDeleteMassage.setText("자산에 포함된 수단도 사라집니다."); }

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                db.dao().deleteAsset(name);
                setRV();
            }
        });

    }



    @SuppressWarnings("deprecation")
    @SuppressLint("NonConstantResourceId")
    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.toBack_editAssetOrSort:
                finish();
                overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)

                break;

            case R.id.addItem:
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
    public void listItemClick(String assetName, String doFlag) {
        if(doFlag.equals("click")){
            Intent intent = new Intent(this, EditItemNameActivity.class);
            intent.putExtra("itemName", assetName);
            intent.putExtra("flag", "modify_assetName");
            startActivityForResult(intent, 0);
            overridePendingTransition(R.anim.left_in_activity, R.anim.hold_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
        }
        else if(doFlag.equals("delete")){
            showDialog(assetName);
        }
    }



    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();

        overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);    // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
    }



}