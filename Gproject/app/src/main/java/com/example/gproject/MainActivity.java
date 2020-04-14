package com.example.gproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button op1 = (Button) findViewById(R.id.op1);
        Button op2 = (Button) findViewById(R.id.op2);
        Button op3 = (Button) findViewById(R.id.op3);
        Button op4 = (Button) findViewById(R.id.op4);
        Button tt = (Button) findViewById(R.id.time_table);
        op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,option1Activity.class);
                startActivity(intent);
            }
        });
        op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,option2Activity.class);
                startActivity(intent);
            }
        });
        op3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,option3Activity.class);
                startActivity(intent);
            }
        });
        op4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,option4Activity.class);
                startActivity(intent);
            }
        });
        tt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TimeTableActivity.class);
                startActivity(intent);
            }
        });

    }


}
