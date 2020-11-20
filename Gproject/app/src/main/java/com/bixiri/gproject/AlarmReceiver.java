package com.bixiri.gproject;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.app.SharedElementCallback;

import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.bixiri.gproject.database.AppDatabase;
import com.bixiri.gproject.database.AppSharedPreference;
import com.bixiri.gproject.thread.BusApiThread;
import com.bixiri.gproject.thread.SubwayApiThread;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.core.app.NotificationCompat;

import static android.content.Context.MODE_PRIVATE;


public class AlarmReceiver extends BroadcastReceiver {
    Context context1;
<<<<<<< Updated upstream
=======
    TextToSpeech tts;
    String op3T;
    String op3U;
    String op3F;
>>>>>>> Stashed changes
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("알람","클래스 진입");
        AppDatabase db = AppDatabase.getInstance(context);
        AppSharedPreference pref = AppSharedPreference.getInstance(context);
        context1 = context;
        int daynum = intent.getIntExtra("day",-1);
        int requestCode = intent.getIntExtra("requestCode", 0);
        Log.v("알람","코드 : ".concat(String.valueOf(requestCode)));
        // 학식 메뉴 정보 받아서 푸쉬알림
        if (requestCode == 0 || requestCode == 1) {
            // 현재 시간을 구한다
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat dayFormat = new SimpleDateFormat("u", Locale.getDefault());
            SimpleDateFormat hourFormat = new SimpleDateFormat("H", Locale.getDefault());
            // 현재 요일
            int day = Integer.parseInt(dayFormat.format(date));
            // 현재 시간
            int hour = Integer.parseInt(hourFormat.format(date));

            new Thread(() -> {
                List<String> menuItems = db.menuDAO().findMenu(day, 2, 1);
                StringBuilder stringBuilder1 = new StringBuilder();
                for (String menu : menuItems) {
                    stringBuilder1.append(menu).append(" ");
                }

                menuItems = db.menuDAO().findMenu(day, 2, 2);
                StringBuilder stringBuilder2 = new StringBuilder();
                for (String menu : menuItems) {
                    stringBuilder2.append(menu).append(" ");
                }

                menuItems = db.menuDAO().findMenu(day, 2, 3);
                StringBuilder stringBuilder3 = new StringBuilder();
                for (String menu : menuItems) {
                    stringBuilder3.append(menu).append(" ");
                }
                String menunoti = "학생회관\n".concat(stringBuilder1.toString()).concat("\n교직원식당\n").concat(stringBuilder2.toString()).concat("\n제2기숙사\n").concat(stringBuilder3.toString());
                sendNotification(context, "menu", "학식메뉴", 1, "학식메뉴", menunoti,1);
            }).start();
        }

