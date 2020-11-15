package com.bixiri.gproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bixiri.gproject.databinding.ActivityOption3Binding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;
import java.util.StringTokenizer;


public class frag3 extends Fragment
{
    private View view;
    private ActivityOption3Binding binding; // View Binding
    String[] AvF = {"패딩\n 두꺼운 코트\n 목도리\n + 기모제품",
            "코트\n 히트텍\n 니트\n 청바지\n 레깅스",
            "자켓\n 트렌치코트\n 야상\n 니트\n 스타킹\n 청바지\n 면바지",
            "자켓\n 가디건\n 야상\n 맨투맨\n 니트\n 스타킹\n 청바지\n 면바지",
            "얇은 니트\n 가디건\n 맨투맨\n 얇은 자켓\n 면바지\n 청바지",
            "얇은 가디건\n 긴팔티\n 면바지\n 청바지\n",
            "반팔\n 얇은 셔츠\n 반바지\n 면바지",
            "민소매\n 반팔\n 반바지\n 치마"};
    public static int sethour, setmin;
    public static Context context;
    String AvgT;
    String UmborNot = "      우산 X";
    String TodayF;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = ActivityOption3Binding.inflate(inflater,container,false);
        view = binding.getRoot();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // View Binding
        binding.averageT.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.morningRainRate.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.AfternoonRainRate.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.RainRateGuide.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.averageTguide.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.fashionGuide.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.fashion.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.averageTguide.setText("오늘 평균 온도");
        binding.RainRateGuide.setText("오전 강수확률   |   오후 강수확률");
        binding.fashionGuide.setText("옷차림 추천");
        //알람 시간 관련 shaF

        SharedPreferences sharepref = this.getActivity().getSharedPreferences("op3Time", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharepref.edit();
        sethour = sharepref.getInt("op3hour", 6);
        setmin = sharepref.getInt("op3min", 0);
        //선언 및 변수 로드
        binding.showsettext.setText("설정한 시간 = " + sethour + "시 " + setmin + "분 ");

        new Thread(() -> {
            try {
                Document doc1 = Jsoup.connect("https://search.naver.com/search.naver?sm=tab_hty.top&where=nexearch&query=%EC%84%9C%EC%9A%B8%EB%82%A0%EC%94%A8").get();
                Elements temp_contents = doc1.select(".merge");
                Elements rain_contents = doc1.select(".rain_rate > .num");

                //오늘 강수량 측정
                String rain_text = rain_contents.text();
                StringTokenizer Rtoken = new StringTokenizer(rain_text);
                String MornRR = Rtoken.nextToken(" ");
                String AftRR = Rtoken.nextToken(" ");
                binding.morningRainRate.setText(MornRR+"%");
                binding.AfternoonRainRate.setText(AftRR+"%");
                //온도 측정
                String text = temp_contents.text();
                text = text.replace("˚", " ");
                text = text.replace("/", " ");
                StringTokenizer token = new StringTokenizer(text);
                String mintempT = token.nextToken(" ");
                String maxtempT = token.nextToken(" ");
                double averageT = (Double.parseDouble(mintempT) * 1.2 + Double.parseDouble(maxtempT)) / 2;
                AvgT = String.format("%.1f",averageT) + "˚C";
                binding.averageT.setText(AvgT);
                //옷차림 정하기
                if (averageT <= 4) {binding.fashion.setText(AvF[0]); TodayF = AvF[0];}
                else if (averageT >= 5 && averageT <= 8) {binding.fashion.setText(AvF[1]);TodayF = AvF[1];}
                else if (averageT >= 9 && averageT <= 11) {binding.fashion.setText(AvF[2]);TodayF = AvF[2];}
                else if (averageT >= 12 && averageT <= 16) {binding.fashion.setText(AvF[3]);TodayF = AvF[3];}
                else if (averageT >= 17 && averageT <= 19){ binding.fashion.setText(AvF[4]);TodayF = AvF[4];}
                else if (averageT >= 20 && averageT <= 22) {binding.fashion.setText(AvF[5]);TodayF = AvF[5];}
                else if (averageT >= 23 && averageT <= 27) {binding.fashion.setText(AvF[6]);TodayF = AvF[6];}
                else {binding.fashion.setText(AvF[7]);     TodayF = AvF[7];}

                //우산 여부
                if (Double.parseDouble(MornRR) >= 60 || Double.parseDouble(AftRR) >= 60){
                    binding.hidegetUmb.setBackgroundColor(Color.parseColor("#00000000"));
                    UmborNot = "\n우산 챙기는 날입니다.";
                }
                else binding.umbguide.setText(UmborNot);




            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

        binding.settimepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
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
                        alarmTime.set(Calendar.HOUR_OF_DAY, sethour);
                        alarmTime.set(Calendar.MINUTE, setmin);
                        alarmTime.set(Calendar.SECOND, 0);
                        alarmTime.set(Calendar.MILLISECOND, 0);
                        if (alarmTime.before(Calendar.getInstance()))
                            alarmTime.add(Calendar.DAY_OF_YEAR, 1);
                        Log.v("알람", "설정시간 :".concat(String.valueOf(alarmTime.getTimeInMillis())));
                        Log.v("알람", String.valueOf(Calendar.getInstance().getTimeInMillis()));
                         accessNotiM(alarmTime);



                       binding.showsettext.setText("설정한 시간 : "+alarmTime.get(Calendar.HOUR_OF_DAY) + "시 " +alarmTime.get(Calendar.MINUTE) +
                               "분 \n 현재시간은 " + Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+"시"+Calendar.getInstance().get(Calendar.MINUTE)+"분");
                    }
                }, sethour, setmin, true);
                timePickerDialog.show();

            }
        });


    }
    void accessNotiM(Calendar c){
        Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
        alarmIntent.putExtra("requestCode",3);
        alarmIntent.putExtra("AVGT",AvgT);
        alarmIntent.putExtra("UmborNot",UmborNot);
        alarmIntent.putExtra("TodayF",TodayF);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext().getApplicationContext(), 3, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(c.getTimeInMillis(),pendingIntent);
        if (alarmManager != null) {
            // 버전에 따라 다르게 구현
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.v("알람","if문");
            //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                alarmManager.setAlarmClock(ac,pendingIntent);
            }else { Log.v("알람","else 문 ");
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
    }
}

