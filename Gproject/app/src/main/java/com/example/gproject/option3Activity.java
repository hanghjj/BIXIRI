package com.example.gproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TimePicker;


import com.example.gproject.databinding.ActivityOption3Binding;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Calendar;
import java.util.StringTokenizer;

public class option3Activity extends AppCompatActivity {
    private ActivityOption3Binding binding; // View Binding
    String[] AvF = {"\n\n패딩\n 두꺼운 코트\n 목도리\n + 기모제품",
            "\n\n코트\n 히트텍\n 니트\n 청바지\n 레깅스",
            "\n\n자켓\n 트렌치코트\n 야상\n 니트\n 스타킹\n 청바지\n 면바지",
            "\n\n자켓\n 가디건\n 야상\n 맨투맨\n 니트\n 스타킹\n 청바지\n 면바지",
            "\n\n얇은 니트\n 가디건\n 맨투맨\n 얇은 자켓\n 면바지\n 청바지",
            "\n\n얇은 가디건\n 긴팔티\n 면바지\n 청바지\n",
            "\n 반팔\n 얇은 셔츠\n 반바지\n 면바지",
            "\n\n민소매\n 반팔\n 반바지\n 치마"};
    public static int sethour, setmin;
    public static Context context;
    String AvgT;
    String UmborNot = "\n 우산 필요 없습니다.";
    String TodayF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding
        binding = ActivityOption3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.averageT.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.morningRainRate.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.AfternoonRainRate.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.RainRateGuide.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.averageTguide.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.fashionGuide.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.fashion.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.averageTguide.setText("오늘 평균 온도");
        binding.RainRateGuide.setText("오전 강수확률   /   오후 강수확률");
        binding.fashionGuide.setText("옷차림 추천");

        //알람 시간 관련 shaF
        SharedPreferences sharepref = getSharedPreferences("op3Time", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharepref.edit();
        sethour = sharepref.getInt("op3hour", 6);
        setmin = sharepref.getInt("op3min", 0);
        //선언 및 변수 로드

        binding.showsettext.setText("설정한 시간 = " + sethour + "시 " + setmin + "분 ");
        new Thread(() -> {
            try {
                Document doc1 = Jsoup.connect("https://search.naver.com/search.naver?sm=tab_hty.top&where=nexearch&query=%EB%82%A0%EC%94%A8").get();
                Elements temp_contents = doc1.select(".merge");
                Elements rain_contents = doc1.select(".rain_rate > .num");

                //오늘 강수량 측정
                String rain_text = rain_contents.text();
                StringTokenizer Rtoken = new StringTokenizer(rain_text);
                String MornRR = Rtoken.nextToken(" ");
                String AftRR = Rtoken.nextToken(" ");

                //온도 측정
                String text = temp_contents.text();
                text = text.replace("˚", " ");
                text = text.replace("/", " ");
                StringTokenizer token = new StringTokenizer(text);
                String mintempT = token.nextToken(" ");
                String maxtempT = token.nextToken(" ");
                double averageT = (Double.parseDouble(mintempT) * 1.2 + Double.parseDouble(maxtempT)) / 2;
                AvgT = String.format("%.1f",averageT) + "도";

                //옷차림 정하기
                if (averageT <= 4) {binding.fashion.setText(AvF[0]);TodayF = AvF[0];}
                else if (averageT >= 5 && averageT <= 8) {binding.fashion.setText(AvF[1]);TodayF = AvF[1];}
                else if (averageT >= 9 && averageT <= 11) {binding.fashion.setText(AvF[2]);TodayF = AvF[2];}
                else if (averageT >= 12 && averageT <= 16) {binding.fashion.setText(AvF[3]);TodayF = AvF[3];}
                else if (averageT >= 17 && averageT <= 19){ binding.fashion.setText(AvF[4]);TodayF = AvF[4];}
                else if (averageT >= 20 && averageT <= 22) {binding.fashion.setText(AvF[5]);TodayF = AvF[5];}
                else if (averageT >= 23 && averageT <= 27) {binding.fashion.setText(AvF[6]);TodayF = AvF[6];}
                else {binding.fashion.setText(AvF[7]);TodayF = AvF[7];}

                //우산 여부
                if (Double.parseDouble(MornRR) >= 60 || Double.parseDouble(AftRR) >= 60){
                    binding.hidegetUmb.setBackgroundColor(00000000);
                    UmborNot = "\n우산 챙기는 날입니다.";
                }
                binding.averageT.setText(AvgT);
                binding.morningRainRate.setText(MornRR);
                binding.AfternoonRainRate.setText(AftRR);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

        context = this;
        binding.settimepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(option3Activity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        sethour = view.getHour();
                        setmin = view.getMinute();
                        editor.putInt("op3hour", sethour);
                        editor.putInt("op3min", setmin);
                        editor.apply();
                        //시간 설정
                        Calendar alarmTime = Calendar.getInstance();
                        Calendar test = Calendar.getInstance();
                        int today = Calendar.DAY_OF_YEAR;
                        alarmTime.set(Calendar.DAY_OF_YEAR,today);
                        if(Calendar.HOUR_OF_DAY<=sethour&&Calendar.MINUTE<setmin)
                            alarmTime.add(Calendar.DAY_OF_YEAR,1);
                        alarmTime.set(Calendar.HOUR_OF_DAY, sethour);
                        alarmTime.set(Calendar.MINUTE, setmin);
                        alarmTime.set(Calendar.SECOND, 0);
                        alarmTime.set(Calendar.MILLISECOND, 0);
                        // 시작할 인텐트 지정
                      //  if(test.get(Calendar.HOUR_OF_DAY)<=sethour&&test.get(Calendar.MINUTE)<=setmin){
                        Intent alarmIntent = new Intent(option3Activity.this, AlarmReceiver.class);
                        alarmIntent.putExtra("requestCode",3);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(option3Activity.this, 3, alarmIntent, 0);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        if (alarmManager != null) {
                            // 버전에 따라 다르게 구현
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
                            } else {
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(),
                                        AlarmManager.INTERVAL_DAY, pendingIntent);
                            }
                        }
                    //}
                        binding.showsettext.setText("설정한 시간 : "+sethour + "시 " + setmin + "분 \n 현재시간은 " + test.get(Calendar.HOUR_OF_DAY)+"시"+test.get(Calendar.MINUTE)+"분");
                    }
                }, sethour, setmin, false);
                timePickerDialog.show();

            }
        });


        // 뒤로가기 버튼
        binding.ReturnHome.setOnClickListener(v -> {
            Intent intent = new Intent(option3Activity.this, MainActivity.class);
            finish();
            startActivity(intent);
        });

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(option3Activity.this, MainActivity.class);
        finish();
        startActivity(intent);
        super.onBackPressed();
    }

}
