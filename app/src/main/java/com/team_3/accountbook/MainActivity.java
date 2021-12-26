package com.team_3.accountbook;

import androidx.appcompat.app.AppCompatActivity;

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
    ArrayList<Message> arrayList=new ArrayList<>();
    TextView t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callPermission();
        readSMSMessage();
    }
    public int readSMSMessage() {
        t= findViewById(R.id.t);
        Uri allMessage = Uri.parse("content://sms");   //문자 접근
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(allMessage,
                new String[]{"address","date","body"},  //new String[]{"_id", "thread_id", "address", "person", "date", "body"},
                null, null,
                "date DESC");

        while (c.moveToNext()) {
            Message msg = new Message(); // 따로 저는 클래스를 만들어서 담아오도록 했습니다.
            String address = c.getString(0);
            msg.address=String.valueOf(address);
            if(address.equals("15881688")) {
                long timestamp = c.getLong(1);
                msg.timestamp = String.valueOf(timestamp);
                String body = c.getString(2);
                msg.body = String.valueOf(body);
                arrayList.add(msg); //
                t.setText(body);
                break;
            }
        }
        return 0;
    }
    public class Message {
        public String address; //휴대폰번호
        public String timestamp; //시간
        public String body; //문자내용
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
