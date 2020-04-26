package com.example.gproject.thread;

import com.example.gproject.database.menu.MenuDAO;
import com.example.gproject.database.menu.MenuEntity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuCrawlingThread {
    // 학교 식단을 크롤링하여 DB에 저장
    public void getMenuFromWeb(MenuDAO menuDAO) {
        // Thread 생성해서 코드 실행
        new Thread(() -> {
            try {
                int index = 0;
                int mealtime; // 식사장소 및 시간
                int day; // 요일 변수 1:월 2:화 3:수 4:목 5:금 6:토
                String[] menus;
                List<MenuEntity> insertList = new ArrayList<MenuEntity>(); // DB에 저장할 리스트

                // 학교 식단 메뉴 가져옴
                Document doc = Jsoup.connect("http://apps.hongik.ac.kr/food/food.php").get();
                Elements menuAll = doc.select(".daily-menu");


                // 각 daily-menu 에 대해 반복
                for (Element menu : menuAll) {
                    mealtime = index / 6;
                    day = (index % 6) + 1;
                    menus = menu.select("p").html().split("<br>");
                    switch (mealtime) {
                        case 0: // 학생회관 점심
                            for (String menuItem : menus) {
                                insertList.add(new MenuEntity(day, 2, 1, menuItem));
                            }
                            break;
                        case 1: // 학생회관 저녁
                            for (String menuItem : menus) {
                                insertList.add(new MenuEntity(day, 3, 1, menuItem));
                            }
                            break;
                        case 2: // 교직원식당 점심
                            for (String menuItem : menus) {
                                insertList.add(new MenuEntity(day, 2, 2, menuItem));
                            }
                            break;
                        case 3: // 교직원식당 저녁
                            for (String menuItem : menus) {
                                insertList.add(new MenuEntity(day, 3, 2, menuItem));
                            }
                            break;
                        case 4: // 제2기숙사식당 아침
                            for (String menuItem : menus) {
                                insertList.add(new MenuEntity(day, 1, 3, menuItem));
                            }
                            break;
                        case 5: // 제2기숙사식당 점심
                            for (String menuItem : menus) {
                                insertList.add(new MenuEntity(day, 2, 3, menuItem));
                            }
                            break;
                        case 6: // 제2기숙사식당 점심, 공백
                            break;
                        case 7: // 제2기숙사식당 저녁
                            for (String menuItem : menus) {
                                insertList.add(new MenuEntity(day, 3, 3, menuItem));
                            }
                            break;
                    }
                    index++;
                }

                menuDAO.deleteAll(); // 기존의 정보 모두 제거
                menuDAO.insertAll(insertList); // DB에 데이터 삽입
            } catch (IOException e) {
                System.out.println("Error");
            }
        }).start();
    }
}
