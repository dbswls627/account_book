package com.team_3.accountbook;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.O)
public class smsReceiver extends BroadcastReceiver {
    public static boolean autoState;

    private static final String CHANNEL_ID = "1";           // Channel 에 대한 id 생성: Channel 을 구부하기 위한 ID.
    private NotificationManager mNotificationManager;       // Channel 을 생성 및 전달해 줄 수 있는 Manager
    private static final int NOTIFICATION_ID = 0;           // Notification 에 대한 ID
    private NotificationCompat.Builder notifyBuilder;               // Notification Builder: Notification 을 생성
    private final MainActivity ma = new MainActivity();

    private String useDate, useAmount;
    private Pattern p;
    private Matcher m;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = parseSms(bundle);    // sms 정보를 담을 객체 선언과 동시에 함수로 정보 저장

        if (messages.length > 0) {
            String sender = messages[0].getOriginatingAddress();
            String body = messages[0].getMessageBody();
            Long millisDate = messages[0].getTimestampMillis();

            if (sender.equals("1111")) {          // ★우선 송신 번호가 '1234' 일때만 토스트가 띄워지도록 함.
                Toast.makeText(context, body, Toast.LENGTH_SHORT).show();
            }

            smsNotification(context, sender, body, millisDate);

//            sendToMainActivity(context, sender, body, millisDate);
        }

    }


    private SmsMessage[] parseSms(Bundle bundle) {
        // PDU: Protocol Data Units. 안드로이드는 SMS 를 PDU 형태로 전달한다.
        Object[] objs = (Object[]) bundle.get("pdus");                  // bundle 로부터 메시지들을 받아온 후,
        SmsMessage[] messages = new SmsMessage[objs.length];            // SmsMessage 배열을 그 크기만큼 만들어 준 뒤,

        for (int i = 0; i < objs.length; i++) {                         // 반복문을 통해 하나씩 PDU 를 SmsMessage 객체로 변환해주어야 하는데
            messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);   // 이는 내장 함수 createFromPdu()를 사용한다.
        }

        return messages;
    }


//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void sendToMainActivity(Context context, String sender, String content, Long millisDate) {
//        Intent intent = new Intent(context, MainActivity.class);   // MainActivity 를 호출할 intent 생성. 우선 형식상으로 정해놨다.
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK   // 실행한 액티비티와 관련된 태스크가 존재하면 동일한 태스크 내에서 실행하고, 그렇지 않으면 새로운 태스크에서 액티비티를 실행하는 플래그
//                | Intent.FLAG_ACTIVITY_SINGLE_TOP       // 실행할 액티비티가 태스크 스택 최상단에 이미 있다면 액티비티를 다시 실행하지 않는 플래그
//                | Intent.FLAG_ACTIVITY_CLEAR_TOP);      // 실행할 액티비티가 태스크에 이미 있다면 태스크에 있는 동일한 액티비티부터 최상단의 액티비티까지 모두 제거하고 새로운 액티비티를 실행하는 플래그
//        intent.putExtra("sender", sender);
//        intent.putExtra("content", content);
//        intent.putExtra("date", ma.sdf.format(millisDate));

        //context.startActivity(intent);      // sms 정보를 가진 intent 로 액티비티 호출

//        smsNotification(context, content);    // ←-- ★그만할 때 이부분을 주석처리해서 알림이 안뜨게 할 것(선택)
//    }


    @SuppressWarnings("AccessStaticViaInstance")
    private void smsNotification(Context context, String sender, String body, Long millisDate) {
        p = Pattern.compile("[0-9]{2}[/][0-9]{2} [0-9]{2}[:][0-9]{2}");           // ex) 02/09 15:32
        m = p.matcher(body);
        if (m.find()) { useDate = m.group(); }                // 매칭 될 문자가 1개 뿐이라 while()말고 if()를 사용함.
        else { useDate = null; }                              // 매칭되는 문자가 없으면 null

        p = Pattern.compile("([0-9])\\S*(원)");               // ex) 13,500원
        m = p.matcher(body);
        if (m.find()) { useAmount = m.group(); }
        else { useAmount = null; }

        if (!body.contains("승인거절") && !body.contains("잔액부족") && useDate != null && useAmount != null) {      // 결제 문자의 형식이 갖춰졌을 때~
            AppDatabase db = AppDatabase.getInstance(context);

            if(db.dao().getAutoState()){        // ★자동저장 기능이 "ON"일 때 실행★
                Cost cost = ma.parsing(body.replaceAll("\n", " "), ma.sdf.format(millisDate), millisDate);

                if (!cost.getSortName().equals("") && cost.getAmount()!=-1 && !cost.getContent().equals("")) {  // 결제문자면서 정규화되지 않았으면 리스트에 추가하지 않음
                    cost.setSortName(ma.matchPhoneNumber(sender, body));      // 결제 문자 구분을 위해 들어있던 sortName 대신 번호에 따른 wayName 을 set

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String wayName = checkWayName(cost.getSortName());
                            if(!wayName.equals("(Auto)")){
                                db.dao().updateWayNotiState(wayName, true);
                            }
                            ma.readSMSMessage(context, db);

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
                                    cost.getUseDate(), wayName, "(미분류)", cost.getAmount(), cost.getContent(), "expense", ma.getMs(), "new");

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
                    notiIntent = new Intent(context, AssetsActivity.class);       // 알림 클릭시 AssetActivity 로 이동하게 설정

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

    }



    private String checkWayName(String name){
        if(name.equals("")){
            name = "(Auto)";
        }

        return name;
    }



}