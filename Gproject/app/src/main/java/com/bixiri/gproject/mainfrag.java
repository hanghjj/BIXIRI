package com.bixiri.gproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bixiri.gproject.database.AppDatabase;
import com.bixiri.gproject.database.AppSharedPreference;
import com.bixiri.gproject.databinding.MainfragBinding;
import com.bixiri.gproject.databinding.RecyclerviewItemArrival42dpBinding;
import com.bixiri.gproject.thread.BusApiThread;
import com.bixiri.gproject.thread.SubwayApiThread;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class mainfrag extends Fragment {
    private MainfragBinding binding;
    private View view;
    AppDatabase db;
    AppSharedPreference pref;
    SubwayApiThread subwayApiThread;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedinstancestate) {
        binding = MainfragBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getInstance(getActivity());
        pref = AppSharedPreference.getInstance(getActivity());

        update();

        binding.buttonMainTime.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TimeTable2.class));
        });
        binding.buttonMainMap.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ActLocation.class));
        });

        binding.materialToolbarMain.setOnMenuItemClickListener(menuItem -> {
            // 새로고침 버튼
            if (menuItem.getItemId() == R.id.menuItem_main_refresh) {
                update();
                return true;
            }
            return false;
        });
    }

    void update() {
        {
            String departureStation = pref.getString(R.string.key_departureStation, "");
            String arrivalStation = pref.getString(R.string.key_arrivalStation, "");
            // SubwayApiThread를 통해서 받아올 리스트
            List<SubwayApiThread.SubwayArrival> arrivalList = new ArrayList<>();
            // recyclerView에 띄울 리스트

            // SubwayApiThread에서 리스트 받아온 이후로 실행할 내용
            Runnable afterRun = () -> {
                // recyclerView에 부착하는건 메인 Thread에서만 가능하다
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (getActivity() != null)
                    getActivity().runOnUiThread(() -> {
                        binding.recyclerviewMainSubwayResult.setLayoutManager(new LinearLayoutManager(getActivity()));
                        final RecyclerViewAdapterSubwayResult adapter = new RecyclerViewAdapterSubwayResult(arrivalList);
                        binding.recyclerviewMainSubwayResult.setAdapter(adapter);
                    });
            };

            // Thread를 실행한다
            subwayApiThread = new SubwayApiThread(getContext(), arrivalList, departureStation, arrivalStation, afterRun);
            subwayApiThread.start();
        }

        {
            String busStId = pref.getString(R.string.key_busStId, "");
            String busRouteId = pref.getString(R.string.key_busRouteId, "");
            String busOrd = pref.getString(R.string.key_busOrg, "");
            // BusApiThread를 통해서 받아올 리스트
            List<BusApiThread.BusArrival> arrivalList = new ArrayList<>();

            Runnable afterRun = () -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (getActivity() != null)
                    getActivity().runOnUiThread(() -> {
                        binding.recyclerviewMainBusResult.setLayoutManager(new LinearLayoutManager(getActivity()));
                        final RecyclerViewAdapterBusResult adapter = new RecyclerViewAdapterBusResult(arrivalList);
                        binding.recyclerviewMainBusResult.setAdapter(adapter);
                    });
            };

            BusApiThread busApiThread = new BusApiThread(arrivalList, busStId, busRouteId, busOrd, afterRun);
            busApiThread.start();
        }

        {
            // 현재 시간을 구한다
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat dayFormat = new SimpleDateFormat("u", Locale.getDefault());
            SimpleDateFormat hourFormat = new SimpleDateFormat("H", Locale.getDefault());
            // 현재 요일
            int day = Integer.parseInt(dayFormat.format(date));
            // 현재 시간
            int hour = Integer.parseInt(hourFormat.format(date));

            List<Menu> menuList = new ArrayList<>();

            new Thread(() -> {
                List<String> menuItems = db.menuDAO().findMenu(day, 2, 1);
                StringBuilder stringBuilder1 = new StringBuilder();
                for (String menu : menuItems) {
                    stringBuilder1.append(menu).append(" ");
                }
                menuItems = db.menuDAO().findMenu(day, 2, 2);
                StringBuilder stringBuilder2 = new StringBuilder();
                for (String menu : menuItems) {
                    stringBuilder2.append(menu).append(" ");
                }

                menuItems = db.menuDAO().findMenu(day, 2, 3);
                StringBuilder stringBuilder3 = new StringBuilder();
                for (String menu : menuItems) {
                    stringBuilder3.append(menu).append(" ");
                }

                menuList.add(new Menu("학생회관", stringBuilder1.toString()));
                menuList.add(new Menu("교직원식당", stringBuilder2.toString()));
                menuList.add(new Menu("제2기숙사 식당", stringBuilder3.toString()));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Main Thread에서 UI 변경
                if (getActivity() != null)
                    getActivity().runOnUiThread(() -> {
                        binding.recyclerviewMainMenu.setLayoutManager(new LinearLayoutManager(getActivity()));
                        final RecyclerViewAdapterMenu adapter = new RecyclerViewAdapterMenu(menuList);
                        binding.recyclerviewMainMenu.setAdapter(adapter);
                    });
            }).start();
        }

        {
            String[] AvF = {"패딩, 두꺼운 코트, 목도리 + 기모제품",
                    "코트, 히트텍, 니트, 청바지, 레깅스",
                    "자켓, 트렌치코트, 야상, 니트, 스타킹, 청바지, 면바지",
                    "자켓, 가디건, 야상, 맨투맨, 니트, 스타킹, 청바지, 면바지",
                    "얇은 니트, 가디건, 맨투맨, 얇은 자켓, 면바지, 청바지",
                    "얇은 가디건, 긴팔티, 면바지, 청바지",
                    "반팔, 얇은 셔츠, 반바지, 면바지",
                    "민소매, 반팔, 반바지, 치마"};

            new Thread(() -> {
                try {
                    Document doc1 = Jsoup.connect("https://search.naver.com/search.naver?sm=tab_hty.top&where=nexearch&query=%EC%84%9C%EC%9A%B8%EB%82%A0%EC%94%A8").get();
                    Elements temp_contents = doc1.select(".merge");
                    Elements rain_contents = doc1.select(".rain_rate > .num");

                    //오늘 강수량 측정
                    String rain_text = rain_contents.text();
                    StringTokenizer Rtoken = new StringTokenizer(rain_text);
                    String MornRR = Rtoken.nextToken(" ");
                    String AftRR = Rtoken.nextToken(" ");

                    //온도 측정
                    String text = temp_contents.text();
                    text = text.replace("˚", " ");
                    text = text.replace("/", " ");
                    StringTokenizer token = new StringTokenizer(text);
                    String mintempT = token.nextToken(" ");
                    String maxtempT = token.nextToken(" ");
                    double averageT = (Double.parseDouble(mintempT) * 1.2 + Double.parseDouble(maxtempT)) / 2;
                    String AvgT = String.format("%.1f", averageT) + "˚C";

                    //옷차림 정하기
                    String TodayF;
                    if (averageT <= 4) {
                        TodayF = AvF[0];
                    } else if (averageT > 4 && averageT <= 8) {
                        TodayF = AvF[1];
                    } else if (averageT > 8 && averageT <= 11) {
                        TodayF = AvF[2];
                    } else if (averageT > 11 && averageT <= 16) {
                        TodayF = AvF[3];
                    } else if (averageT > 16 && averageT <= 19) {
                        TodayF = AvF[4];
                    } else if (averageT > 19 && averageT <= 22) {
                        TodayF = AvF[5];
                    } else if (averageT > 22 && averageT <= 27) {
                        TodayF = AvF[6];
                    } else {
                        TodayF = AvF[7];
                    }

                    if (getActivity() != null)
                        getActivity().runOnUiThread(() -> {
                            //우산 여부
                            if (Double.parseDouble(MornRR) >= 60 || Double.parseDouble(AftRR) >= 60) {
                                binding.textViewMainWeatherTitle.setText("평균온도: " + AvgT + " [우산 필요]");
                            } else {
                                binding.textViewMainWeatherTitle.setText("평균온도: " + AvgT);
                            }
                            binding.textViewMainWeatherContent.setText(TodayF);
                        });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    // 메뉴 리스트
    class Menu {
        String cafeteria;
        String menus;

        public Menu(String cafeteria, String menus) {
            this.cafeteria = cafeteria;
            this.menus = menus;
        }

        public String getCafeteria() {
            return cafeteria;
        }

        public void setCafeteria(String cafeteria) {
            this.cafeteria = cafeteria;
        }

        public String getMenus() {
            return menus;
        }

        public void setMenus(String menus) {
            this.menus = menus;
        }
    }

    // RecyclerView
    class RecyclerViewAdapterSubwayResult extends RecyclerView.Adapter<RecyclerViewAdapterSubwayResult.ViewHolder> {
        private List<SubwayApiThread.SubwayArrival> arrivalList;

        class ViewHolder extends RecyclerView.ViewHolder {
            RecyclerviewItemArrival42dpBinding binding;

            ViewHolder(RecyclerviewItemArrival42dpBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

        RecyclerViewAdapterSubwayResult(List<SubwayApiThread.SubwayArrival> arrivalList) {
            this.arrivalList = arrivalList;
        }

        @NonNull
        @Override
        public RecyclerViewAdapterSubwayResult.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerviewItemArrival42dpBinding binding = RecyclerviewItemArrival42dpBinding.
                    inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new RecyclerViewAdapterSubwayResult.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapterSubwayResult.ViewHolder holder, int position) {
            int arrivalTime = arrivalList.get(position).getArrivalTime();
            int minute = (int) arrivalTime / 60;
            int second = arrivalTime % 60;
            String arrivalText; // 00분 00초 후 도착으로 표시
            if (second == 0) {
                arrivalText = minute + "분";
            } else if (minute == 0) {
                arrivalText = second + "초";
            } else {
                arrivalText = minute + "분 " + second + "초";
            }
            // 도착시간: 00시 00분 00초로 표시하기 위한 옵션
//            Date arrivalDate = new Date(System.currentTimeMillis() + arrivalTime * 1000);
//            SimpleDateFormat format = new SimpleDateFormat("hh시 mm분 ss초", Locale.getDefault());
//            arrivalText = format.format(arrivalDate);

            holder.binding.textViewRecyclerviewItemArrival.setText(getString(R.string.recyclerview_item_arrival,
                    arrivalList.get(position).getCurrentLocation(),
                    arrivalText));
        }

        @Override
        public int getItemCount() {
            return arrivalList.size();
        }
    }

    // RecyclerView
    class RecyclerViewAdapterBusResult extends RecyclerView.Adapter<RecyclerViewAdapterBusResult.ViewHolder> {
        List<BusApiThread.BusArrival> arrivalList;

        RecyclerViewAdapterBusResult(List<BusApiThread.BusArrival> arrivalList) {
            this.arrivalList = arrivalList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            RecyclerviewItemArrival42dpBinding binding;

            ViewHolder(RecyclerviewItemArrival42dpBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

        @NonNull
        @Override
        public RecyclerViewAdapterBusResult.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerviewItemArrival42dpBinding binding = RecyclerviewItemArrival42dpBinding.
                    inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new RecyclerViewAdapterBusResult.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapterBusResult.ViewHolder holder, int position) {
            holder.binding.textViewRecyclerviewItemArrival.setText(getString(R.string.recyclerview_item_arrival,
                    arrivalList.get(position).getStationNm(), arrivalList.get(position).getTraTime()));
        }

        @Override
        public int getItemCount() {
            return arrivalList.size();
        }
    }

    class RecyclerViewAdapterMenu extends RecyclerView.Adapter<RecyclerViewAdapterMenu.ViewHolder> {
        List<Menu> menuList;

        RecyclerViewAdapterMenu(List<Menu> menuList) {
            this.menuList = menuList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            RecyclerviewItemArrival42dpBinding binding;

            ViewHolder(RecyclerviewItemArrival42dpBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerviewItemArrival42dpBinding binding = RecyclerviewItemArrival42dpBinding.
                    inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.binding.textViewRecyclerviewItemArrival.setText(getString(R.string.recyclerview_item_menu,
                    menuList.get(position).getCafeteria(), menuList.get(position).getMenus()));
        }

        @Override
        public int getItemCount() {
            return menuList.size();
        }
    }
}
