package com.example.gproject.thread;

import android.content.Context;

import com.example.gproject.R;
import com.example.gproject.database.AppDatabase;
import com.example.gproject.database.AppSharedPreference;
import com.example.gproject.database.subwaystation.SubwayStationDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SubwayApiThread extends Thread {
    private List<SubwayArrival> result;
    private String departureStation;
    private String arrivalStation;
    private Runnable runnable;
    private AppDatabase db;
    private SubwayStationDAO table;
    private AppSharedPreference pref;

    public SubwayApiThread(Context context, List<SubwayArrival> result, String departureStation, String arrivalStation, Runnable runnable) {
        this.result = result;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.runnable = runnable;
        db = AppDatabase.getInstance(context);
        table = db.subwayStationDAO();
        pref = AppSharedPreference.getInstance(context);
    }

    @Override
    public void run() {
        try {
            // 현재 시간을 구한다
            Date currentTime = new Date(System.currentTimeMillis());

            // URL 연결
            String key = "4457594e5a73617a3130324567425a65";
            URL url = new URL("http://swopenapi.seoul.go.kr/api/subway/" + key + "/json/realtimeStationArrival/0/100/" + departureStation);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // URL에서 받아온 데이터를 String으로 변환
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuffer buffer = new StringBuffer(); // StringBuilder 대신 thread-safe한 StringBuffer 사용
                String str;
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                reader.close();

                // 출발역에서 도착역으로 가는 지하철 노선 (한개일수도 여러개일수도)
                List<Integer> lineList = pref.getIntList(R.string.key_selectedLines, 0);
                // 출발역에서 도착역으로 가는 지하철 노선 (한개일수도 여러개일수도)
                List<Integer> selectedStation = pref.getIntList(R.string.key_selectedStations, 0);
                // JSON 형태의 String을 파싱
                JSONArray arrivalList = new JSONObject(buffer.toString()).getJSONArray("realtimeArrivalList");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.getDefault());
                int arrivalTime, subwayId, currentStationId, currentLineId, wayTo;
                for (int i = 0; i < arrivalList.length(); i++) {
                    JSONObject arrival = arrivalList.getJSONObject(i);

                    // 정보가 생성된 시간
                    Date receivedTime = simpleDateFormat.parse(arrival.getString("recptnDt"));
                    // 도착 예정 시간
                    // Api에서 제공된 도착 예정 시간에서 (현재 시간 - 정보가 생성된 시간)을 뺀다
                    arrivalTime = (int) (arrival.getInt("barvlDt") - (currentTime.getTime() - receivedTime.getTime()) / 1000);
                    // 들어오는 열차 노선 번호
                    subwayId = arrival.getInt("subwayId");

                    // 도착 예정 시간이 유효한 경우에만 데이터를 추가한다
                    if (arrivalTime > 0) {
                        // IF : 출발역과 도착역이 동일하게 설정되어 있는가
                        // TRUE : 모든 도착 데이터를 보여준다
                        // FALSE : 해당 노선에 있는 데이터만 보여준다
                        if (departureStation.equals(arrivalStation)) {
                            result.add(new SubwayArrival(arrivalTime, arrival.getString("bstatnNm"), arrival.getString("arvlMsg3"), subwayId));
                        } else {
                            for (int j = 0; j < lineList.size(); j++) {
                                currentLineId = selectedStation.get(j);
                                currentStationId = selectedStation.get(j);
                                wayTo = table.getIdWithNameAndLine(arrivalStation, currentLineId) - currentStationId;
                                if (lineList.get(j) == subwayId && (table.getIdWithNameAndLine(arrival.getString("bstatnNm"), currentLineId) - currentStationId) * wayTo >= 0) {
                                    result.add(new SubwayArrival(arrivalTime, arrival.getString("bstatnNm"), arrival.getString("arvlMsg3"), subwayId));
                                }
                            }
                        }
                    }
                }

                conn.disconnect();
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

        SubwayArrival(int arrivalTime, String destination, String currentLocation, int line) {
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
