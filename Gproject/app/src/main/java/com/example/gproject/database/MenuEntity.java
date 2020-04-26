package com.example.gproject.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MenuEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    // 요일 1 : 월요일, 2 : 화요일, 3 : 수요일, 4 : 목요일, 5 : 금요일, 6 : 토요일
    private int day;
    // 식사시간 1 : 아침, 2 : 점심, 3 : 저녁
    private int mealtime;
    // 식당구분 1 : 학생회관식당, 2 : 교직원식당, 3 : 제2기숙사 식당
    private int cafeteria;
    // 메뉴 항목
    private String menuItem;

    public MenuEntity(int day, int mealtime, int cafeteria, String menuItem) {
        this.day = day;
        this.mealtime = mealtime;
        this.cafeteria = cafeteria;
        this.menuItem = menuItem;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMealtime() {
        return mealtime;
    }

    public void setMealtime(int mealtime) {
        this.mealtime = mealtime;
    }

    public int getCafeteria() {
        return cafeteria;
    }

    public void setCafeteria(int cafeteria) {
        this.cafeteria = cafeteria;
    }

    public String getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(String menuItem) {
        this.menuItem = menuItem;
    }

    @Override
    public String toString() {
        return "MenuEntity{" +
                "id=" + id +
                ", day=" + day +
                ", mealtime=" + mealtime +
                ", cafeteria=" + cafeteria +
                ", menuItem='" + menuItem + '\'' +
                '}';
    }
}