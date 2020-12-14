package com.bixiri.gproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.bixiri.gproject.database.AppDatabase;
import com.bixiri.gproject.databinding.ActivityStationSubwaySelectBinding;
import com.bixiri.gproject.databinding.RecyclerviewItemSubwayStationBinding;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SelectActivity extends AppCompatActivity {
    private ActivityStationSubwaySelectBinding binding;
    Intent intent;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding
        binding = ActivityStationSubwaySelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intent = getIntent();
        AppDatabase db = AppDatabase.getInstance(this);

        // title 설정
        binding.materialtoolbarStationSubwaySelect.setTitle(intent.getStringExtra("title"));

        // 취소 버튼
        binding.materialtoolbarStationSubwaySelect.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED, intent);
            finish();
        });

        // RecyclerView를 사용해 DB에서 불러온 역 리스트를 보여준다
        // requestCode로 출발역을 선택하는 건지 도착역을 선택하는 건지 확인한다
        int requestCode = intent.getIntExtra("requestCode", 0);
        new Thread(() -> {
            // RecyclerView를 통해 보일 List
            List<String> list;

            switch (requestCode) {
                case 1: // 출발역을 선택하는 경우
                    // 모든 지하철 역을 가져온다
                    list = db.subwayStationDAO().getAllNames();
                    break;
                case 2: // 도착역을 선택하는 경우
                    // 출발역과 같은 노선에 있는 역들만 가져온다
                    List<Integer> lineList = db.subwayStationDAO().getAllLinesWithName(intent.getStringExtra("selectedData"));
                    list = db.subwayStationDAO().getAllNamesWithLines(lineList);
                    break;
                case 3: // 버스노선을 선택하는 경우
                    // 모든 버스노선을 가져온다.
                    list = db.busStopDAO().getAllBusRoute();
                    break;
                case 4: // 버스정류장을 선택하는 경우
                    // 선택한 버스노선에 있는 모든 버스정류장을 가져온다
                    list = db.busStopDAO().getAllBusStopWithRouteName(intent.getStringExtra("selectedData"));
                    break;
                default:
                    list = new ArrayList<>();
            }

            // 메인 Thread에서 RecyclerView를 통해 보여준다
            runOnUiThread(() -> {
                setRecyclerView(list);
            });
        }).start();
    }

    // RecyclerView를 만들고 검색기능을 넣는 함수
    void setRecyclerView(List<String> list) {
        binding.recyclerviewStationSubwaySelect.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(list);
        binding.recyclerviewStationSubwaySelect.setAdapter(adapter);

        // SearchView에서 얻은 String을 RecyclerView의 filter에 적용시킨다 (검색기능)
        SearchView searchView = binding.materialtoolbarStationSubwaySelect.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return true;
            }
        });
    }

    // RecyclerView
    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {
        private List<String> unFiltered_list;
        private List<String> filtered_list;

        class ViewHolder extends RecyclerView.ViewHolder {
            RecyclerviewItemSubwayStationBinding binding;

            ViewHolder(RecyclerviewItemSubwayStationBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

        RecyclerViewAdapter(List<String> list) {
            unFiltered_list = list;
            filtered_list = list;
        }

        @NonNull
        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerviewItemSubwayStationBinding binding = RecyclerviewItemSubwayStationBinding.
                    inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
            holder.binding.textViewRecyclerviewItemStationSubway.setText(filtered_list.get(position));
            // 리스트를 클릭한 경우 해당 리스트를 결과로 부모 액티비티에 전달
            holder.binding.constraintlayoutRecyclerviewItemStationSubway.setOnClickListener(v -> {
                intent.putExtra("selectedData", filtered_list.get(position));
                setResult(RESULT_OK, intent);
                finish();
            });
        }

        @Override
        public int getItemCount() {
            return filtered_list.size();
        }

        // 전달받은 문자열로 필러링 처리
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        filtered_list = unFiltered_list;
                    } else {
                        List<String> filteringList = new ArrayList<>();
                        for (String statnNm : unFiltered_list) {
                            if (statnNm.toLowerCase().contains(charString.toLowerCase())) {
                                filteringList.add(statnNm);
                            }
                        }
                        filtered_list = filteringList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filtered_list;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    filtered_list = (List<String>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }
}
