package com.bixiri.gproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bixiri.gproject.database.AppDatabase;
import com.bixiri.gproject.databinding.ActivityOption2Binding;
import com.bixiri.gproject.thread.MenuCrawlingThread;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class frag2 extends Fragment{
    private ActivityOption2Binding binding;
    private View view;
    Activity activity;
    int[] mealtime  = new int[5];
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedinstancestate) {
        binding = ActivityOption2Binding.inflate(inflater, container, false);
        view = binding.getRoot();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppDatabase db = AppDatabase.getInstance(getContext());
        SharedPreferences timepref = this.getActivity().getSharedPreferences("test", MODE_PRIVATE);
        for(int i=0;i<5;i++) {
            mealtime[i] = timepref.getInt("meal".concat(Integer.toString(i)),-1);
        }
        Log.v("알람","asdf"+String.valueOf(mealtime[4]));
        new MenuCrawlingThread().getMenuFromWeb(db.menuDAO());

        binding.caf1.setText("학생회관 식당");
        binding.caf1.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.caf2.setText("교직원식당");
        binding.caf2.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.caf3.setText("제 2기숙사 식당");
        binding.caf3.setGravity(Gravity.CENTER_HORIZONTAL);

        // 현재 시간을 구한다
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dayFormat = new SimpleDateFormat("u", Locale.getDefault());
        SimpleDateFormat hourFormat = new SimpleDateFormat("H", Locale.getDefault());
        // 현재 요일
        int day = Integer.parseInt(dayFormat.format(date));
        // 현재 시간
        int hour = Integer.parseInt(hourFormat.format(date));

        binding.topAppBar2.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.refresh:
                    // Thread 생성해서 실행
                    new Thread(() -> {
                        List<String> menuItems = db.menuDAO().findMenu(day, 2, 1);
                        StringBuilder stringBuilder1 = new StringBuilder();
                        for (String menu : menuItems) {
                            stringBuilder1.append(menu).append("\n");
                        }
                        menuItems = db.menuDAO().findMenu(day, 2, 2);
                        StringBuilder stringBuilder2 = new StringBuilder();
                        for (String menu : menuItems) {
                            stringBuilder2.append(menu).append("\n");
                        }

                        menuItems = db.menuDAO().findMenu(day, 2, 3);
                        StringBuilder stringBuilder3 = new StringBuilder();
                        for (String menu : menuItems) {
                            stringBuilder3.append(menu).append("\n");
                        }

                        // Main Thread에서 UI 변경
                        if (activity != null)
                            activity.runOnUiThread(() -> {
                                binding.cafeteria1.setText(stringBuilder1.toString());
                                binding.cafeteria1.setGravity(Gravity.CENTER_HORIZONTAL);
                                binding.cafeteria2.setText(stringBuilder2.toString());
                                binding.cafeteria2.setGravity(Gravity.CENTER_HORIZONTAL);
                                binding.cafeteria3.setText(stringBuilder3.toString());
                                binding.cafeteria3.setGravity(Gravity.CENTER_HORIZONTAL);
                            });
                    }).start();
                    break;
                case R.id.settingA:
                    String text="";
                    String[] days ={"월요일","화요일","수요일","목요일","금요일"};
                    Calendar[] alarmTime = new Calendar[5];
                    for(int i = 0;i<5;i++){
                        alarmTime[i] = Calendar.getInstance();
                    }
                    for(int i = 2;i<7;i++){
                        alarmTime[i-2].set(Calendar.DAY_OF_WEEK,i);
                        if(mealtime[i-2] != -1){
                            alarmTime[i-2].set(Calendar.HOUR_OF_DAY,mealtime[i-2]);
                            alarmTime[i-2].set(Calendar.MINUTE, 0);
                            alarmTime[i-2].set(Calendar.SECOND, 0);
                            alarmTime[i-2].set(Calendar.MILLISECOND, 0);
                        }
                        else alarmTime[i-2].set(Calendar.YEAR,1970);
                    }
                    for(int i = 2;i<7;i++){
                        if (alarmTime[i-2].before(Calendar.getInstance())&&alarmTime[i-2].get(Calendar.DAY_OF_WEEK)==Calendar.getInstance().get(Calendar.DAY_OF_WEEK)||alarmTime[i-2].get(Calendar.DAY_OF_MONTH)<Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                            alarmTime[i-2].add(Calendar.DAY_OF_MONTH, 7);
                    }
                    for(int i =0;i<5;i++){
                        if(alarmTime[i].get(Calendar.YEAR)!=1970){
                            accessNotiM(alarmTime[i],20+alarmTime[i].get(Calendar.DAY_OF_WEEK));
                            text = text.concat(alarmTime[i].get(Calendar.DAY_OF_MONTH)+"일 "+days[i]+" "+alarmTime[i].get(Calendar.HOUR)+"시에 알람이 예약되었습니다.\n");
                        }
                    }
                    Toast.makeText(getContext(),text,Toast.LENGTH_LONG).show();
                    break;
                case R.id.deleteA:
                    Toast.makeText(getContext(),"평일 알람이 해제되었습니다.",Toast.LENGTH_LONG).show();
                    for(int i=2;i<7;i++){
                        deleteAlarm(20+i);
                    }
                    break;
                case R.id.alarmtest1m:
                    Calendar test1m = Calendar.getInstance();
                    test1m.set(Calendar.MINUTE,test1m.get(Calendar.MINUTE)+1);
                    test1m.set(Calendar.SECOND, 0);
                    test1m.set(Calendar.MILLISECOND, 0);
                    accessNotiM(test1m,20);
                    break;
                default: break;
            }
            return super.onOptionsItemSelected(menuItem);
        });
    }

    void accessNotiM(Calendar c,int day){
        Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
        alarmIntent.putExtra("requestCode",day);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext().getApplicationContext(), day, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(c.getTimeInMillis(),pendingIntent);
        if (alarmManager != null) {
            // 버전에 따라 다르게 구현
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.v("알람","if문"+String.valueOf(day));
                //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                alarmManager.setAlarmClock(ac,pendingIntent);
            }else { Log.v("알람","else 문 ");
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
    }
    public void deleteAlarm(int requestCode){
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent pendingIntent
                = PendingIntent.getBroadcast(getContext(), requestCode, intent, 0);
        AlarmManager manager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        manager = null;
        intent = null;
    }
}
