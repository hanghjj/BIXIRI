package com.example.gproject.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {MenuEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    // 싱글톤
    private static AppDatabase INSTANCE;

    public abstract MenuDAO menuDAO();

    // DB 객체생성 가져오기
    public static AppDatabase getAppDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "bixiri-db").build();
        }
        return INSTANCE;
    }

    // DB 객체제거
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
