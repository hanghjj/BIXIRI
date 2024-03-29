package com.bixiri.gproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bixiri.gproject.database.AppDatabase;
import com.bixiri.gproject.database.AppSharedPreference;
import com.bixiri.gproject.database.busstop.BusStopEntity;
import com.bixiri.gproject.database.subwaystation.SubwayStationEntity;
import com.bixiri.gproject.databinding.ActivityOption1Binding;
import com.bixiri.gproject.databinding.RecyclerviewItemBusArrivalBinding;
import com.bixiri.gproject.databinding.RecyclerviewItemSubwayArrivalBinding;
import com.bixiri.gproject.thread.BusApiThread;
import com.bixiri.gproject.thread.SubwayApiThread;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class option1Activity extends AppCompatActivity {
    private ActivityOption1Binding binding; // View Binding
    AppDatabase db;
    AppSharedPreference pref;
    SubwayApiThread subwayApiThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding
        binding = ActivityOption1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);
        pref = AppSharedPreference.getInstance(this);

        // 뒤로가기 버튼
        binding.materialToolbarOp1.setNavigationOnClickListener(v -> {
            finish();
        });

        // 출발역
        {
            binding.textviewOp1DepartureStationSelected.setText(pref.getString(R.string.key_departureStation, ""));
            // SelectActivity를 불러오는데 requestCode로 출발역을 선택하는 건지 도착역을 선택하는 건지 구분
            binding.constraintlayoutOp1DepartureStation.setOnClickListener(v -> {
                Intent intent = new Intent(this, SelectActivity.class);
                intent.putExtra("title", "출발역을 선택하세요");
                // requestCode가 1이면 출발역 선택
                intent.putExtra("requestCode", 1);
                startActivityForResult(intent, 1);
            });
        }

        // 도착역
        {
            binding.textviewOp1ArrivalStationSelected.setText(pref.getString(R.string.key_arrivalStation, ""));
            // SelectActivity를 불러오는데 requestCode로 출발역을 선택하는 건지 도착역을 선택하는 건지 구분
            binding.constraintlayoutOp1ArrivalStation.setOnClickListener(v -> {
                Intent intent = new Intent(this, SelectActivity.class);
                intent.putExtra("title", "도착역을 선택하세요");
                // 선택된 출발역 데이터도 전달
                intent.putExtra("selectedData", pref.getString(R.string.key_departureStation, getString(R.string.default_departure_station)));
                // requestCode가 2이면 도착역 선택
                intent.putExtra("requestCode", 2);
                startActivityForResult(intent, 2);
            });
        }

        // 버스노선
        {
            binding.textviewOp1BusRouteSeleted.setText(pref.getString(R.string.key_busRoute, ""));
            // requestCode 3 사용
            binding.constraintlayoutOp1BusRoute.setOnClickListener(v -> {
                Intent intent = new Intent(this, SelectActivity.class);
                intent.putExtra("title", "버스노선을 선택하세요");
                intent.putExtra("requestCode", 3);
                startActivityForResult(intent, 3);
            });
        }

        // 버스정류장
        {
            binding.textviewOp1BusStopSelected.setText(pref.getString(R.string.key_busStop, ""));
            // requestCode 4 사용
            binding.constraintlayoutOp1BusStop.setOnClickListener(v -> {
                Intent intent = new Intent(this, SelectActivity.class);
                intent.putExtra("title", "버스정류장을 선택하세요");
                // 선택된 버스노선 데이터도 전달
                intent.putExtra("selectedData", pref.getString(R.string.key_busRoute, getString(R.string.default_busRoute)));
                intent.putExtra("requestCode", 4);
                startActivityForResult(intent, 4);
            });
        }

        binding.materialToolbarOp1.setOnMenuItemClickListener(menuItem -> {
            // 새로고침 버튼
            if (menuItem.getItemId() == R.id.refresh) {
                {
                    String departureStation = pref.getString(R.string.key_departureStation, "");
                    String arrivalStation = pref.getString(R.string.key_arrivalStation, "");
                    // SubwayApiThread를 통해서 받아올 리스트
                    List<SubwayApiThread.SubwayArrival> arrivalList = new ArrayList<>();
                    // recyclerView에 띄울 리스트

                    // SubwayApiThread에서 리스트 받아온 이후로 실행할 내용
                    Runnable afterRun = () -> {
                        // recyclerView에 부착하는건 메인 Thread에서만 가능하다
                        runOnUiThread(() -> {
                            binding.recyclerviewOp1SubwayResult.setLayoutManager(new LinearLayoutManager(this));
                            final RecyclerViewAdapterSubwayResult adapter = new RecyclerViewAdapterSubwayResult(arrivalList);
                            binding.recyclerviewOp1SubwayResult.setAdapter(adapter);
                        });
                    };

                    // Thread를 실행한다
                    subwayApiThread = new SubwayApiThread(this, arrivalList, departureStation, arrivalStation, afterRun);
                    subwayApiThread.start();
                }

                {
                    String busStId = pref.getString(R.string.key_busStId, "");
                    String busRouteId = pref.getString(R.string.key_busRouteId, "");
                    String busOrd = pref.getString(R.string.key_busOrg, "");
                    // BusApiThread를 통해서 받아올 리스트
                    List<BusApiThread.BusArrival> arrivalList = new ArrayList<>();

                    Runnable afterRun = () -> {
                        runOnUiThread(() -> {
                            binding.recyclerviewOp1BusResult.setLayoutManager(new LinearLayoutManager(this));
                            final RecyclerViewAdapterBusResult adapter = new RecyclerViewAdapterBusResult(arrivalList);
                            binding.recyclerviewOp1BusResult.setAdapter(adapter);
                        });
                    };

                    BusApiThread busApiThread = new BusApiThread(arrivalList, busStId, busRouteId, busOrd, afterRun);
                    busApiThread.start();
                }

                return true;
            }
            return false;
        });
    }

    // 출발역, 도착역, 버스노선, 버스정류장을 선택한 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String selectedData = data.getStringExtra("selectedData");

            switch (requestCode) {
                case 1: // 출발역을 새로 선택한 결과
                    pref.putString(R.string.key_departureStation, selectedData);
                    binding.textviewOp1DepartureStationSelected.setText(selectedData);
                    checkSelectedLines();
                    break;
                case 2: // 도착역을 새로 선택한 결과
                    pref.putString(R.string.key_arrivalStation, selectedData);
                    binding.textviewOp1ArrivalStationSelected.setText(selectedData);
                    checkSelectedLines();
                    break;
                case 3: // 버스노선을 새로 선택한 결과
                    pref.putString(R.string.key_busRoute, selectedData);
                    binding.textviewOp1BusRouteSeleted.setText(selectedData);
                    checkBusStop();
                    break;
                case 4: // 버스정류장을 새로 선택한 결과
                    pref.putString(R.string.key_busStop, selectedData);
                    binding.textviewOp1BusStopSelected.setText(selectedData);
                    checkBusStop();
                    break;
            }
        }
    }

    // 출발역과 도착역이 같은 호선에 있는지 확인하고 있으면 같은 호선이면 데이터를 저장한다
    // 종로3가 <> 신길 처럼 같은 호선이 2개 있을 수도 있으므로 List<Integer>로 저장한다
    void checkSelectedLines() {
        // DB를 사용하므로 Thread 만들어서 실행
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<SubwayStationEntity> departureList = db.subwayStationDAO().getAllWithName(pref.getString(R.string.key_departureStation, ""));
            List<SubwayStationEntity> arrivalList = db.subwayStationDAO().getAllWithName(pref.getString(R.string.key_arrivalStation, ""));
            List<Integer> selectedLine = new ArrayList<>();
            List<Integer> selectedStation = new ArrayList<>();
            for (SubwayStationEntity departure : departureList) {
                for (SubwayStationEntity arrival : arrivalList) {
                    if (departure.getSubwayId() == arrival.getSubwayId()) {
                        selectedLine.add(departure.getSubwayId());
                        selectedStation.add(departure.getStatnId());
                    }
                }
            }
            // 두 역이 같은 호선에 있지 않은 경우 예외처리를 한다
            if (selectedLine.size() == 0) {
                binding.textviewOp1ArrivalStationSelected.setText(R.string.op1_arrival_station_exception);
                pref.putString(R.string.key_arrivalStation, getString(R.string.op1_arrival_station_exception));
            }

            pref.putIntList(R.string.key_selectedStations, selectedStation);
            pref.putIntList(R.string.key_selectedLines, selectedLine);
        }).start();
    }

    // 선택한 버스노선에 선택한 버스정류장이 없는 경우를 확인한다
    // 그리고 유효한 버스정류장에 대해 RouteId, Org, StId 값을 저장한다 (BusApi에서 사용)
    void checkBusStop() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<String> selectedRoute = db.busStopDAO().getAllBusStopWithRouteName(pref.getString(R.string.key_busRoute, ""));
            String busRoute = pref.getString(R.string.key_busRoute, "");
            String busStop = pref.getString(R.string.key_busStop, "");

            for (String busStopInRoute : selectedRoute) {
                if (busStopInRoute.equals(busStop)) {
                    BusStopEntity busStopEntity = db.busStopDAO().getWithRouteAndStop(busRoute, busStop);
                    pref.putString(R.string.key_busRouteId, Integer.toString(busStopEntity.getBusRouteId()));
                    pref.putString(R.string.key_busOrg, Integer.toString(busStopEntity.getOrg()));
                    pref.putString(R.string.key_busStId, Integer.toString(busStopEntity.getStId()));
                    return;
                }
            }

            binding.textviewOp1BusStopSelected.setText(R.string.op1_busRoute_exception);
            pref.putString(R.string.key_busStop, getString(R.string.op1_busRoute_exception));
            pref.putString(R.string.key_busRouteId, "");
            pref.putString(R.string.key_busOrg, "");
            pref.putString(R.string.key_busStId, "");
        }).start();
    }

    // RecyclerView
    class RecyclerViewAdapterBusResult extends RecyclerView.Adapter<RecyclerViewAdapterBusResult.ViewHolder> {
        List<BusApiThread.BusArrival> arrivalList;

        RecyclerViewAdapterBusResult(List<BusApiThread.BusArrival> arrivalList) {
            this.arrivalList = arrivalList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            RecyclerviewItemBusArrivalBinding binding;

            ViewHolder(RecyclerviewItemBusArrivalBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

        @NonNull
        @Override
        public RecyclerViewAdapterBusResult.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerviewItemBusArrivalBinding binding = RecyclerviewItemBusArrivalBinding.
                    inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapterBusResult.ViewHolder holder, int position) {
            holder.binding.textViewRecyclerviewItemBusArrivalTitle.setText(getString(R.string.recyclerview_item_bus_arrival_title,
                    arrivalList.get(position).getStationNm()));
            holder.binding.textViewRecyclerviewItemBusArrivalContent.setText(getString(R.string.recyclerview_item_bus_arrival_content1,
                    arrivalList.get(position).getBusType(),
                    arrivalList.get(position).getRerideNum(),
                    arrivalList.get(position).getTraTime(),
                    arrivalList.get(position).getOrdDiff()));
        }

        @Override
        public int getItemCount() {
            return arrivalList.size();
        }
    }

    // RecyclerView
    class RecyclerViewAdapterSubwayResult extends RecyclerView.Adapter<RecyclerViewAdapterSubwayResult.ViewHolder> {
        private List<SubwayApiThread.SubwayArrival> arrivalList;

        class ViewHolder extends RecyclerView.ViewHolder {
            RecyclerviewItemSubwayArrivalBinding binding;

            ViewHolder(RecyclerviewItemSubwayArrivalBinding binding) {
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
            RecyclerviewItemSubwayArrivalBinding binding = RecyclerviewItemSubwayArrivalBinding.
                    inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new RecyclerViewAdapterSubwayResult.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapterSubwayResult.ViewHolder holder, int position) {
            holder.binding.textViewRecyclerviewItemSubwayArrivalTitle.setText(getString(R.string.recyclerview_item_subway_arrival_title, arrivalList.get(position).getCurrentLocation()));
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

            holder.binding.textViewRecyclerviewItemSubwayArrivalContent.setText(getString(R.string.recyclerview_item_subway_arrival_content1,
                    arrivalList.get(position).getDestination(),
                    arrivalText));
        }

        @Override
        public int getItemCount() {
            return arrivalList.size();
        }
    }
}