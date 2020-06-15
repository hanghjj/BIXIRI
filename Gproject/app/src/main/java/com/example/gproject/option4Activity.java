package com.example.gproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.gproject.databinding.ActivityOption4Binding;

import java.util.Calendar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class option4Activity extends AppCompatActivity {
    private ActivityOption4Binding binding; // View Binding

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding
        binding = ActivityOption4Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // save 버튼 눌렀을 시
        binding.btnOp4Save.setOnClickListener(v -> {
            // 사용자가 지정한 시간에 인텐트 실행
            int hour = binding.timeOp4Test.getHour();
            int minute = binding.timeOp4Test.getMinute();
            Calendar alarmTime = Calendar.getInstance();
            alarmTime.set(Calendar.HOUR_OF_DAY, hour);
            alarmTime.set(Calendar.MINUTE, minute);
            alarmTime.set(Calendar.SECOND, 0);
            alarmTime.set(Calendar.MILLISECOND, 0);

            // 시작할 인텐트 지정
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            alarmIntent.putExtra("requestCode",0);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            if (alarmManager != null) {
                // 버전에 따라 다르게 구현
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY, pendingIntent);
                }
            }
        });

        // 뒤로가기 버튼
        binding.ReturnHome.setOnClickListener(v -> {
            finish();
        });
    }
}
