package com.example.gproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.gproject.database.AppSharedPreference;

import java.util.Calendar;
import java.util.Objects;

// 스마트폰이 재부팅 시에 실행되는 클래스
// 기존에 설정한 AlarmManager는 재부팅되면 날라가므로 새로 설정해야 한다
public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            AppSharedPreference pref = AppSharedPreference.getInstance(context);

            // 저장된 시간 불러옴
            int sethour = pref.getInt(R.string.key_op3hour, 0);
            int setmin = pref.getInt(R.string.key_op3min, 0);

            Calendar alarmTime = Calendar.getInstance();
            alarmTime.set(Calendar.HOUR_OF_DAY, sethour);
            alarmTime.set(Calendar.MINUTE, setmin);
            alarmTime.set(Calendar.SECOND, 0);
            alarmTime.set(Calendar.MILLISECOND, 0);

            // 알람 재설정
            if (manager != null) {
                manager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
    }
}
