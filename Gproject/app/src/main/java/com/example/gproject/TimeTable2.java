package com.example.gproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.gproject.databinding.ActivityTimeTableBinding;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class TimeTable2 extends AppCompatActivity {
    private ActivityTimeTableBinding binding; // View Binding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding
        binding = ActivityTimeTableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //sharedpref 관련
        final SharedPreferences test = getSharedPreferences("test", MODE_PRIVATE);
        final SharedPreferences.Editor ed = test.edit();
        final ArrayList<String> key = new ArrayList<String>();
        for (int i = 0; i < 50; i++) {
            key.add(Integer.toString(i));
        }
        final int[] keyindex = {test.getInt("keyindex", 0)}; //key값 index 불러오기
        //선언 끝

        //view , adapter 선언
        final String[] title = {"월", "화", "수", "목", "금", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "};
        final ArrayAdapter<String> ad = new ArrayAdapter<String>(this, R.layout.gridviewcustom, title);
        String[] times = {" ", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        ArrayAdapter<String> timead = new ArrayAdapter<>(this, R.layout.gridviewcustom, times);
        //선언 끝

        binding.time.setAdapter(timead);


        for (int i = 0; i < 1000; i++) {
            int temp = test.getInt(key.get(i), -9); //저장된 position load
            if (temp == -1) continue;//바뀐 값일 경우 continue
            else if (temp == -9) break;//값이 있는 최대 index 도달 시 break
            title[temp] = "수업";
        }

        binding.gridview.setAdapter(ad);
        binding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (title[position].equals("수업")) {
                    title[position] = " ";
                    for (int i = 0; i < 1000; i++) {
                        int temp = test.getInt(key.get(i), -9);
                        if (temp == -1) continue; //바뀐 값이면 continue
                        else if (temp == -9) break; //최대 index 도달 시 break
                        if (temp == position) {
                            ed.putInt(key.get(i), -1);
                            ed.apply();
                        } //sharedpref에 저장된 position값들 중 방금 삭제한 position에 일치시 해당 data 삭제
                    }
                } else {
                    title[position] = "수업";
                    ed.putInt(key.get(keyindex[0]++), position); // 바꾼 값의 index 저장
                    ed.putInt("keyindex", keyindex[0]); // 저장된 index의 key값 따로 저장
                    ed.apply();
                }
                ad.notifyDataSetChanged();
            }
        });

        binding.ReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
