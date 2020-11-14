package com.bixiri.gproject;

import android.os.Bundle;
import android.util.Log;

import com.bixiri.gproject.databinding.ActivityPlaceinfoBinding;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

import androidx.appcompat.app.AppCompatActivity;


public class PlaceinfoActivity extends AppCompatActivity {
    String title = "";
    String title2 = "";
    String add = "";
    private String starP = "";
    private String numReview = "";
    String jsonurl;
    String jsonstring = "";
    String pid;
    String startemp = "";
    String reviewtemp = "";
    String reviews = "";

    private ActivityPlaceinfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceinfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle info = getIntent().getExtras();
        if (info == null) title = "error";
        else {
            title = info.getString("title");
            add = info.getString("address");
            pid = info.getString("pid");
            if (add.equals("주소 미발견"))
                add = "대한민국";
        }
        binding.basic.setText(title.concat("에 관한 별점/리뷰 정보"));
        jsonurl = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + pid + "&key=AIzaSyC_5YmCtnBm4OK0BvpVjVYM4AOf4IE_0rw";
        Log.d("zz", jsonurl);
        new Thread(() -> {
            try {
                URL url = new URL(jsonurl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String temp = null;
                while ((temp = bufferedReader.readLine()) != null) {
                    jsonstring += temp;
                    if (temp.contains("rating")) startemp += temp;
                    if (temp.contains("user_ratings_total")) reviewtemp += temp;
                    if (temp.contains("text") || temp.contains("author_name") || temp.contains("rating")) reviews = reviews.concat(temp + "\n");
                    if (temp.contains("text")) reviews = reviews.concat("\n");

                }
                startemp = startemp.replace("\"rating\" : ", "");
                startemp = startemp.replace("\"user_ratings_total\" : ", "");
                reviewtemp = reviewtemp.replace("\"user_ratings_total\" : ", "");
                numReview = reviewtemp.replace(",", "");
                reviews = reviews.replace("\"weekday_text\" : [", "");
                reviews = reviews.replace("\"author_name\"", "⩥리뷰 작성자");
                reviews = reviews.replace("\"text\"", "⩥리뷰 내용");
                reviews = reviews.replace("\\n", "\n");
                reviews = reviews.replace("\"rating\"", "⩥별점 ");
                reviews = reviews.replace(",", "");
                reviews = reviews.replace("\"user_ratings_total\"", "⩥총 리뷰 개수");
                reviews = reviews.replace("\"", "");

                StringTokenizer st = new StringTokenizer(startemp, ",");
                int cnt = 0;
                while (st.hasMoreTokens()) {
                    if (cnt == 0) starP = st.nextToken();
                    else st.nextToken();
                    cnt++;
                }
                if (starP.equals("")) starP = "평가 없음";
                else starP = starP.concat(" / 5.0");
                if (numReview.equals("")) numReview = "리뷰 없음";
            } catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                binding.star.setText("별점 : ".concat(starP));
                binding.review.setText("리뷰 개수 : ".concat(numReview));
                binding.reviews.setText(reviews);
            });
        }).start();

    }

}