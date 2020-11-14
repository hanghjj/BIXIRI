package com.bixiri.gproject.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.bixiri.gproject.R;

import java.util.ArrayList;
import java.util.List;

// SharedPreference를 싱글톤으로 관리하기 위한 클래스
// getter와 setter를 사용시에 R.string.id를 key값으로 쓴다
public class AppSharedPreference {
    private static AppSharedPreference INSTANCE;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static Context context;

    // 생성자를 private하여 다른 곳에서 객체를 생성하지 못하게끔 한다.
    private AppSharedPreference(Context context) {
        AppSharedPreference.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.sharedPreferences_name), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static AppSharedPreference getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AppSharedPreference(context);
        }
        return INSTANCE;
    }

    public int getInt(int id, int defaultValue) {
        return sharedPreferences.getInt(context.getString(id), defaultValue);
    }

    public String getString(int id, String defaultValue) {
        return sharedPreferences.getString(context.getString(id), defaultValue);
    }

    public boolean getBoolean(int id, boolean defaultValue) {
        return sharedPreferences.getBoolean(context.getString(id), defaultValue);
    }

    public List<Integer> getIntList(int id, int defaultValue) {
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i <= sharedPreferences.getInt(context.getString(id) + 0, 0); i++) {
            result.add(sharedPreferences.getInt(context.getString(id) + i, defaultValue));
        }
        return result;
    }

    public void putInt(int id, int value) {
        editor.putInt(context.getString(id), value).commit();
    }

    public void putString(int id, String value) {
        editor.putString(context.getString(id), value).commit();
    }

    public void putBoolean(int id, boolean value) {
        editor.putBoolean(context.getString(id), value).commit();
    }

    // List<Integer>를 저장
    // 0번째 원소에는 크기 저장
    // 1 ~ size 까지에는 0 ~ (size - 1)의 정보 저장
    public void putIntList(int id, List<Integer> value) {
        // 기존 List 제거
        for (int i = 0; i <= sharedPreferences.getInt(context.getString(id) + 0, 0); i++) {
            editor.remove(context.getString(id) + i);
        }

        // 새로운 List 삽입
        editor.putInt(context.getString(id) + 0, value.size());
        for (int i = 0; i < value.size(); i++) {
            editor.putInt(context.getString(id) + (i + 1), value.get(i));
        }
        editor.commit();
    }

    public void clearAll() {
        editor.clear();
        editor.commit();
    }
}
