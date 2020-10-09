package com.example.gproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.gproject.database.AppDatabase;
import com.example.gproject.database.AppSharedPreference;
import com.example.gproject.databinding.ActivityMainBinding;
import com.example.gproject.thread.MenuCrawlingThread;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    private long backbuttonduration = 0;
    private Toast toast;
    Calendar calendar = Calendar.getInstance();
    private ActivityMainBinding binding; // ViewBinding 사용
    private MenuCrawlingThread menuCrawlingThread; // 학교 식단을 크롤링하는 클래스
    private static AppDatabase db; // 싱글톤 db 객체
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private frag1 frag1;
    private frag3 frag3;
    private mainfrag mainfrag;
    private boolean loadF(Fragment f){
        if(f!=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,f).commit();
            return true;
        }
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding 아래 세줄은 건드리지 말것
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        mainfrag = new mainfrag();
        loadF(new mainfrag());
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment frag = null;
                switch(item.getItemId()) {
                    case R.id.main:
                        frag = new mainfrag();
                        break;
                    case R.id.act1:
                        frag = new frag1();
                        break;
                    case R.id.act2:
                        frag = new frag2();
                        break;
                    case R.id.act3:
                        frag = new frag3();
                        break;
                }
                return loadF(frag);
            }

        });



        //Shef 선언 끝
        menuCrawlingThread = new MenuCrawlingThread();
        db = AppDatabase.getInstance(this);
        AppSharedPreference pref = AppSharedPreference.getInstance(this);
        // 어플리케이션 최초 실행 확인
        if (pref.getBoolean(R.string.key_firstRun, true)) {
            // 최초 실행 값 설정
            pref.putBoolean(R.string.key_firstRun, false);
            pref.putString(R.string.key_departureStation, getString(R.string.default_departure_station));
            pref.putString(R.string.key_arrivalStation, getString(R.string.default_arrival_station));
            List<Integer> lineList = new ArrayList<>();
            lineList.add(1002);
            pref.putIntList(R.string.key_selectedLines, lineList);
            List<Integer> stationList = new ArrayList<>();
            stationList.add(1002000239);
            pref.putIntList(R.string.key_selectedStations, stationList);
            pref.putString(R.string.key_busRoute, getString(R.string.default_busRoute));
            pref.putString(R.string.key_busStop, getString(R.string.default_busStop));
            pref.putString(R.string.key_busRouteId, getString(R.string.default_busRouteId));
            pref.putString(R.string.key_busStId, getString(R.string.default_busStId));
            pref.putString(R.string.key_busOrg, getString(R.string.default_busOrg));
        } else {
        }

     /*   binding.op1.setOnClickListener(v -> {
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
            finish();
            startActivity(intent);
        });
        binding.op4.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, option4Activity.class);
            startActivity(intent);
        });*/
        binding.time.setOnClickListener(v -> {
            startActivity(new Intent(this, TimeTable2.class));
        });
    }

    // 뒤로가기 버튼 두번 연달아 누르면 앱 종료
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backbuttonduration + 2000) {
            backbuttonduration = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else {
            finish();
            toast.cancel();
        }
    }
}
