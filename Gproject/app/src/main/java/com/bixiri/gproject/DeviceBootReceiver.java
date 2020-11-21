package com.bixiri.gproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.bixiri.gproject.database.AppSharedPreference;

import java.util.Calendar;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

// 스마트폰이 재부팅 시에 실행되는 클래스
// 기존에 설정한 AlarmManager는 재부팅되면 날라가므로 새로 설정해야 한다
public class DeviceBootReceiver extends BroadcastReceiver {
    private Context mcontext;
    int[] starttime = new int[5];
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            // 저장된 시간 불러옴
            mcontext =context;
            SharedPreferences timepref= context.getSharedPreferences("test", MODE_PRIVATE);
            for(int i=0;i<5;i++){
                starttime[i] = timepref.getInt("start".concat(Integer.toString(i)),-1);
            }
            op3Aset();
        }
    }
    void op1Aset(){}
    void op2Aset(){}
    void op3Aset(){
        Calendar[] alarmTime = new Calendar[5];
        for(int i = 0;i<5;i++){
            alarmTime[i] = Calendar.getInstance();
        }
        for(int i = 2;i<7;i++){
            alarmTime[i-2].set(Calendar.DAY_OF_WEEK,i);
            if(starttime[i-2] != -1){
                alarmTime[i-2].set(Calendar.HOUR_OF_DAY,starttime[i-2]);
                alarmTime[i-2].set(Calendar.MINUTE, 0);
                alarmTime[i-2].set(Calendar.SECOND, 0);
                alarmTime[i-2].set(Calendar.MILLISECOND, 0);
            }
            else alarmTime[i-2].set(Calendar.YEAR,1970);
        }
        for(int i = 2;i<7;i++){
            if (alarmTime[i-2].before(Calendar.getInstance())&&alarmTime[i-2].get(Calendar.DAY_OF_WEEK)==Calendar.getInstance().get(Calendar.DAY_OF_WEEK)||alarmTime[i-2].get(Calendar.DAY_OF_MONTH)<Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                alarmTime[i-2].add(Calendar.DAY_OF_MONTH, 7);
        }
        for(int i =0;i<5;i++){
            if(alarmTime[i].get(Calendar.YEAR)!=1970){
                accessNotiM(alarmTime[i],alarmTime[i].get(Calendar.DAY_OF_WEEK));
            }
        }
    }
    void accessNotiM(Calendar c,int day){
        Intent alarmIntent = new Intent(mcontext, AlarmReceiver.class);
        alarmIntent.putExtra("requestCode",3);
        alarmIntent.putExtra("day",day);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mcontext.getApplicationContext(), day, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mcontext.getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(c.getTimeInMillis(),pendingIntent);
        if (alarmManager != null) {
            // 버전에 따라 다르게 구현
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.v("알람","if문"+String.valueOf(day));
                //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                alarmManager.setAlarmClock(ac,pendingIntent);
            }else { Log.v("알람","else 문 ");
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
    }
}
