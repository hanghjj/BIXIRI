package com.example.gproject;

import android.os.Bundle;

import com.example.gproject.databinding.ActivityOption3Binding;

import androidx.appcompat.app.AppCompatActivity;

public class option3Activity extends AppCompatActivity {
    private ActivityOption3Binding binding; // View Binding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding
        binding = ActivityOption3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 뒤로가기 버튼
        binding.ReturnHome.setOnClickListener(v -> {
            finish();
        });
    }
}
