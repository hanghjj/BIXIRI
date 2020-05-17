package com.example.gproject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.DatePicker;
import android.widget.TimePicker;


import com.example.gproject.databinding.ActivityOption3Binding;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;

public class option3Activity extends AppCompatActivity {
    private ActivityOption3Binding binding; // View Binding
    String[] AvF = {"\n\n패딩\n 두꺼운 코트\n 목도리\n + 기모제품",
            "\n\n코트\n 히트텍\n 니트\n 청바지\n 레깅스",
            "\n\n자켓\n 트렌치코트\n 야상\n 니트\n 스타킹\n 청바지\n 면바지",
            "\n\n자켓\n 가디건\n 야상\n 맨투맨\n 니트\n 스타킹\n 청바지\n 면바지",
            "\n\n얇은 니트\n 가디건\n 맨투맨\n 얇은 자켓\n 면바지\n 청바지",
            "\n\n얇은 가디건\n 긴팔티\n 면바지\n 청바지\n",
            "\n\n반팔\n 얇은 셔츠\n 반바지\n 면바지",
            "\n\n민소매\n 반팔\n 반바지\n 치마"};
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
        //new AlarmHatt(getApplicationContext()).Alarm();

        new Thread(() -> {
            try{
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
                text = text.replace("˚"," ");
                text = text.replace("/"," ");
                StringTokenizer token = new StringTokenizer(text);
                String mintempT = token.nextToken(" ");
                String maxtempT = token.nextToken(" ");
                double averageT = (Double.parseDouble(mintempT)*1.2+Double.parseDouble(maxtempT))/2;
                String AvgT = String.valueOf(averageT)+"도";

                //옷차림 정하기
                if(averageT<=4) binding.fashion.setText(AvF[0]);
                else if(averageT>=5&&averageT<=8) binding.fashion.setText(AvF[1]);
                else if(averageT>=9&&averageT<=11) binding.fashion.setText(AvF[2]);
                else if(averageT>=12&&averageT<=16) binding.fashion.setText(AvF[3]);
                else if(averageT>=17&&averageT<=19) binding.fashion.setText(AvF[4]);
                else if(averageT>=20&&averageT<=22) binding.fashion.setText(AvF[5]);
                else if(averageT>=23&&averageT<=27) binding.fashion.setText(AvF[6]);
                else binding.fashion.setText(AvF[7]);

                //우산 여부
                if(Double.parseDouble(MornRR)>=60||Double.parseDouble(AftRR)>=60)
                    binding.hidegetUmb.setBackgroundColor(00000000);
                binding.averageT.setText(AvgT);
                binding.morningRainRate.setText(MornRR);
                binding.AfternoonRainRate.setText(AftRR);
            }catch(IOException e){e.printStackTrace();}

        }).start();


        // 뒤로가기 버튼
        binding.ReturnHome.setOnClickListener(v -> {
            finish();
        });
    }
    /*public class AlarmHatt{
        private Context context;
        public AlarmHatt(Context context){
            this.context = context;
        }

        public void Alarm() {
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(option3Activity.this,AlarmReceiver.class);

            PendingIntent sender = PendingIntent.getBroadcast(option3Activity.this,0,intent,0);
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE),00,31,0); //시간 설정 -> 여기를 변수로 바꾸면 시간 설정 가능
            alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),sender);
        }
    }*/

}
