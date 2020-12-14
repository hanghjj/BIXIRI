package com.bixiri.gproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;

import com.bixiri.gproject.database.AppDatabase;
import com.bixiri.gproject.databinding.ActivityStationSubwaySelectBinding;
import com.bixiri.gproject.databinding.RecyclerviewItemSubwayStationBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StationSubwaySelectActivity extends AppCompatActivity {
    private ActivityStationSubwaySelectBinding binding;
    Intent intent;

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
        if (intent.getIntExtra("requestCode", 0) == 1) {
            // 출발역을 설정하는 경우
            new Thread(() -> {
                // 모든 역을 보여준다
                List<String> stringList = db.subwayStationDAO().getAllNames();

                // 메인 Thread에서 RecyclerView를 통해 보여준다
                runOnUiThread(() -> {
                    binding.recyclerviewStationSubwaySelect.setLayoutManager(new LinearLayoutManager(this));
                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(stringList);
                    binding.recyclerviewStationSubwaySelect.setAdapter(adapter);
                    setSearchViewWithFilter(adapter);
                });
            }).start();
        } else if (intent.getIntExtra("requestCode", 0) == 2) {
            //            // 도착역을 설정하는 경우
            new Thread(() -> {
                // 출발역과 같은 노선에 있는 역들만 보여준다
                List<Integer> lineList = db.subwayStationDAO().getAllLinesWithName(intent.getStringExtra("selectedStation"));
                List<String> stringList = db.subwayStationDAO().getAllNamesWithLines(lineList);

                // 메인 Thread에서 RecyclerView를 통해 보여준다
                runOnUiThread(() -> {
                    binding.recyclerviewStationSubwaySelect.setLayoutManager(new LinearLayoutManager(this));
                    final RecyclerViewAdapter adapter = new RecyclerViewAdapter(stringList);
                    binding.recyclerviewStationSubwaySelect.setAdapter(adapter);
                    setSearchViewWithFilter(adapter);
                });
            }).start();
        }
    }

    // SearchView에 검색기능을 넣는 함수
    // 입력된 String을 RecyclerView의 Filter로 전달
    void setSearchViewWithFilter(RecyclerViewAdapter adapter) {
        SearchView searchView = (SearchView) binding.materialtoolbarStationSubwaySelect.findViewById(R.id.search);
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
        private List<String> unFiltered_statnNm;
        private List<String> filtered_statnNm;

        public class ViewHolder extends RecyclerView.ViewHolder {
            RecyclerviewItemSubwayStationBinding binding;

            public ViewHolder(RecyclerviewItemSubwayStationBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

        public RecyclerViewAdapter(List<String> statnNm) {
            unFiltered_statnNm = statnNm;
            filtered_statnNm = statnNm;
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
            holder.binding.textViewRecyclerviewItemStationSubway.setText(filtered_statnNm.get(position));
            // 리스트를 클릭한 경우 해당 리스트를 결과로 부모 액티비티에 전달
            holder.binding.constraintlayoutRecyclerviewItemStationSubway.setOnClickListener(v -> {
                intent.putExtra("selectedStation", filtered_statnNm.get(position));
                setResult(RESULT_OK, intent);
                finish();
            });
        }

        @Override
        public int getItemCount() {
            return filtered_statnNm.size();
        }

        // 전달받은 문자열로 필러링 처리
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        filtered_statnNm = unFiltered_statnNm;
                    } else {
                        List<String> filteringList = new ArrayList<>();
                        for (String statnNm : unFiltered_statnNm) {
                            if (statnNm.toLowerCase().contains(charString.toLowerCase())) {
                                filteringList.add(statnNm);
                            }
                        }
                        filtered_statnNm = filteringList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filtered_statnNm;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    filtered_statnNm = (List<String>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }
}
