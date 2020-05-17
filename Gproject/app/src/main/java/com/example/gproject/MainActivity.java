package com.example.gproject;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.gproject.database.AppDatabase;
import com.example.gproject.database.AppSharedPreference;
import com.example.gproject.databinding.ActivityMainBinding;
import com.example.gproject.thread.MenuCrawlingThread;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

        menuCrawlingThread = new MenuCrawlingThread();
        db = AppDatabase.getInstance(this);

        new AlarmHatt(getApplicationContext()).Alarm();

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

    public class AlarmHatt {
            private Context context;
            public AlarmHatt(Context context){
                this.context = context;
            }

            public void Alarm() {
                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(MainActivity.this,AlarmReceiver.class);

                PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this,0,intent,0);
                Calendar calendar = Calendar.getInstance();
                calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE),00,40,0); //시간 설정 -> 여기를 변수로 바꾸면 시간 설정 가능
                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),sender);
            }
        }
       /* 푸쉬알림 코드 -> AlarmReceiver 클래스로 복사해놓음
        binding.button.setOnClickListener(v -> {
            NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notiint  = new Intent(this, MainActivity.class);
            notiint.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendint = PendingIntent.getActivity(this,0,notiint,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"10001").setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_arrow_back_24dp))
                    .setContentTitle("Test").setContentText("test").setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendint).setAutoCancel(true);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    builder.setSmallIcon(R.drawable.op2_background);
                    CharSequence channalName = "TEST";
                    String description = "test";
                    int imp = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel ch = new NotificationChannel("10001",channalName,imp);
                    ch.setDescription(description);

                    assert noti != null;
                    noti.createNotificationChannel(ch);

                }else builder.setSmallIcon(R.mipmap.ic_launcher);

                assert noti != null;
                noti.notify(1234,builder.build());
        });*/

}