        if (requestCode == 0 || requestCode == 2) {
            StringBuffer result= new StringBuffer();
            {
                String departureStation = pref.getString(R.string.key_departureStation, "");
                String arrivalStation = pref.getString(R.string.key_arrivalStation, "");

                // SubwayApiThread를 통해서 받아올 리스트
                List<SubwayApiThread.SubwayArrival> arrivalList = new ArrayList<>();

                // SubwayApiThread에서 리스트 받아온 이후로 실행할 내용
                Runnable afterRun = () -> {
                    int index = 6;
                    for (SubwayApiThread.SubwayArrival arrival : arrivalList) {
                        String arrivalText; // 00분 00초 후 도착으로 표시
                        {
                            int arrivalTime = arrival.getArrivalTime();
                            int minute = arrivalTime / 60;
                            int second = arrivalTime % 60;
                            if (second == 0) {
                                arrivalText = minute + "분";
                            } else if (minute == 0) {
                                arrivalText = second + "초";
                            } else {
                                arrivalText = minute + "분 " + second + "초";
                            }
                        }
                        String temp = "".concat(arrival.getCurrentLocation()).concat("\n").concat(context.getString(R.string.recyclerview_item_subway_arrival_content1,
                                arrival.getDestination(), arrivalText)).concat("\n");

                        result.append(temp);
                        Log.v("알람",result.toString());
                        index++;
                    }
                    sendNotification(context, "subway", "대중교통 도착정보", 2, "대중교통 도착정보",
                            result.toString(),2);
                };

                // Thread를 실행한다
                SubwayApiThread subwayApiThread = new SubwayApiThread(context, arrivalList, departureStation, arrivalStation, afterRun);
                subwayApiThread.start();
            }

            {
                String busStId = pref.getString(R.string.key_busStId, "");
                String busRouteId = pref.getString(R.string.key_busRouteId, "");
                String busOrd = pref.getString(R.string.key_busOrg, "");
                // BusApiThread를 통해서 받아올 리스트
                List<BusApiThread.BusArrival> arrivalList = new ArrayList<>();

                Runnable afterRun = () -> {
                    int index = 4;
                    for (BusApiThread.BusArrival arrival : arrivalList) {
                       String temp = "".concat( arrival.getStationNm()).concat("\n").concat(context.getString(R.string.recyclerview_item_bus_arrival_content1,
                                arrival.getBusType(),
                                arrival.getRerideNum(),
                                arrival.getTraTime(),
                                arrival.getOrdDiff())).concat("\n");
                        /*sendNotification(context, "bus", "버스도착정보", index, arrival.getStationNm(),
                                context.getString(R.string.recyclerview_item_bus_arrival_content1,
                                        arrival.getBusType(),
                                        arrival.getRerideNum(),
                                        arrival.getTraTime(),
                                        arrival.getOrdDiff()),2);*/
                        index++;
                        result.append(temp);
                        Log.v("알람",result.toString());
                    }
                };

                BusApiThread busApiThread = new BusApiThread(arrivalList, busStId, busRouteId, busOrd, afterRun);
                busApiThread.start();
            }
        }
        if (requestCode == 0 || requestCode == 3) {
            op3T = intent.getStringExtra("AvgT");
            op3U = intent.getStringExtra("UmborNot");
            op3F = intent.getStringExtra("TodayF");;
            sendNotification(context, "op3", "오늘의 날씨", 0, "날씨정보", "평균온도 :" + op3T+"\n\n오늘의 의상 추천 "+op3F +op3U,3);
        }

    }


    void sendNotification(Context context, String channelId, String channelName, int id, String title, String text,int rCode) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Intent notificationIntent = new Intent(context, MainActivity.class);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendint = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setTicker("{Time to watch some cool stuff!}")
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//                .setContentIntent(pendint);

        // OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.ic_baseline_home_24);
            NotificationChannel ch = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(ch);
            }
        } else builder.setSmallIcon(R.mipmap.ic_launcher);

        if (notificationManager != null) {
            Log.v("알람","낫널");
            notificationManager.notify(id, builder.build());
            Calendar nextNotifyTime = Calendar.getInstance();
            nextNotifyTime.add(Calendar.DATE,7);
            repeat(nextNotifyTime, rCode);
        }

    }
    void repeat(Calendar calendar,int rCode){
        Log.v("알람","다음 알람 설정 완료");
        //Boolean 값 추가로 ON/OFF 기능 만들기
       /* Toast.makeText(context1, String.valueOf(calendar.get(Calendar.DATE)).concat("일 ").
                concat(String.valueOf(calendar.get(Calendar.HOUR))).concat("시 ").
                concat(String.valueOf(calendar.get(Calendar.MINUTE))).
                concat("분에 다음 알람이 설정 되었습니다."), Toast.LENGTH_LONG).show();*/
        Intent alarmIntent = new Intent(context1, AlarmReceiver.class);
        alarmIntent.putExtra("requestCode",rCode);
        alarmIntent.putExtra("AvgT",op3T);
        alarmIntent.putExtra("UmborNot",op3U);
        alarmIntent.putExtra("TodayF",op3F);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, rCode, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(),pendingIntent);
        Log.v("알람","설정 시간 : ".concat(String.valueOf(calendar.getTimeInMillis())));
        if (alarmManager != null) {
            // 버전에 따라 다르게 구현
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.v("알람","if문repeat");
                //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                alarmManager.setAlarmClock(ac,pendingIntent);
            }else { Log.v("알람","else 문 ");
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
        else Log.v("알람","알람매니져 = null");
    }
}