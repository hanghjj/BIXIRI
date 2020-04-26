package com.example.gproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.gproject.database.AppDatabase;
import com.example.gproject.database.AppSharedPreference;
import com.example.gproject.databinding.ActivityMainBinding;
import com.example.gproject.thread.MenuCrawlingThread;

import androidx.appcompat.app.AppCompatActivity;

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

        menuCrawlingThread = new MenuCrawlingThread();
        db = AppDatabase.getInstance(this);

        

        AppSharedPreference pref = AppSharedPreference.getInstance(this);
        // 어플리케이션 최초 실행 확인
        if (pref.getBoolean(R.string.key_firstRun, true)) {
            // 최초 실행 값 설정
            pref.putBoolean(R.string.key_firstRun, false);
            pref.putString(R.string.key_departureStation, getString(R.string.default_departure_station));
            pref.putString(R.string.key_arrivalStation, getString(R.string.default_arrival_station));
        } else {
        }

        binding.op1.setOnClickListener(v -> {
            // new SubwayApiThread().getSubwayFromApi("수락산");
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
