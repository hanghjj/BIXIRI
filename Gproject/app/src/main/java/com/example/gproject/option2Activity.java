package com.example.gproject;


import android.os.Bundle;
import android.view.Gravity;

import com.example.gproject.database.AppDatabase;
import com.example.gproject.databinding.ActivityOption2Binding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class option2Activity extends AppCompatActivity {
    private ActivityOption2Binding binding; // View Binding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding
        binding = ActivityOption2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppDatabase db = AppDatabase.getInstance(this);

        binding.setnoti.setText("알림");
        binding.caf1.setText("학생회관 식당");
        binding.caf1.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.caf2.setText("교직원식당");
        binding.caf2.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.caf3.setText("제 2기숙사 식당");
        binding.caf3.setGravity(Gravity.CENTER_HORIZONTAL);

        // 현재 시간을 구한다
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dayFormat = new SimpleDateFormat("u", Locale.getDefault());
        SimpleDateFormat hourFormat = new SimpleDateFormat("H", Locale.getDefault());
        // 현재 요일
        int day = Integer.parseInt(dayFormat.format(date));
        // 현재 시간
        int hour = Integer.parseInt(hourFormat.format(date));

        // 뒤로가기 버튼
        binding.topAppBar2.setNavigationOnClickListener(v -> {
            finish();
        });
        binding.topAppBar2.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.refresh:
                    // Thread 생성해서 실행
                    new Thread(() -> {
                        List<String> menuItems = db.menuDAO().findMenu(day, 2, 1);
                        StringBuilder stringBuilder1 = new StringBuilder();
                        for (String menu : menuItems) {
                            stringBuilder1.append(menu).append("\n");
                        }

                        menuItems = db.menuDAO().findMenu(day, 2, 2);
                        StringBuilder stringBuilder2 = new StringBuilder();
                        for (String menu : menuItems) {
                            stringBuilder2.append(menu).append("\n");
                        }

                        menuItems = db.menuDAO().findMenu(day, 2, 3);
                        StringBuilder stringBuilder3 = new StringBuilder();
                        for (String menu : menuItems) {
                            stringBuilder3.append(menu).append("\n");
                        }

                        // Main Thread에서 UI 변경
                        runOnUiThread(() -> {
                            binding.cafeteria1.setText(stringBuilder1.toString());
                            binding.cafeteria1.setGravity(Gravity.CENTER_HORIZONTAL);
                            binding.cafeteria2.setText(stringBuilder2.toString());
                            binding.cafeteria2.setGravity(Gravity.CENTER_HORIZONTAL);
                            binding.cafeteria3.setText(stringBuilder3.toString());
                            binding.cafeteria3.setGravity(Gravity.CENTER_HORIZONTAL);
                        });
                    }).start();
                    return true;
            }
            return false;
        });
        /* 푸시알림 시도 후 실패한 코드
        binding.setnoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 NotificationManager notificationManager = (NotificationManager)option2Activity.this.getSystemService(option2Activity.this.NOTIFICATION_SERVICE);
                 Intent intent = new Intent(option2Activity.this.getApplicationContext(),option2Activity.class);
                 Notification.Builder builder = new Notification.Builder((getApplicationContext()));
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendnoti = PendingIntent.getActivity(option2Activity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setSmallIcon(R.drawable.ic_arrow_back_24dp).setTicker("Ticker").setWhen(System.currentTimeMillis()).setNumber(1).setContentTitle("Title").setContentText("Text").setContentIntent(pendnoti).setAutoCancel(true).setOngoing(true);
                assert notificationManager != null;
                notificationManager.notify(5,builder.build());
            }
        });*/
    }
}