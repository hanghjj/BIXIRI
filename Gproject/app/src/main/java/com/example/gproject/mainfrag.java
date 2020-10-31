package com.example.gproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gproject.databinding.MainfragBinding;

public class mainfrag extends Fragment {
    private MainfragBinding binding;
    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedinstancestate){
        binding = MainfragBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.time.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TimeTable2.class));
        });
        binding.test.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ActLocation.class));
        });
    }
}
