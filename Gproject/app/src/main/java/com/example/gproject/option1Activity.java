package com.example.gproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.gproject.database.AppDatabase;
import com.example.gproject.database.AppSharedPreference;
import com.example.gproject.database.subwaystation.SubwayStationDAO;
import com.example.gproject.databinding.ActivityOption1Binding;
import com.example.gproject.databinding.RecyclerviewItemSubwayArrivalBinding;
import com.example.gproject.thread.SubwayApiThread;

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
        binding.materialtoolbarOp1.setNavigationOnClickListener(v -> {
            finish();
        });

        // 출발역 텍스트 설정
        binding.textviewOp1DepartureStationSelected.setText(pref.getString(R.string.key_departureStation, getString(R.string.default_departure_station)));

        // 출발역 설정 버튼
        // StationSubwaySelectActivity를 불러오는데 requestCode로 출발역을 선택하는 건지 도착역을 선택하는 건지 구분
        binding.constraintlayoutOp1DepartureStation.setOnClickListener(v -> {
            Intent intent = new Intent(this, StationSubwaySelectActivity.class);
            intent.putExtra("title", "출발역을 선택하세요");
            // requestCode가 1이면 출발역 선택
            intent.putExtra("requestCode", 1);
            startActivityForResult(intent, 1);
        });

        // 도착역 텍스트 설정
        binding.textviewOp1ArrivalStationSelected.setText(pref.getString(R.string.key_arrivalStation, getString(R.string.default_arrival_station)));

        // 도착역 설정 버튼
        // StationSubwaySelectActivity를 불러오는데 requestCode로 출발역을 선택하는 건지 도착역을 선택하는 건지 구분
        binding.constraintlayoutOp1ArrivalStation.setOnClickListener(v -> {
            Intent intent = new Intent(this, StationSubwaySelectActivity.class);
            intent.putExtra("title", "도착역을 선택하세요");
            // 선택된 출발역 데이터도 전송
            intent.putExtra("selectedStation", pref.getString(R.string.key_departureStation, getString(R.string.default_departure_station)));
            // requestCode가 2이면 도착역 선택
            intent.putExtra("requestCode", 2);
            startActivityForResult(intent, 2);
        });

        binding.materialtoolbarOp1.setOnMenuItemClickListener(menuItem -> {
            // 새로고침 버튼
            if (menuItem.getItemId() == R.id.refresh) {
                String departureStation = pref.getString(R.string.key_departureStation, "");
                String arrivalStation = pref.getString(R.string.key_arrivalStation, "");
                // SubwayApiThread를 통해서 받아올 리스트
                List<SubwayApiThread.SubwayArrival> arrivalList = new ArrayList<>();
                // recyclerView에 띄울 리스트
                List<SubwayApiThread.SubwayArrival> showList = new ArrayList<>();
                // 출발역에서 도착역으로 가는 지하철 노선 (한개일수도 여러개일수도)
                List<Integer> lineList = pref.getIntList(R.string.key_selectedLines, 0);
                // db에서 사용할 테이블
                SubwayStationDAO table = db.subwayStationDAO();

                // SubwayApiThread에서 리스트 받아온 이후로 실행할 내용
                Runnable afterRun = () -> {
                    // 출발역과 도착역이 같게 설정된 경우 모든 도착 데이터를 보여준다
                    if (arrivalStation.equals(departureStation)) {
                        // recyclerView에 부착하는건 메인 Thread에서만 가능하다
                        runOnUiThread(() -> {
                            binding.recyclerviewOp1SubwayResult.setLayoutManager(new LinearLayoutManager(this));
                            final RecyclerViewAdapter adapter = new RecyclerViewAdapter(arrivalList);
                            binding.recyclerviewOp1SubwayResult.setAdapter(adapter);
                        });
                    } else {
                        // 출발역과 도착역이 서로 다르게 설정된 경우 해당하는 열차 데이터만 보여준다
                        int wayTo;
                        int currentStationId;
                        for (int line : lineList) {
                            currentStationId = table.getIdWithNameAndLine(departureStation, line);
                            // 열차의 방향을 계산한다
                            wayTo = table.getIdWithNameAndLine(arrivalStation, line) - currentStationId;
                            for (SubwayApiThread.SubwayArrival arrival : arrivalList) {
                                // 받아온 열차의 노선과 방향(상행, 하행, 외선, 내선)이 같은 경우에만 보여준다
                                if (arrival.getLine() == line && (table.getIdWithNameAndLine(arrival.getDestination(), line) - currentStationId) * wayTo >= 0) {
                                    showList.add(arrival);
                                }
                            }
                        }

                        // recyclerView에 부착하는건 메인 Thread에서만 가능하다
                        runOnUiThread(() -> {
                            binding.recyclerviewOp1SubwayResult.setLayoutManager(new LinearLayoutManager(this));
                            final RecyclerViewAdapter adapter = new RecyclerViewAdapter(showList);
                            binding.recyclerviewOp1SubwayResult.setAdapter(adapter);
                        });
                    }
                };

                // Thread를 실행한다
                subwayApiThread = new SubwayApiThread(arrivalList, departureStation, afterRun);
                subwayApiThread.start();

                return true;
            }
            return false;
        });
    }

    // 새로운 출발, 도착역을 선택한 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                // 출발역을 새로 선택한 결과
                String selectedStation = data.getStringExtra("selectedStation");
                pref.putString(R.string.key_departureStation, selectedStation);
                binding.textviewOp1DepartureStationSelected.setText(selectedStation);
            } else if (requestCode == 2) {
                // 도착역을 새로 선택한 결과
                String selectedStation = data.getStringExtra("selectedStation");
                pref.putString(R.string.key_arrivalStation, selectedStation);
                binding.textviewOp1ArrivalStationSelected.setText(selectedStation);
            }

            // 출발역과 도착역이 같은 호선에 있는지 확인하고 있으면 같은 호선이면 데이터를 저장한다
            // 종로3가 <> 신길 처럼 같은 호선이 2개 있을 수도 있으므로 List<Integer>로 저장한다
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                List<Integer> departureLines = db.subwayStationDAO().getAllLinesWithName(pref.getString(R.string.key_departureStation, ""));
                List<Integer> arrivalLines = db.subwayStationDAO().getAllLinesWithName(pref.getString(R.string.key_arrivalStation, ""));
                List<Integer> selectedLine = new ArrayList<>();
                for (int departureLine : departureLines) {
                    for (int arrivalLine : arrivalLines) {
                        if (departureLine == arrivalLine) {
                            selectedLine.add(arrivalLine);
                        }
                    }
                }
                // 두 역이 같은 호선에 있지 않은 경우 예외처리를 한다
                if (selectedLine.size() == 0) {
                    binding.textviewOp1ArrivalStationSelected.setText(getString(R.string.op1_arrival_station_exception));
                    pref.putString(R.string.key_arrivalStation, getString(R.string.op1_arrival_station_exception));
                }
                pref.putIntList(R.string.key_selectedLines, selectedLine);
            }).start();
        }
    }

    // RecyclerView
    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private List<SubwayApiThread.SubwayArrival> arrivalList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            RecyclerviewItemSubwayArrivalBinding binding;

            public ViewHolder(RecyclerviewItemSubwayArrivalBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

        public RecyclerViewAdapter(List<SubwayApiThread.SubwayArrival> arrivalList) {
            this.arrivalList = arrivalList;
        }

        @NonNull
        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerviewItemSubwayArrivalBinding binding = RecyclerviewItemSubwayArrivalBinding.
                    inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new RecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
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

            holder.binding.textViewRecyclerviewItemSubwayArrivalContent.setText(getString(R.string.recyclerview_item_subway_arrival_content1, arrivalList.get(position).getDestination(), arrivalText));
        }

        @Override
        public int getItemCount() {
            return arrivalList.size();
        }
    }
}