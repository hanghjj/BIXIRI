package com.bixiri.gproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.bixiri.gproject.databinding.ActivityLocationBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;


import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesListener;
import noman.googleplaces.Place;
import noman.googleplaces.PlacesException;

public class ActLocation extends AppCompatActivity implements PlacesListener, OnMapReadyCallback {
    private ActivityLocationBinding binding;
    private gpsTracker tracker;
    String exactadd; //주석처리된 부분을 위한 변수
    String realadd= " "; //주석처리된 부분을 위한 변수
    double lati;
    double longi;
    String pid;
    List<Marker> previous_marker = new ArrayList<>();
    HashMap<String,String> pinfo = new HashMap<String,String>();
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //뷰 바인딩 끝

        SupportMapFragment mapF = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapF.getMapAsync(this);


        //위도와 경도
        tracker = new gpsTracker(ActLocation.this);
        lati = tracker.getLatitude();
        longi = tracker.getLongitude();
        LatLng currentPosition = new LatLng(lati,longi);
        binding.findrest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showPlaceInformation(currentPosition);
            }
        });
       /*
       //위도 경도 세팅 끝
        try{//구해진 위도,경도 값으로 주소를 도출하고 시,구,동에 대한 정보만 담도록 스트링 가공
            List<Address> geoResult = geocoder.getFromLocation(lati,longi,1);
            exactadd = geoResult.get(0).getAddressLine(0);
            StringTokenizer st = new StringTokenizer(exactadd," ");
            String temp=null;

            while(st.hasMoreTokens()){
                temp = st.nextToken();
                if(temp.charAt(temp.length()-1)=='구'||temp.charAt(temp.length()-1)=='시'||temp.charAt(temp.length()-1)=='동'){
                    realadd = realadd.concat(temp+" ");
                }

            }
            binding.add1.setText(realadd);
        }catch (IOException e){
            e.printStackTrace();
        }
*/

//네이버 API를 이용해 검색하기
        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                //String result = naverapi(realadd);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
              }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });thread.start();*/
    }
    // OnMapReadyCallback override
    @Override
    public  void onMapReady(final GoogleMap googleMap){
        map = googleMap;
        LatLng myLocate = new LatLng(lati,longi);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLocate);
        markerOptions.title("현재 위치");
        markerOptions.snippet("Your Location");
        map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocate,15));
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent= new Intent(getBaseContext(),PlaceinfoActivity.class);
                String t = marker.getTitle();
                String a = marker.getSnippet();
                intent.putExtra("title",t);
                intent.putExtra("address",a);
                String temp = (String) pinfo.get(t);
                intent.putExtra("pid",temp);
                startActivity(intent);
            }
        });
    }
    //Places override
    @Override
    public void onPlacesFailure(PlacesException e){}
    @Override
    public void onPlacesStart(){}
    @Override
    public void onPlacesSuccess(List<Place> places){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(noman.googleplaces.Place place : places){
                    LatLng latLng = new LatLng(place.getLatitude(),place.getLongitude());
                    String markerSnippet = getCurrentAddress(latLng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    markerOptions.snippet(markerSnippet);
                    pid = place.getPlaceId();
                    pinfo.put(place.getName(),pid);
                    Marker item = map.addMarker(markerOptions);
                    previous_marker.add(item);
                }
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();;
                previous_marker.addAll(hashSet);
            }
        });
    }

    @Override
    public void onPlacesFinished(){}
    public void showPlaceInformation(LatLng location){
        map.clear();
        if(previous_marker!=null) previous_marker.clear();
        new NRPlaces.Builder().listener(ActLocation.this).key("AIzaSyC_5YmCtnBm4OK0BvpVjVYM4AOf4IE_0rw")
                .latlng(location.latitude,location.longitude)
                .radius(500)
                .type(PlaceType.RESTAURANT)
                .build().execute();
    }
    public String getCurrentAddress(LatLng latlng){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> geoResult;
        try{
            geoResult = geocoder.getFromLocation(latlng.latitude,latlng.longitude,1);
            }catch (IOException e){
            Toast.makeText(this, "지오코더 서비스 사용 불가", Toast.LENGTH_SHORT).show();
            return "지오코더 서비스 사용불가";
            }catch (IllegalArgumentException i){
            Toast.makeText(this,"잘못된 GPS 좌표",Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
            }
            if(geoResult ==null||geoResult.size()==0){
                return "주소 미발견";
            }else{
                Address address = geoResult.get(0);
                return address.getAddressLine(0).toString();
            }
        }
/*
 //네이버 API 사용 함수
    public String naverapi(String key){
        String clientId = "MfikIi83tGt5CI313IdT";
        String clientSecret = "B96Golukkq";
        StringBuffer buff = new StringBuffer();
        try{
            String temp = URLEncoder.encode(key ,"UTF-8");
            String queryURL = "https://openapi.naver.com/v1/search/blog.xml?query="+temp+"&display=5&start = 1&sort = random";
            URL url = new URL(queryURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Naver-Client-Id",clientId);
            connection.setRequestProperty("X-Naver-Client-Secret",clientSecret);
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser parser = fac.newPullParser();
            String tag;
            parser.setInput(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            parser.next();
            int event = parser.getEventType();
            while(event!=XmlPullParser.END_DOCUMENT){
                switch (event){
                    case XmlPullParser.START_TAG:
                        tag = parser.getName();
                            if(tag.equals("total")) {buff.append("개수 : ");
                                parser.next();
                                buff.append(parser.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\\\\\s[a-zA-Z]*=[^>]*)?(\\\\\\\\s)*(/)?>",""));
                                buff.append("\n");}
                            else if(tag.equals("title")){
                                buff.append("제목 : ");
                                parser.next();
                                buff.append(parser.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\\\\\s[a-zA-Z]*=[^>]*)?(\\\\\\\\s)*(/)?>",""));
                                buff.append("\n");
                            }
                            else if(tag.equals("address")){
                                buff.append("내용 : ");
                                parser.next();
                                buff.append(parser.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\\\\\s[a-zA-Z]*=[^>]*)?(\\\\\\\\s)*(/)?>",""));
                                buff.append("\n");
                            }
                            break;
                }
                event = parser.next();
            }
        }catch (Exception e){
            return e.toString();
        }
        return buff.toString();
    }*/
}

