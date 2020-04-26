package com.example.gproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.gproject.database.AppDatabase;
import com.example.gproject.databinding.ActivityMainBinding;
import com.example.gproject.thread.MenuCrawlingThread;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding; // ViewBinding 사용
    private MenuCrawlingThread menuCrawlingThread; // 학교 식단을 크롤링하는 클래스
    private static AppDatabase db; // 싱글톤 db 객체


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding 아래 세줄은 건드리지 말것
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /* 푸쉬알림 공부중..
        NotificationManager test = (NotificationManager)TimeTable2.this.getSystemService(TimeTable2.this.NOTIFICATION_SERVICE);
        Notification.Builder testB = new Notification.Builder(getApplicationContext());
        testB.setSmallIcon(R.drawable.ic_arrow_back_black_24dp).setContentTitle("ex").setContentTitle("example");
        test.notify(1,testB.build());
        */

        menuCrawlingThread = new MenuCrawlingThread();
        db = AppDatabase.getAppDatabase(this);

        binding.op1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, option1Activity.class);
            startActivity(intent);
        });
        binding.op2.setOnClickListener(v -> {
            menuCrawlingThread.getMenuFromWeb(db.menuDAO()); // 학교 식단을 크롤링하여 DB에 저장
            Intent intent = new Intent(MainActivity.this, option2Activity.class);
            startActivity(intent);
        });
        binding.op3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, option3Activity.class);
            startActivity(intent);
        });
        binding.op4.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, option4Activity.class);
            startActivity(intent);
        });
        binding.timeTable.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TimeTable2.class);
            startActivity(intent);
        });
    }
}
