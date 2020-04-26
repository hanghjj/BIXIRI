package com.example.gproject;

import android.os.Bundle;

import com.example.gproject.databinding.ActivityOption1Binding;

import androidx.appcompat.app.AppCompatActivity;

public class option1Activity extends AppCompatActivity {
    private ActivityOption1Binding binding; // View Binding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding
        binding = ActivityOption1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 뒤로가기 버튼
        binding.ReturnHome.setOnClickListener(v -> {
           finish();
        });
    }
}