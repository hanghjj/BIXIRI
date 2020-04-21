package com.example.gproject;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.gproject.databinding.ActivityTimeTable2Binding;

import androidx.appcompat.app.AppCompatActivity;

public class TimeTableActivity extends AppCompatActivity {
    private ActivityTimeTable2Binding binding; // View Binding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding
        binding = ActivityTimeTable2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String str[] = {"   ", "1", "2", "3", "4", "5", "6",
                "7 ", "8", "9", "10", "11","12"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, str);
        binding.time.setAdapter(adapter);

        final String monclass[] = {"월", " ", " ", " ", " ", " ", " ",
                " "," ", " "," ", " ", " "};
        final ArrayAdapter<String> monadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, monclass);
        binding.mon.setAdapter(monadapter);
        binding.mon.setOnItemClickListener((parent, view, position, id) -> {
            if(monclass[position].equals("수업")){
                monclass[position] =" ";
            }
            else monclass[position] = "수업";
            monadapter.notifyDataSetChanged();
        });

        String tueclass[] = {"화", " ", " ", " ", " ", " ", " ",
                " "," ", " "," ", " ", " "};
        ArrayAdapter<String> tueadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tueclass);
        binding.tue.setAdapter(tueadapter);

        String wedclass[] = {"수", " ", " ", " ", " ", " ", " ",
                " "," ", " "," ", " ", " "};
        ArrayAdapter<String> wedadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wedclass);
        binding.wed.setAdapter(wedadapter);

        String thuclass[] = {"목", " ", " ", " ", " ", " ", " ",
                " "," ", " "," ", " ", " "};
        ArrayAdapter<String> thuadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, thuclass);
        binding.thu.setAdapter(thuadapter);

        String friclass[] = {"금", " ", " ", " ", " ", " ", " ",
                " "," ", " "," ", " ", " "};
        ArrayAdapter<String> friadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friclass);
        binding.fri.setAdapter(friadapter);

        // 뒤로가기 버튼
        binding.ReturnHome.setOnClickListener(v -> {
            finish();
        });
    }
}
