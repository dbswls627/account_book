package com.team_3.accountbook;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_SMS = 100;
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");

    AppDatabase db;
    ArrayList<Cost> arrayList = new ArrayList<>();
    RecyclerView mRecyclerView;
    Context context;

    @Override
    protected void onRestart() {
        arrayList.clear();
        super.onRestart();
        readSMSMessage();

        ArrayList<String> dateArray = new ArrayList<>();        // 중복 제거한 날짜(yyyy년 MM월 dd일)만 담는 리스트 (adapter2로 넘겨주기 위함)
        for (Cost cost : arrayList) {
            if(!dateArray.contains(cost.getUseDate().substring(0, 14))) {
                dateArray.add(cost.getUseDate().substring(0, 14));
            }
        }

        mRecyclerView = (RecyclerView)findViewById(R.id.rv);
        mRecyclerView.setAdapter(new adapter2(context, arrayList, dateArray));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);
        callPermission();
        readSMSMessage();

        ArrayList<String> dateArray = new ArrayList<>();        // 중복 제거한 날짜(yyyy년 MM월 dd일)만 담는 리스트 (adapter2로 넘겨주기 위함)
        for (Cost cost : arrayList) {
            if(!dateArray.contains(cost.getUseDate().substring(0, 14))) {
                dateArray.add(cost.getUseDate().substring(0, 14));
            }
        }

        mRecyclerView = (RecyclerView)findViewById(R.id.rv);
        mRecyclerView.setAdapter(new adapter2(context, arrayList, dateArray));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }



    public void readSMSMessage() {
        Uri allMessage = Uri.parse("content://sms");   // 문자 접근
        Uri rcs = Uri.parse("content://im/chat");     // RCS 접근
        String where = "address = 15881688";

        ContentResolver cr = getContentResolver();

        Cursor c = cr.query(allMessage,              // .query(from / select / ? / where / order by);
                new String[]{"body", "date"},
                where, null,
                "date DESC");
        Cursor c2 = cr.query(rcs,              //RCS
                new String[]{"body", "date"},
                where, null,
                "date DESC");

        Date timeInDate;

        /*
        JSON: Key-Value 의 집합으로 중괄호'{ }'를 사용. 각 쌍들은 쉼표(,)로 구분된다.
              ex) {"name":"abc", "age":20, "phone":"010-1234-5678"}
              또한, 대괄호'[ ]'로 표현되는 배열을 제공하며, 배열의 각 요소는 기본 자료형/객체/배열이 될 수 있다.
        */
        while (c2.moveToNext()) {               // RCS
            String body = c2.getString(0);      // rcs 의 body
            try{
                JSONObject jObject = new JSONObject(body);      // body 전체를 담음
                JSONArray jArray;

                jObject = jObject.getJSONObject("layout");          // layout 키의 값을 담음
                jArray  = jObject.getJSONArray("children");         // children 키의 값을 감음(배열[] 형태이기 때문에 jSONArray 에 담음)
                jObject = (JSONObject) jArray.get(1);               // 배열의 두번째 값을 담음
                jArray  = jObject.getJSONArray("children");         // children 키의 값을 감음(배열[] 형태이기 때문에 jSONArray 에 담음)
                jObject = (JSONObject) jArray.get(0);               // 배열의 첫번째 값을 담음

                body = String.valueOf(jObject.get("text"));         // text 키의 값(문자 내용)을 가져옴.
                Log.d("test", body);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        while (c.moveToNext()) {
            String body = c.getString(0);
            long timestamp = c.getLong(1);

            if (!db.dao().getMs().contains(timestamp)){     // ms 값이 겹치지 않는 값들만 실행
                timeInDate = new Date(timestamp);
                String date = sdf.format(timeInDate);
                Cost cost = parsing(body, date, timestamp);

                if (cost.getAmount()!=-1 && cost.getContent()!="") { // 정규화되지 않았으면 리스트에 추가하지 않음
                    arrayList.add(cost);    // 리턴 받은 값 바로 리스트에 저장
                }
            }

        }


    }



    private Cost parsing(String body, String date, long timestamp){
        String amount;              // 추출한 가격(~원)
        String place;               // 추출한 사용처
        int int_amount;             // int 형으로 변환한 가격(only 숫자)

        Pattern p = Pattern.compile("([0-9])\\S*(원)");  // 원 앞에 있는 숫자들과 원을 파싱 적어도 앞에 숫자하나가 있어야함
        Matcher m;                  // 패턴 p와 matching 되는 문자들을 저장할 Matcher 클래스 객체 m 생성
        m = p.matcher(body);        // 정규식으로 가격(~원)을 파싱 후 매칭되는 문자들을 Matcher 객체에 저장

        if(m.find()){ amount = m.group(); }               // 매칭 될 문자가 1개 뿐이라 while()말고 if()를 사용함.
        else{ amount = null; }                            // 매칭되는 문자가 없으면 null

        p = Pattern.compile("(.*사용)");                  // p객체 재활용 *** 사용 까지 parsing
        m = p.matcher(body);                             // m객체 재활용

        if(m.find()){ place = m.group(); }               // 매칭 될 문자가 1개 뿐이라 while()말고 if()를 사용함.
        else{ place = ""; }                              // 매칭되는 문자가 없으면 null
        try {                                            // null 값을 받으면 에러가 나서 예외처리 사용
            place=place.replaceAll("사용$", "");          // 정규식으로 끝에 있는 사용만 제거
        }
        catch (Exception e){place = "";}

        try {           // null 값을 받는 상황을 위해 예외처리 사용
            int_amount = Integer.parseInt(amount.replaceAll("[,]|[원]", ""));   // '~원' 형식으로 추출된 가격을 정수형으로 2차 가공 및 반환
        }
        catch (Exception e){int_amount = -1;}

        return new Cost(0, int_amount, place, date, 0, "", "", "", timestamp);
        //return new Cost(date, place, int_amount, timestamp, 0);        // item 형태의 객체 return
    }



    private void callPermission() {       // 문자 권한 얻기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.READ_SMS},
                    PERMISSIONS_REQUEST_READ_SMS);
        } else {
            // 해당 로직으로 이동
        }
        String[] permissions = {Manifest.permission.RECEIVE_SMS};
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

}
