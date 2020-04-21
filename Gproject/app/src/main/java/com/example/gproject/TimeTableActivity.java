package com.example.gproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.gproject.databinding.ActivityTimeTable2Binding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class TimeTableActivity extends AppCompatActivity {
    private ActivityTimeTable2Binding binding; // View Binding

    SQLiteDatabase sampleDB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding
        binding = ActivityTimeTable2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //DB 선언 및 테이블 생성
        sampleDB = this.openOrCreateDatabase("class", MODE_PRIVATE, null);
        sampleDB.execSQL("CREATE TABLE IF NOT EXISTS CLASS (day VARCHAR(20), c1 VARCHAR(5),c2 VARCHAR(5),c3 VARCHAR(5),c4 VARCHAR(5),c5 VARCHAR(5),c6 VARCHAR(5),c7 VARCHAR(5),c8 VARCHAR(5),c9 VARCHAR(5),c10 VARCHAR(5)" +
                ",c11 VARCHAR(5),c12 VARCHAR(5))");
        sampleDB.execSQL("CREATE TABLE IF NOT EXISTS Sample (data VARCHAR(100))");
        Cursor cursor = sampleDB.rawQuery("SELECT * FROM DATA",null);
        String temp1 = cursor.getString(0);
        //끝

        //초기 배열 선언
        String str[] = {"   ", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11","12"};
        final ArrayList<String> monclass = new ArrayList<String>();
        monclass.add("월");
        for(int i=0;i<12;i++) monclass.add(" ");
        String tueclass[] = {"화", " ", " ", " ", " ", " ", " ", " "," ", " "," ", " ", " "};
        String wedclass[] = {"수", " ", " ", " ", " ", " ", " ", " "," ", " "," ", " ", " "};
        String thuclass[] = {"목", " ", " ", " ", " ", " ", " ", " "," ", " "," ", " ", " "};
        String friclass[] = {"금", " ", " ", " ", " ", " ", " ", " "," ", " "," ", " ", " "};
        //선언 끝

        //Gson 관련
        final Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String >>(){}.getType();
        final String monjson = gson.toJson(monclass);
        ArrayList<String> temp12 = new ArrayList<>();
        temp12 = gson.fromJson(monjson,type);
        //GSon 끝

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, str);
        binding.time.setAdapter(adapter);

        final ArrayAdapter<String> monadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, monclass);
        binding.mon.setAdapter(monadapter);
        binding.mon.setOnItemClickListener((parent, view, position, id) -> {
            if(monclass.get(position).equals("수업")){
                monclass.set(position," ");
            }
            else monclass.set(position,"수업");
            monadapter.notifyDataSetChanged();
        });

        ArrayAdapter<String> tueadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tueclass);
        binding.tue.setAdapter(tueadapter);

        ArrayAdapter<String> wedadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wedclass);
        binding.wed.setAdapter(wedadapter);

        ArrayAdapter<String> thuadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, thuclass);
        binding.thu.setAdapter(thuadapter);

        ArrayAdapter<String> friadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friclass);
        binding.fri.setAdapter(friadapter);

        // 뒤로가기 버튼
        binding.ReturnHome.setOnClickListener(v -> {
            finish();
        });
    }
}
