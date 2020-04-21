package com.example.gproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.gproject.databinding.ActivityOption4Binding; // View Binding

public class option4Activity extends AppCompatActivity {
    private ActivityOption4Binding binding; // View Binding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding
        binding = ActivityOption4Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 뒤로가기 버튼
        binding.ReturnHome.setOnClickListener(v -> {
            finish();
        });
    }
}
