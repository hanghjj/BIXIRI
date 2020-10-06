package com.example.gproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gproject.R;
import com.example.gproject.databinding.FragmentOp4MainBinding;
import com.example.gproject.fragment.dialog.WolProfileDialogFragment;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

public class Op4MainFragment extends Fragment {
    private FragmentOp4MainBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // View Binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_op4_main, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.floatingActionButton.setOnClickListener(v -> {
            WolProfileDialogFragment fragment = new WolProfileDialogFragment();
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.framelayout_op4, fragment).addToBackStack(null).commit();
        });
    }
}
