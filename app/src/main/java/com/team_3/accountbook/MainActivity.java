package com.team_3.accountbook;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_SMS = 100;
    public Context context = this;
    private TextView mTv_Months;
    private LinearLayout mLayoutNoData;
    private AppDatabase db;
    private ArrayList<Cost> arrayList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private int months = 1;

    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onRestart() {
        super.onRestart();
        setMessageList();
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTv_Months = findViewById(R.id.tv_months);
        mLayoutNoData = findViewById(R.id.layout_noData_main);
        mRecyclerView = findViewById(R.id.rv);
        db = AppDatabase.getInstance(this);

        mLayoutNoData.setVisibility(View.GONE);
        callPermission();

        Intent intent = getIntent();
        months = intent.getIntExtra("months", -1);

        setMessageList();


    }



    private void setMessageList(){
        mTv_Months.setText(String.valueOf(months));

        arrayList.clear();
        readSMSMessage(this, db);

        if(arrayList.isEmpty()){
            mLayoutNoData.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
        else{
            mLayoutNoData.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

            ArrayList<String> dateArray = new ArrayList<>();        // 중복 제거한 날짜(yyyy년 MM월 dd일)만 담는 리스트 (adapter2로 넘겨주기 위함)
            for (Cost cost : arrayList) {
                if(!dateArray.contains(cost.getUseDate().substring(0, 14))) {
                    dateArray.add(cost.getUseDate().substring(0, 14));
                }
            }

            mRecyclerView = findViewById(R.id.rv);
            mRecyclerView.setAdapter(new adapter2(this, arrayList, dateArray));
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void readSMSMessage(Context context, AppDatabase db) {
        Uri smsUri = Uri.parse("content://sms");        // 문자 접근
        Uri rcsUri = Uri.parse("content://im/chat");    // RCS 접근

        ZoneId zoneid = ZoneId.of("Asia/Seoul");                                                            // 서울 시각
        long beforeM = LocalDateTime.now().minusMonths(months).atZone(zoneid).toInstant().toEpochMilli();   // months 개월 전 ms
        String where = "date >"+beforeM;
        //날짜 조건 추가
        ContentResolver cr = context.getContentResolver();

        Cursor smsCur = cr.query(smsUri,              // .query(from / select / ? / where / order by);
                new String[]{"body", "date", "address"},
                where, null,
                "date DESC");
        Cursor rcsCur = cr.query(rcsUri,              // RCS
                new String[]{"body", "date", "address"},
                where, null,
                "date DESC");

        Date timeInDate;

        /*
        JSON: Key-Value 의 집합으로 중괄호'{ }'를 사용. 각 쌍들은 쉼표(,)로 구분된다.
              ex) {"name":"abc", "age":20, "phone":"010-1234-5678"}
              또한, 대괄호'[ ]'로 표현되는 배열을 제공하며, 배열의 각 요소는 기본 자료형/객체/배열이 될 수 있다.
        */

        if(rcsCur != null){     // RCS 가 하나도 없으면 팅김.(Null Point Exception)
            while (rcsCur.moveToNext()) {               // RCS
                String body = rcsCur.getString(0);      // rcs 의 body
                long timestamp = rcsCur.getLong(1);     // rcs 의 date(ms)
                String address = rcsCur.getString(2);

                try{
                    JSONObject jObject = new JSONObject(body);      // body 전체를 담음
                    JSONArray jArray;
                    jObject = jObject.getJSONObject("layout");          // layout 키의 값을 담음
                    jArray  = jObject.getJSONArray("children");         // children 키의 값을 감음(배열[] 형태이기 때문에 jSONArray 에 담음)
                    jObject = (JSONObject) jArray.get(1);               // 배열의 두번째 값을 담음
                    jArray  = jObject.getJSONArray("children");         // children 키의 값을 감음(배열[] 형태이기 때문에 jSONArray 에 담음)
                    jObject = (JSONObject) jArray.get(0);               // 배열의 첫번째 값을 담음

                    body = String.valueOf(jObject.get("text"));         // text 키의 값(문자 내용)을 가져옴.
                    body = body.replaceAll("\n", " ");

                    if (!db.dao().getMs().contains(timestamp)){     // ms 값이 겹치지 않는 값들만 실행
                        timeInDate = new Date(timestamp);
                        String date = sdf.format(timeInDate);
                        Cost cost = parsing(body, date, timestamp);
                        cost.setDivision(body);   // MainActivity 에서 문자 내용을 표시하기 위해 사용하지 않는 division 에 문자 내용을 set 해서 전달함.

                        addToList(cost, address, body);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if(smsCur != null){     // SMS 가 하나도 없으면 팅김.(Null Point Exception)
            while (smsCur.moveToNext()) {
                String body = smsCur.getString(0);
                long timestamp = smsCur.getLong(1);
                String address = smsCur.getString(2);
                body = body.replaceAll("\n", " ");

                if (!db.dao().getMs().contains(timestamp)){     // ms 값이 겹치지 않는 값들만 실행

                    timeInDate = new Date(timestamp);
                    String date = sdf.format(timeInDate);
                    Cost cost = parsing(body, date, timestamp);
                    cost.setDivision(body.replaceAll("\n", " "));   // MainActivity 에서 문자 내용을 표시하기 위해 사용하지 않는 division 에 문자 내용을 set 해서 전달함.

                    addToList(cost, address, body);
                }

            }
        }

        Collections.sort(arrayList, new Comparator<Cost>() {        // arrayList 날짜순으로 정렬
            @Override
            public int compare(Cost c1, Cost c2) {
                return c2.getUseDate().compareTo(c1.getUseDate());
            }
        });



    }

    public Long getMs(){
        return arrayList.get(0).getMs();
    }



    public Cost parsing(String body, String date, long timestamp){
        String amount;              // 추출한 가격(~원)
        String place;               // 추출한 사용처
        int int_amount;             // int 형으로 변환한 가격(only 숫자)
        String flagDate;

        Pattern p = Pattern.compile("[0-9|,]+(원)");  // 원 앞에 있는 숫자들과 원을 파싱 적어도 앞에 숫자하나가 있어야함
        Matcher m;                  // 패턴 p와 matching 되는 문자들을 저장할 Matcher 클래스 객체 m 생성
        m = p.matcher(body);        // 정규식으로 가격(~원)을 파싱 후 매칭되는 문자들을 Matcher 객체에 저장
        if(m.find()){ amount = m.group(); }               // 매칭 될 문자가 1개 뿐이라 while()말고 if()를 사용함.
        else{ amount = null; }                            // 매칭되는 문자가 없으면 null
        try {                                             // null 값을 받는 상황을 위해 예외처리 사용
            int_amount = Integer.parseInt(amount.replaceAll(",|원", ""));   // '~원' 형식으로 추출된 가격을 정수형으로 2차 가공 및 반환
        }
        catch (Exception e){int_amount = -1;}

        p = Pattern.compile("([,|0-9]+원 )+[\\S| ]+( 사용)");
        m = p.matcher(body);                             // m객체 재활용
        if(m.find()){ place = m.group(); }
        else{ place = ""; }
        try {
            place = place.replaceAll("[,|0-9]+원 | 사용", "");       // 정규식으로 "-,---원 "&" 사용"제거
        }
        catch (Exception e){place = "";}

        if(place.equals("")){       // 사용처가 "--- 사용" 형식이 아닌 경우
            p = Pattern.compile("([0-9]{2}:[0-9]{2} )+[^0-9 ]+");
            m = p.matcher(body);
            if(m.find()){ place = m.group(); }
            else{ place = ""; }
            try {                                                          // null 값을 받으면 에러가 나서 예외처리 사용
                place = place.replaceAll("[0-9]{2}:[0-9]{2} ", "");        // "hh:mm "제거
            }
            catch (Exception e){place = "";}

            if(place.equals("주식회사")){   // 사용처가 "주식회사 협성대"인 경우가 있음. 그 때를 위함
                p = Pattern.compile("(주식회사 )+\\S+");
                m = p.matcher(body);
                if(m.find()){ place = m.group(); }
                else{ place = ""; }
                try {                                                 // null 값을 받으면 에러가 나서 예외처리 사용
                    place = place.replaceAll("(주식회사 )", "");        // "주식회사 " 제거
                }
                catch (Exception e){place = "";}
            }
        }


        p = Pattern.compile("[0-9]{2}[/][0-9]{2} [0-9]{2}:[0-9]{2}");                  // 결제 문자 형식을 구분하기 위한 정규식   ex) MM/dd hh:mm
        m = p.matcher(body);
        if(m.find()){ flagDate = m.group(); }
        else{ flagDate = ""; }      // 매칭되는 문자가 없으면 ""


        // 결제 문자 형식 구분을 위한 flagDate 를 Cost 테이블의 sortName 에 넣어서 리턴함.
        return new Cost(0, int_amount, place, date, 0, flagDate, "", "", timestamp);
    }



    public String matchPhoneNumber(String add, String body){
        /*  ↓↓어째서인지 smsReceiver.java 에서 실행하면 db.dao()에서 NullPointerException 이 발생함. 메소드 내부에서 생성해 사용하는 방법으로 해결  */
        AppDatabase db = AppDatabase.getInstance(this);
        List<String> wayNameList = db.dao().getWayName(add);
        String wayName = "";

        if(wayNameList.size() == 1){            // 번호가 1개(동일한 등록 번호 없음.)
            wayName = wayNameList.get(0);
        }

        else if(wayNameList.size() > 1){        // 동일 번호가 2개 이상.
            List<String> delimiter = db.dao().getWayDelimiter(add);

            for (int i = 0; i < delimiter.size(); i++) {
                if(!delimiter.get(i).equals("")){

                    if(body.contains(delimiter.get(i))){       // 구분어와 일치하는 문자는 구분어를 저장한 wayName 을 저장
                        wayName = db.dao().getWayNameDetail(add, delimiter.get(i));
                    }

                }
            }

        }

        return wayName;
    }



    private void addToList(Cost cost, String address, String body){
        if (!cost.getSortName().equals("") && cost.getAmount()!=-1 && !cost.getContent().equals("")) {  // 결제문자면서 정규화되지 않았으면 리스트에 추가하지 않음
            cost.setSortName(matchPhoneNumber(address, body));      // 결제 문자 구분을 위해 들어있던 sortName 대신 번호에 따른 wayName 을 set
            if(cost.getSortName().equals("")){
                cost.setSortName(address+"!#@!");                   // 결제 문자가 아니면, 결제 문자가 아님을 구분하기 위에 (번호+"!#@!")로 set
            }

            arrayList.add(cost);    // 리턴 받은 값 바로 리스트에 저장
        }
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



    @SuppressLint("NonConstantResourceId")
    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.toBack_main:
                finish();
                overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);     // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)

                break;

            case R.id.periodCorrection:
                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_movetomain);

                showDialog(dialog);

                break;

        }

    }



    @SuppressLint("SetTextI18n")
    public void showDialog(Dialog dialog){
        dialog.show();

        TextView mNoBring, mBring;
        EditText mMonths;

        mNoBring = dialog.findViewById(R.id.tv_noBring);
        mBring = dialog.findViewById(R.id.tv_bring);
        mMonths = dialog.findViewById(R.id.months);
        mMonths.setText(months+"");

        mMonths.setOnEditorActionListener((textView,i, keyEvent)->{
            months = Integer.parseInt(mMonths.getText().toString());
            setMessageList();

            dialog.dismiss();
            return false;
        });

        mNoBring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mBring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    months = Integer.parseInt(mMonths.getText().toString());
                    setMessageList();

                    dialog.dismiss();
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.hold_activity, R.anim.left_out_activity);     // (나타날 액티비티가 취해야할 애니메이션, 현재 액티비티가 취해야할 애니메이션)
    }
}
