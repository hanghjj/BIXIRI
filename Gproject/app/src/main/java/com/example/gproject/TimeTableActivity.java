package com.example.gproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import java.util.List;

public class TimeTableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table2);
        ListView time = (ListView) findViewById(R.id.time);
        String str[] = {"   ", "1", "2", "3", "4", "5", "6",
                "7 ", "8", "9", "10", "11","12"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, str);
        time.setAdapter(adapter);

        ListView mon = (ListView) findViewById(R.id.mon);
        final String monclass[] = {"월", " ", " ", " ", " ", " ", " ",
                " "," ", " "," ", " ", " "};
        final ArrayAdapter<String> monadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, monclass);
        mon.setAdapter(monadapter);
        mon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(monclass[position].equals("수업")){
                    monclass[position] =" ";
                }
                else monclass[position] = "수업";
                monadapter.notifyDataSetChanged();
            }
        });
        ListView tue = (ListView) findViewById(R.id.tue);
        String tueclass[] = {"화", " ", " ", " ", " ", " ", " ",
                " "," ", " "," ", " ", " "};
        ArrayAdapter<String> tueadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tueclass);
        tue.setAdapter(tueadapter);

        ListView wed = (ListView) findViewById(R.id.wed);
        String wedclass[] = {"수", " ", " ", " ", " ", " ", " ",
                " "," ", " "," ", " ", " "};
        ArrayAdapter<String> wedadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wedclass);
        wed.setAdapter(wedadapter);

        ListView thu = (ListView) findViewById(R.id.thu);
        String thuclass[] = {"목", " ", " ", " ", " ", " ", " ",
                " "," ", " "," ", " ", " "};
        ArrayAdapter<String> thuadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, thuclass);
        thu.setAdapter(thuadapter);

        ListView fri = (ListView) findViewById(R.id.fri);
        String friclass[] = {"금", " ", " ", " ", " ", " ", " ",
                " "," ", " "," ", " ", " "};
        ArrayAdapter<String> friadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friclass);
        fri.setAdapter(friadapter);


        Button tt_backhome = (Button) findViewById(R.id.ReturnHome);
        tt_backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimeTableActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}
