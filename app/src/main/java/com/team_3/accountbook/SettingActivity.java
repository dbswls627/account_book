package com.team_3.accountbook;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingActivity extends AppCompatActivity {
    ImageView mPermissionImage;
    TextView add , list, sqlTest, wear, amountGoal_text, mPermissionText;
    Intent intent;
    AppDatabase db;
    BottomNavigationView bottom_menu;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onRestart() {
        super.onRestart();
        setPermissionSettingView();
    }

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
        mPermissionImage = findViewById(R.id.permissionImage_setting);
        mPermissionText = findViewById(R.id.permissionText_setting);


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


        setPermissionSettingView();

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



    private void showDialogToListProvisionSetting(Dialog dialog){
        dialog.show();

        LinearLayout mAsset, mWay, mSort;
        TextView mCancel, mAccept;

        mAsset = dialog.findViewById(R.id.assetProvision);
        mWay = dialog.findViewById(R.id.wayProvision);
        mSort = dialog.findViewById(R.id.sortProvision);
        mCancel = dialog.findViewById(R.id.cancel_provisionSetting);
        mAccept = dialog.findViewById(R.id.accept_provisionSetting);


        mAsset.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                mAsset.setSelected(true);
                mAsset.setBackgroundColor(getColor(R.color.lightGreen));
                mWay.setSelected(false);
                mWay.setBackgroundColor(getColor(R.color.white));
                mSort.setSelected(false);
                mSort.setBackgroundColor(getColor(R.color.white));
            }
        });
        mWay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                mAsset.setSelected(false);
                mAsset.setBackgroundColor(getColor(R.color.white));
                mWay.setSelected(true);
                mWay.setBackgroundColor(getColor(R.color.lightGreen));
                mSort.setSelected(false);
                mSort.setBackgroundColor(getColor(R.color.white));
            }
        });
        mSort.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                mAsset.setSelected(false);
                mAsset.setBackgroundColor(getColor(R.color.white));
                mWay.setSelected(false);
                mWay.setBackgroundColor(getColor(R.color.white));
                mSort.setSelected(true);
                mSort.setBackgroundColor(getColor(R.color.lightGreen));
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mAccept.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                if(mAsset.isSelected()){
                    intent = new Intent(getApplicationContext(), EditAssetOrSortActivity.class);
                    intent.putExtra("forWhat", "asset");
                    startActivity(intent);
                    dialog.dismiss();
                    overridePendingTransition(R.anim.left_in_activity, R.anim.hold_activity);     // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
                }
                else if(mWay.isSelected()){
                    intent = new Intent(getApplicationContext(), AssetForEditActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                    overridePendingTransition(R.anim.left_in_activity, R.anim.hold_activity);     // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
                }
                else if(mSort.isSelected()){
                    Toast.makeText(getApplicationContext(), "sort", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "하나를 선택하세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.listSetting:
                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_listprovisionsetting);

                showDialogToListProvisionSetting(dialog);

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

            case R.id.test:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED){
                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                }

                break;
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setPermissionSettingView() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED){
            mPermissionImage.setColorFilter(getColor(R.color.gray));
            mPermissionText.setTextColor(getColor(R.color.gray));
        }
    }


}