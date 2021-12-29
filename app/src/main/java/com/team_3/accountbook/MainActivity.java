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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_SMS = 100;

    ArrayList<item> arrayList = new ArrayList<>();
    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setAdapter(new adapter(arrayList));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        callPermission();
        readSMSMessage();
    }


    public int readSMSMessage() {
        Uri allMessage = Uri.parse("content://sms");   //문자 접근

        String where = "address = 15881688";

        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(allMessage,    // .query(from / select / ? / where / order by);
                new String[]{"body", "date"},
                where, null,
                "date DESC");

        while (c.moveToNext()) {
            SimpleDateFormat sdf = new SimpleDateFormat("< yyyy년 MM월 dd일 HH:mm >");
            Date timeInDate;

            String body = c.getString(0);      //문자에 날짜 나오는데 연도가 안나옴
            long timestamp = c.getLong(1);

            timeInDate = new Date(timestamp);
            String date = sdf.format(timeInDate);
            arrayList.add(new item(date, body));
        }
        return 0;
    }


    private void callPermission() {        //문자 권한 얻기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.READ_SMS},
                    PERMISSIONS_REQUEST_READ_SMS);
        } else {
            // 해당 로직으로 이동
        }
    }
}
