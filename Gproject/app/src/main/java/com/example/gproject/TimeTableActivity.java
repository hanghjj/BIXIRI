package com.example.gproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TimeTableActivity extends AppCompatActivity {
    SQLiteDatabase sampleDB = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table2);

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

        ListView time = (ListView) findViewById(R.id.time);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, str);
        time.setAdapter(adapter);
        ListView mon = (ListView) findViewById(R.id.mon);
        final ArrayAdapter<String> monadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, monclass);
        mon.setAdapter(monadapter);
        mon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(monclass.get(position).equals("수업")){
                    monclass.set(position," ");
                }
                else monclass.set(position,"수업");
                monadapter.notifyDataSetChanged();
            }
        });

        ListView tue = (ListView) findViewById(R.id.tue);
        ArrayAdapter<String> tueadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tueclass);
        tue.setAdapter(tueadapter);

        ListView wed = (ListView) findViewById(R.id.wed);
        ArrayAdapter<String> wedadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wedclass);
        wed.setAdapter(wedadapter);

        ListView thu = (ListView) findViewById(R.id.thu);
        ArrayAdapter<String> thuadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, thuclass);
        thu.setAdapter(thuadapter);

        ListView fri = (ListView) findViewById(R.id.fri);
        ArrayAdapter<String> friadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friclass);
        fri.setAdapter(friadapter);


        Button tt_backhome = (Button) findViewById(R.id.ReturnHome);
        tt_backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(TimeTableActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}
