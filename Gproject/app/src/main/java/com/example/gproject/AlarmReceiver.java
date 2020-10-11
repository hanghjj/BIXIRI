package com.example.gproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.gproject.database.AppDatabase;
import com.example.gproject.database.AppSharedPreference;
import com.example.gproject.thread.BusApiThread;
import com.example.gproject.thread.SubwayApiThread;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("알람","클래스 진입");
        AppDatabase db = AppDatabase.getInstance(context);
        AppSharedPreference pref = AppSharedPreference.getInstance(context);
        int requestCode = intent.getIntExtra("requestCode", 0);
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

                sendNotification(context, "menu", "학식메뉴", 1, "학생회관", stringBuilder1.toString());
                sendNotification(context, "menu", "학식메뉴", 2, "교직원식당", stringBuilder2.toString());
                sendNotification(context, "menu", "학식메뉴", 3, "제2기숙사", stringBuilder3.toString());
            }).start();
        }

        if (requestCode == 0 || requestCode == 2) {
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
                        sendNotification(context, "subway", "지하철도착정보", index, arrival.getCurrentLocation(),
                                context.getString(R.string.recyclerview_item_subway_arrival_content1,
                                        arrival.getDestination(),
                                        arrivalText));
                        index++;
                    }
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
                        sendNotification(context, "bus", "버스도착정보", index, arrival.getStationNm(),
                                context.getString(R.string.recyclerview_item_bus_arrival_content1,
                                        arrival.getBusType(),
                                        arrival.getRerideNum(),
                                        arrival.getTraTime(),
                                        arrival.getOrdDiff()));
                        index++;
                    }
                };

                BusApiThread busApiThread = new BusApiThread(arrivalList, busStId, busRouteId, busOrd, afterRun);
                busApiThread.start();
            }
        }
        if (requestCode == 0 || requestCode == 3) {
            String op3T = intent.getStringExtra("AVGT");
            String op3U = intent.getStringExtra("UmborNot");
            String op3F = intent.getStringExtra("TodayF");;

            if(op3T != null)
            sendNotification(context, "op3", "오늘의 날씨", 0, "날씨정보", "평균온도 :" + op3T+"\n\n오늘의 의상 추천 "+op3F +op3U);
        }
    }


    void sendNotification(Context context, String channelId, String channelName, int id, String title, String text) {
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
            builder.setSmallIcon(R.drawable.op2_background);
            NotificationChannel ch = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(ch);
            }
        } else builder.setSmallIcon(R.mipmap.ic_launcher);

        if (notificationManager != null) {
            notificationManager.notify(id, builder.build());
        }
    }
}