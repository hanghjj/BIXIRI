package com.example.gproject.thread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SubwayApiThread extends Thread {
    List<SubwayArrival> result;
    String station;
    Runnable runnable;

    public SubwayApiThread(List<SubwayArrival> result, String station, Runnable runnable) {
        this.result = result;
        this.station = station;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            // 현재 시간을 구한다
            Date currentTime = new Date(System.currentTimeMillis());

            // URL 연결
            String key = "4457594e5a73617a3130324567425a65";
            URL url = new URL("http://swopenapi.seoul.go.kr/api/subway/" + key + "/json/realtimeStationArrival/0/100/" + station);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == conn.HTTP_OK) {
                // URL에서 받아온 데이터를 String으로 변환
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuffer buffer = new StringBuffer(); // StringBuilder 대신 thread-safe한 StringBuffer 사용
                String str;
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                reader.close();

                // JSON 형태의 String을 파싱
                JSONArray arrivalList = new JSONObject(buffer.toString()).getJSONArray("realtimeArrivalList");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                for (int i = 0; i < arrivalList.length(); i++) {
                    JSONObject arrival = arrivalList.getJSONObject(i);

                    // 정보가 생성된 시간
                    Date receivedTime = simpleDateFormat.parse(arrival.getString("recptnDt"));
                    // 도착 예정 시간
                    // Api에서 제공된 도착 예정 시간에서 (현재 시간 - 정보가 생성된 시간)을 뺀다
                    int arrivalTime = (int) (arrival.getInt("barvlDt") - (currentTime.getTime() - receivedTime.getTime()) / 1000);

                    // 도착 예정 시간이 유효한 경우에만 데이터를 추가한다
                    if (arrivalTime > 0)
                        result.add(new SubwayArrival(arrivalTime, arrival.getString("bstatnNm"), arrival.getString("arvlMsg3"), arrival.getInt("subwayId")));
                }

                runnable.run();
            } else {
                System.out.println(conn.getResponseMessage());
            }
        } catch (IOException | JSONException | ParseException e) {
            System.out.println(e);
        }
    }

    public class SubwayArrival {
        int arrivalTime; // 도착 예정 시간
        String destination; // 열차 종착역 ~행
        String currentLocation; // 현재 열차 위치
        int line; // 열차의 노선

        public SubwayArrival(int arrivalTime, String destination, String currentLocation, int line) {
            this.arrivalTime = arrivalTime;
            this.destination = destination;
            this.currentLocation = currentLocation;
            this.line = line;
        }

        public int getArrivalTime() {
            return arrivalTime;
        }

        public String getDestination() {
            return destination;
        }

        public String getCurrentLocation() {
            return currentLocation;
        }

        public int getLine() {
            return line;
        }
    }
}
