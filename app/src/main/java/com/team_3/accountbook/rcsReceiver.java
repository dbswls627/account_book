package com.team_3.accountbook;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class rcsReceiver extends BroadcastReceiver {
    Cost cost;
    long timestamp;     // rcs 의 date(ms)
    String address;
    String body;      // rcs 의 body
    private NotificationCompat.Builder notifyBuilder;
    private final MainActivity ma = new MainActivity();
    private static final int NOTIFICATION_ID = 1;           // Notification 에 대한 ID
    private static final String CHANNEL_ID = "2";           // Channel 에 대한 id 생성: Channel 을 구부하기 위한 ID.
    private NotificationManager mNotificationManager;       // Channel 을 생성 및 전달해 줄 수 있는 Manager
    @Override
    public void onReceive(Context context, Intent intent) {
        Uri rcsUri = Uri.parse("content://im/chat");    // RCS 접근
        long beforeM = LocalDateTime.now().minusMonths(1).atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
        String where = "date >"+beforeM;
        ContentResolver cr = context.getContentResolver();
        Cursor rcsCur = cr.query(rcsUri,              // RCS
                new String[]{"body", "date", "address"},
                where, null,
                "date DESC");

        Date timeInDate;
        AppDatabase db = AppDatabase.getInstance(context);
        /** 최근 RCS 문자 하나만 가져와서 Cost 에 넣음*/
        if(rcsCur != null){     // RCS 가 하나도 없으면 팅김.(Null Point Exception)
            rcsCur.moveToFirst();                          //첫번째 RCS 문자만
            body = rcsCur.getString(0);
            timestamp = rcsCur.getLong(1);
            address = rcsCur.getString(2);
            Log.d("RCS",body);
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

                timeInDate = new Date(timestamp);
                String date = ma.sdf.format(timeInDate);

                cost = ma.parsing(body, date, timestamp);
                cost.setDivision(body);
                Log.d("RCS",cost.getContent()+cost.getAmount());

                if (!cost.getSortName().equals("") && cost.getAmount()!=-1 && !cost.getContent().equals("")){ // 가져온 가장 최근 RCS 문자가 결제 문자면
                    Log.d("RCS","결제문자임");

                    if(db.dao().getAutoState()){        // ★자동저장 기능이 "ON"일 때 실행★

                        if (!cost.getSortName().equals("") && cost.getAmount()!=-1 && !cost.getContent().equals("")) {  // 결제문자면서 정규화되지 않았으면 리스트에 추가하지 않음
                            cost.setSortName(ma.matchPhoneNumber(address, body));      // 결제 문자 구분을 위해 들어있던 sortName 대신 번호에 따른 wayName 을 set

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    String wayName = checkWayName(cost.getSortName());
                                    if(!wayName.equals("(Auto)")){
                                        db.dao().updateWayNotiState(wayName, true);
                                    }

                                    List<Cost> preData_today = db.dao().getNowPre(cost.getUseDate(), cost.getContent(), wayName);
                                    List<Cost> afterData_today = db.dao().getNowAfter(cost.getUseDate(), cost.getContent(), wayName);
                                    List<Cost> preData = db.dao().getCostDataPre(cost.getUseDate(), wayName);
                                    List<Cost> afterData = db.dao().getCostDataAfter(cost.getUseDate(), wayName);

                                    preData_today.addAll(preData);
                                    afterData_today.addAll(afterData);

                                    int preCostId = -100, afterCostId = -100;

                                    try { preCostId = preData_today.get(0).getCostId(); }
                                    catch (Exception ignored) { }
                                    try { afterCostId = afterData_today.get(0).getCostId(); }
                                    catch (Exception ignored) { }

                                    AddActivity addAc = new AddActivity();
                                    addAc.updateBalanceOnByNewData(afterData_today, preCostId, afterCostId,
                                            cost.getUseDate(), wayName, "(미분류)", cost.getAmount(), cost.getContent(), "expense",cost.getMs(), "new", true);

                                }
                            }, 1000);   // 1초 후 자동저장이 실행됨.(SMS 를 다 읽기 전에(?) ms 값을 가져와서, 올바른 ms 값을 못가져옴. 딜레이를 줌으로 해결함)

                        }
                    }



                    mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //Channel 정의 생성자( construct 이용 )
                        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Test Notification", mNotificationManager.IMPORTANCE_HIGH);
                        //Channel 에 대한 기본 설정
                        notificationChannel.enableLights(true);
                        notificationChannel.enableVibration(true);
                        notificationChannel.setDescription("Notification from Mascot");
                        // Manager 을 이용하여 Channel 생성
                        mNotificationManager.createNotificationChannel(notificationChannel);

                        Intent notiIntent;
                        if(db.dao().getAutoState()){    // Auto-Save ON
                            notiIntent = new Intent(context, HomeActivity.class);       // 알림 클릭시 HomeActivity 로 이동하게 설정

                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                            stackBuilder.addParentStack(MainActivity.class);
                            stackBuilder.addNextIntent(notiIntent);

                            PendingIntent notiPendingIntent = stackBuilder.getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);     // Notification 에선 PendingIntent 라는걸 써야한다고 함

                            notifyBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setContentTitle("자동 저장 알림")                     // 알림 제목
                                    .setContentText("자동 저장된 결제 정보를 확인하세요.")    // 알림 내용
                                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                    .setSmallIcon(R.drawable.ic_launcher_foreground)    // 알림 아이콘
                                    .setContentIntent(notiPendingIntent)                // 알림 클릭시 실행할 액티비티 인텐트
                                    .setAutoCancel(true);                               // notification 을 탭 했을경우 notification 을 없앤다.
                        }
                        else{           // Auto-Save OFF
                            notiIntent = new Intent(context, MainActivity.class);       // 알림 클릭시 MainActivity 로 이동하게 설정
                            notiIntent.putExtra("months", 1);

                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                            stackBuilder.addParentStack(MainActivity.class);
                            stackBuilder.addNextIntent(notiIntent);

                            PendingIntent notiPendingIntent = stackBuilder.getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);     // Notification 에선 PendingIntent 라는걸 써야한다고 함

                            notifyBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setContentTitle("새로운 결제 알림")                   // 알림 제목
                                    .setContentText("결제 정보를 등록하세요.")              // 알림 내용
                                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                    .setSmallIcon(R.drawable.ic_launcher_foreground)    // 알림 아이콘
                                    .setContentIntent(notiPendingIntent)                // 알림 클릭시 실행할 액티비티 인텐트
                                    .setAutoCancel(true);                               // notification 을 탭 했을경우 notification 을 없앤다.
                        }

//                RemoteViews notificationLayout = new RemoteViews("com.team_3.accountbook", R.layout.notification_small);
//                @SuppressLint("RemoteViewLayout") RemoteViews notificationLayoutExpanded = new RemoteViews("com.team_3.accountbook", R.layout.notification_large);
//                Notification notifyBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                        .setSmallIcon(R.drawable.ic_launcher_foreground)    // 알림 아이콘
//                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                        .setCustomContentView(notificationLayout)
//                        .setCustomBigContentView(notificationLayoutExpanded)
//                        .setContentIntent(notiPendingIntent)                // 알림 클릭시 실행할 액티비티 인텐트
//                        .setAutoCancel(true)                                // notification 을 탭 했을경우 notification 을 없앤다.
//                        .build();

                        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());        // 최종적으로 알림 띄우기
                    }

                }

            } catch (JSONException e) {
                Log.d("RCS","NoJSON");
                e.printStackTrace();
            }
        }


        }
    private String checkWayName(String name){
        if(name.equals("")){
            name = "(Auto)";
        }

        return name;
    }
}
