package com.example.gproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
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
    public static Context context;
    public final int[] finishtime = new int[5];
    public final int[] mealtime = new int [5];
    public int onoff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding
        binding = ActivityTimeTableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this; //다른 Activity에서 참조할 수 있도록 하기

        //sharedpref 관련
        final SharedPreferences test = getSharedPreferences("test", MODE_PRIVATE);
        final SharedPreferences.Editor ed = test.edit();
        final ArrayList<String> key = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            key.add(Integer.toString(i));
        }
        final int[] keyindex = {test.getInt("keyindex", 0)}; //key값 index 불러오기
        //선언 끝


        //view , adapter 선언
        final String[] title = {"월", "화", "수", "목", "금", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "};
        final ArrayAdapter<String> ad = new ArrayAdapter<String>(this, R.layout.gridviewcustom, title);
        String[] times = {" ", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        ArrayAdapter<String> timead = new ArrayAdapter<>(this, R.layout.gridviewcustom, times);
        binding.time.setAdapter(timead);
        //선언 끝

        //불러오기
        for (int i = 0; i < 1000; i++) {
            int temp = test.getInt(key.get(i), -9); //저장된 position load
            if (temp == -1) continue;//바뀐 값일 경우 continue
            else if (temp == -9) break;//값이 있는 최대 index 도달 시 break
            title[temp] = "수업";
        }

        int var1 = test.getInt("onoff",-1);
        if(var1 == 0) binding.clickable.setClickable(true);
        else if(var1 ==1) binding.clickable.setClickable(false);
        //불러오기 과정끝

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


        //수정가능/불가 상태 구현
        binding.complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.clickable.setClickable(true);
                onoff = 0;
                ed.putInt("onoff",onoff);
                ed.apply();
            }
        });
        binding.change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.clickable.setClickable(false);
                onoff = 1;
                ed.putInt("onoff",onoff);
                ed.apply();
            }
        });


        //끝나는 시간 구하기
        for(int i=6; i<title.length; i++){
            if(title[i].equals("수업")){
                switch (i%5){
                    case 0:
                        finishtime[0] = i/5+9;
                        break;
                    case 1:
                        finishtime[1] = i/5+9;
                        break;
                    case 2:
                        finishtime[2] = i/5+9;
                        break;
                    case 3:
                        finishtime[3] = i/5+9;
                        break;
                    case 4:
                        finishtime[4] = i/5+9;
                        break;
                    default:
                        break;
                }
            }
        }
        //finishtime에 끝나는 시간 저장[월,화,수,목,금]
        //mealtime 구하기
        int mon = 5,tue=6,wed=7,thu=8,fri=9,
                monmin=999,monfirst=0,
                tuemin=999,tuefirst=0,
                wedmin=999,wedfirst=0,
                thumin=999,thufirst=0,
                frimin=999,frifirst=0;

        for(;mon<title.length;mon+=5){
                    if(title[mon].equals("수업"))
                    {   monfirst = mon;
                        break;   }
            }
            for(mon = monfirst; mon<title.length;mon+=5)
            {   if(title[mon].equals("수업"))continue;
                else{
                if(monmin>(mon/5+8))
                monmin = (mon/5+8);
            }}
         for(;tue<title.length;tue+=5){
                    if(title[tue].equals("수업"))
                    {   tuefirst = tue;
                        break;   }
                }
         for(tue = tuefirst; tue<title.length;tue+=5)
                {   if(title[tue].equals("수업"))continue;
                else{
                    if(tuemin>(tue/5+8))
                        tuemin = (tue/5+8);
                }}
         for(;wed<title.length;wed+=5){
                        if(title[wed].equals("수업"))
                        {   wedfirst = wed;
                            break;   }
                    }
          for(wed = wedfirst; wed<title.length;wed+=5)
                    {   if(title[wed].equals("수업"))continue;
                    else{
                        if(wedmin>(wed/5+8))
                            wedmin = (wed/5+8);
                    }}
          for(;thu<title.length;thu+=5){
                            if(title[thu].equals("수업"))
                            {   thufirst = thu;
                                break;   }
                        }
          for(thu = thufirst; thu<title.length;thu+=5)
                        {   if(title[thu].equals("수업"))continue;
                        else{
                            if(thumin>(thu/5+8))
                                thumin = (thu/5+8);
                        }}
          for(;fri<title.length;fri+=5){
                                if(title[fri].equals("수업"))
                                {   frifirst = fri;
                                    break;   }
                            }
          for(fri = frifirst; fri<title.length;fri+=5)
                            {   if(title[fri].equals("수업"))continue;
                            else{
                                if(frimin>(fri/5+8))
                                    frimin = (fri/5+8);
                            }
                            }binding.textView.setText("식사 시간 : \n" + Integer.toString(monmin)+"\n"+Integer.toString(tuemin)+"\n"+Integer.toString(wedmin)+"\n"+Integer.toString(thumin)+"\n"+Integer.toString(frimin)+"\n"+
                            "하교 시간 : \n" + Integer.toString(finishtime[0])+"\n"+Integer.toString(finishtime[1])+"\n"+Integer.toString(finishtime[2])+"\n"+Integer.toString(finishtime[3])+"\n"+Integer.toString(finishtime[4]));
        binding.ReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
