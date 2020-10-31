package com.bixiri.gproject.thread;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;

public class BusApiThread extends Thread {
    private String stId;
    private String busRouteId;
    private String ord;
    private Runnable runnable;
    private List<BusArrival> result;

    public BusApiThread(List<BusArrival> result, String stId, String busRouteId, String ord, @Nullable Runnable runnable) {
        this.result = result;
        this.stId = stId;
        this.busRouteId = busRouteId;
        this.ord = ord;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        new Thread(() -> {
            String key;
            try {
                // 현재 시간을 구한다
                Date currentTime = new Date(System.currentTimeMillis());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.getDefault());
                Date receivedTime = new Date(System.currentTimeMillis()); // 도착 정보가 생성된 시간을 구한다

                // 연결
                key = URLEncoder.encode("N8XfKTMBGcDvqnpIhhinKgbaEZ4S9ZZkKHD7fp9rN4wLVazAbrFQYd5LCyVLsgJJk8szqKL1CEKPao47M1GGWQ==", "UTF-8");
                String urlBuilder = "http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRoute" + "?ServiceKey=" + key +
                        "&stId=" + stId +
                        "&busRouteId=" + busRouteId +
                        "&ord=" + ord;
                Log.i("BusApi", urlBuilder);
                URL url = new URL(urlBuilder);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = parserCreator.newPullParser();
                    parser.setInput(url.openStream(), null);

                    int parserEvent = parser.getEventType();
                    BusArrival busArrival1 = new BusArrival();
                    BusArrival busArrival2 = new BusArrival();

                    String busType, reRide, arrivalText, stationNm;
                    int arrivalTime, minute, second;
                    while (parserEvent != XmlPullParser.END_DOCUMENT) {
                        if (parserEvent == XmlPullParser.START_TAG) { //parser가 시작 태그를 만나면 실행
                            switch (parser.getName()) {
                                case "busType1": // 차량유형
                                    parser.next();
                                    switch (Integer.parseInt(parser.getText())) {
                                        case 1:
                                            busType = "저상버스";
                                            break;
                                        case 2:
                                            busType = "굴절버스";
                                            break;
                                        default:
                                            busType = "일반버스";
                                    }
                                    busArrival1.setBusType(busType);
                                    break;
                                case "busType2": // 차량유형
                                    parser.next();
                                    switch (Integer.parseInt(parser.getText())) {
                                        case 1:
                                            busType = "저상버스";
                                            break;
                                        case 2:
                                            busType = "굴절버스";
                                            break;
                                        default:
                                            busType = "일반버스";
                                    }
                                    busArrival2.setBusType(busType);
                                    break;
                                case "mkTm": // 데이터 제공시각
                                    parser.next();
                                    receivedTime = simpleDateFormat.parse(parser.getText());
                                    break;
                                case "reride_Num1": // 재차 인원
                                    parser.next();
                                    switch (Integer.parseInt(parser.getText())) {
                                        case 3:
                                            reRide = "여유";
                                            break;
                                        case 4:
                                            reRide = "보통";
                                            break;
                                        case 5:
                                            reRide = "혼잡";
                                            break;
                                        default:
                                            reRide = "미제공";
                                    }
                                    busArrival1.setRerideNum(reRide);
                                    break;
                                case "reride_Num2": // 재차 인원
                                    parser.next();
                                    switch (Integer.parseInt(parser.getText())) {
                                        case 3:
                                            reRide = "여유";
                                            break;
                                        case 4:
                                            reRide = "보통";
                                            break;
                                        case 5:
                                            reRide = "혼잡";
                                            break;
                                        default:
                                            reRide = "미제공";
                                    }
                                    busArrival2.setRerideNum(reRide);
                                    break;
                                case "sectOrd1": // 현재 위치한 정류소의 순번
                                    parser.next();
                                    busArrival1.setOrdDiff(Integer.parseInt(ord) - Integer.parseInt(parser.getText()));
                                    break;
                                case "sectOrd2":
                                    parser.next();
                                    busArrival2.setOrdDiff(Integer.parseInt(ord) - Integer.parseInt(parser.getText()));
                                    break;
                                case "staOrd": // 요청한 정류소 순번
                                    parser.next();
                                    System.out.println(parser.getText());
                                    break;
                                case "stationNm1": // 현재 위치한 정류소 이름
                                    parser.next();
                                    stationNm = parser.getText();
                                    if (stationNm.equals(" ")) {
                                        stationNm = "차고지";
                                    }
                                    busArrival1.setStationNm(stationNm);
                                    break;
                                case "stationNm2":
                                    parser.next();
                                    stationNm = parser.getText();
                                    if (stationNm.equals(" ")) {
                                        stationNm = "차고지";
                                    }
                                    busArrival2.setStationNm(stationNm);
                                    break;
                                case "traTime1": // 도착예정까지 남은 시간(초)
                                    parser.next();
                                    arrivalTime = (int) (Integer.parseInt(parser.getText()) - (currentTime.getTime() - receivedTime.getTime()) / 1000);
                                    if (arrivalTime < 0) {
                                        arrivalTime = 0;
                                    }
                                    minute = (int) arrivalTime / 60;
                                    second = arrivalTime % 60;
                                    if (second == 0) {
                                        arrivalText = minute + "분";
                                    } else if (minute == 0) {
                                        arrivalText = second + "초";
                                    } else {
                                        arrivalText = minute + "분 " + second + "초";
                                    }
                                    busArrival1.setTraTime(arrivalText);
                                    break;
                                case "traTime2":
                                    parser.next();
                                    arrivalTime = (int) (Integer.parseInt(parser.getText()) - (currentTime.getTime() - receivedTime.getTime()) / 1000);
                                    if (arrivalTime < 0) {
                                        arrivalTime = 0;
                                    }
                                    minute = (int) arrivalTime / 60;
                                    second = arrivalTime % 60;
                                    if (second == 0) {
                                        arrivalText = minute + "분";
                                    } else if (minute == 0) {
                                        arrivalText = second + "초";
                                    } else {
                                        arrivalText = minute + "분 " + second + "초";
                                    }
                                    busArrival2.setTraTime(arrivalText);
                                    break;
                            }
                        }
                        parserEvent = parser.next();
                    }

                    result.add(busArrival1);
                    result.add(busArrival2);
                }

                conn.disconnect();
                runnable.run();
            } catch (IOException | XmlPullParserException | ParseException ex) {
                ex.printStackTrace();
            }
        }).start();

//        runnable.run();
    }

    public class BusArrival {
        String stationNm; // 현재 위치
        int ordDiff; // N정거장 전
        String rerideNum; // 혼잡도
        String busType; // 차량유형
        String traTime; // 도착예정시간

        public String getStationNm() {
            return stationNm;
        }

        void setStationNm(String stationNm) {
            this.stationNm = stationNm;
        }

        public int getOrdDiff() {
            return ordDiff;
        }

        void setOrdDiff(int ordDiff) {
            this.ordDiff = ordDiff;
        }

        public String getRerideNum() {
            return rerideNum;
        }

        void setRerideNum(String rerideNum) {
            this.rerideNum = rerideNum;
        }

        public String getBusType() {
            return busType;
        }

        void setBusType(String busType) {
            this.busType = busType;
        }

        public String getTraTime() {
            return traTime;
        }

        void setTraTime(String traTime) {
            this.traTime = traTime;
        }
    }
}
