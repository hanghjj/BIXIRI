package com.example.gproject;

import android.os.Bundle;
import android.view.Gravity;

import com.example.gproject.database.AppDatabase;
import com.example.gproject.database.MenuDAO;
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

        //시간표 상 끝나는 시간 불러오기
        int[] finish = new int[5];
        for(int i=0;i<5; i++){
            finish[i] = ((TimeTable2)TimeTable2.context).finishtime[i];
        }
        //

        AppDatabase db = AppDatabase.getAppDatabase(this);

        // 현재 시간을 구한다
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dayFormat = new SimpleDateFormat("u", Locale.getDefault());
        SimpleDateFormat hourFormat = new SimpleDateFormat("H", Locale.getDefault());
        // 현재 요일
        int day = Integer.parseInt(dayFormat.format(date));
        // 현재 시간
        int hour = Integer.parseInt(hourFormat.format(date));

        // 뒤로가기 버튼
        binding.ReturnHome.setOnClickListener(v -> {
            finish();
        });

        // 새로고침 버튼
        binding.refresh.setOnClickListener(v -> {
            // Thread 생성해서 실행
            new Thread(() -> {
                List<MenuDAO.MenuItem> menuItems = db.menuDAO().findMenu(day, 2, 1);
                StringBuilder stringBuilder1 = new StringBuilder();
                for (MenuDAO.MenuItem menu : menuItems) {
                    stringBuilder1.append(menu.menuItem).append("\n");
                }

                menuItems = db.menuDAO().findMenu(day, 2, 2);
                StringBuilder stringBuilder2 = new StringBuilder();
                for (MenuDAO.MenuItem menu : menuItems) {
                    stringBuilder2.append(menu.menuItem).append("\n");
                }

                menuItems = db.menuDAO().findMenu(day, 2, 3);
                StringBuilder stringBuilder3 = new StringBuilder();
                for (MenuDAO.MenuItem menu : menuItems) {
                    stringBuilder3.append(menu.menuItem).append("\n");
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
        });
    }
}
