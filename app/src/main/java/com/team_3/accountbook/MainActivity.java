package com.team_3.accountbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_SMS = 100;
    ArrayList<String> arrayList=new ArrayList<>();
    RecyclerView mRecyclerView;
    TextView t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callPermission();
        readSMSMessage();
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        mRecyclerView.setAdapter(new adapter(arrayList));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    public int readSMSMessage() {
        Uri allMessage = Uri.parse("content://sms");   //문자 접근
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(allMessage,
                new String[]{"address","date","body"},  //new String[]{"_id", "thread_id", "address", "person", "date", "body"},
                null, null,
                "date DESC");

        while (c.moveToNext()) {


            if(c.getString(0).equals("15881688")) {
                long timestamp = c.getLong(1);     ///숫자 이상함 막 60으로 나누고 어쩌고 해야할듯
                String body = c.getString(2);      //문자에 날짜 나오는데 연도가 안나옴
                arrayList.add(body);
            }
        }
        return 0;
    }
    private void callPermission() {        //문자 권한 얻기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.READ_SMS},
                    PERMISSIONS_REQUEST_READ_SMS);
        } else {
            // 해당 로직으로 이동
        }
    }
}
