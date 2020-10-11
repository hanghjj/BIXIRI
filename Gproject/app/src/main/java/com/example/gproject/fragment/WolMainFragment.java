package com.example.gproject.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gproject.R;
import com.example.gproject.database.AppDatabase;
import com.example.gproject.database.wolprofile.WolProfileDAO;
import com.example.gproject.database.wolprofile.WolProfileEntity;
import com.example.gproject.databinding.FragmentOp4MainBinding;
import com.example.gproject.databinding.RecyclerviewItemWolProfileBinding;
import com.example.gproject.frag4;
import com.example.gproject.thread.WakeOnLanThread;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/*
    TODO: recyclerview item 에 외곽선 넣기
    TODO: recyclerview onclickListener 함수 선언 위치 변경하기
    TODO: recyclerview 에서 favorite 클릭 시 이벤트
    TODO: recyclerview 에서 delete 클릭 시 이벤트
 */

public class WolMainFragment extends Fragment {
    private FragmentOp4MainBinding binding;
    private AppDatabase db;
    private WolProfileDAO table;
    private RecyclerViewAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // View Binding
        binding = FragmentOp4MainBinding.inflate(inflater, container, false);
        db = AppDatabase.getInstance(getContext());
        table = db.wolProfileDAO();

        new Thread(() -> {
            List<WolProfileEntity> list = table.getAll();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    binding.recyclerviewOp4Profile.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter = new RecyclerViewAdapter(list);
                    binding.recyclerviewOp4Profile.setAdapter(adapter);
                });
            } else {
                Log.d("WolMainFragment", "getActivity is null");
            }
        }).start();

        binding.floatingActionButton.setOnClickListener(v -> {
            if (getParentFragment() != null) {
                ((frag4) getParentFragment()).newProfile();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        List<WolProfileEntity> wolProfileList;

        public RecyclerViewAdapter(List<WolProfileEntity> wolProfileList) {
            this.wolProfileList = wolProfileList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            RecyclerviewItemWolProfileBinding binding;

            ViewHolder(RecyclerviewItemWolProfileBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                Log.d("WolMainFragment", "ViewHolder : " + Integer.toString(getAdapterPosition()));
            }
        }

        @NonNull
        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerviewItemWolProfileBinding binding = RecyclerviewItemWolProfileBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
            String name = wolProfileList.get(position).getName();
            int favorite = wolProfileList.get(position).getFavorite();
            String ipAddress = wolProfileList.get(position).getIpAddress();
            String macAddress = wolProfileList.get(position).getMacAddress();
            int port = wolProfileList.get(position).getPort();

            holder.binding.textViewRecyclerviewItemWolProfileTitle.setText(getString(R.string.recyclerview_item_wol_profile_title, name));
            holder.binding.textViewRecyclerviewItemWolProfileContent.setText(getString(R.string.recyclerview_item_wol_profile_content1, ipAddress, port));

            holder.binding.constraintlayoutRecyclerviewItemWolProfile.setOnClickListener(v -> {
                // Thread 만들어서 실행, WakeOnLanThread.java 참조
                WakeOnLanThread wakeOnLanThread = new WakeOnLanThread(ipAddress, macAddress, port);
                wakeOnLanThread.start();
                // 확인 메시지 출력
                Toast myToast = Toast.makeText(getContext(), R.string.op4_wol_toast, Toast.LENGTH_SHORT);
                myToast.show();
                Log.d("test 레이아웃", Integer.toString(v.getId()));
            });
            holder.binding.buttonRecyclerviewItemWolProfile.setOnClickListener(v -> {
                if (getContext() != null) {
                    PopupMenu popupMenu = new PopupMenu(getContext(), holder.binding.buttonRecyclerviewItemWolProfile);
                    popupMenu.getMenuInflater().inflate(R.menu.popupmenu_wol_item, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case R.id.item_popupMenuWolItem_favorite:
                                return true;
                            case R.id.item_popupMenuWolItem_delete:
                                new Thread(() -> {
                                    table.delete(new WolProfileEntity(name, favorite, ipAddress, macAddress, port));
                                }).start();
                                return true;
                            case R.id.item_popupMenuWolItem_edit:
                                return true;
                            default:
                                return false;
                        }
                    });
                    popupMenu.show();
                } else {
                    Log.d("WolMainFragment", "getContext in ViewHolder is null");
                }
            });
        }

        @Override
        public int getItemCount() {
            return wolProfileList.size();
        }
    }
}