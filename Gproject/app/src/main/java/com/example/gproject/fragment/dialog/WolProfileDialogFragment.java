package com.example.gproject.fragment.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gproject.database.AppDatabase;
import com.example.gproject.database.wolprofile.WolProfileDAO;
import com.example.gproject.database.wolprofile.WolProfileEntity;
import com.example.gproject.databinding.DialogfragmentWolProfileBinding;

import androidx.fragment.app.DialogFragment;

/*
    TODO: 텍스트 입력 시에 양식에 맞는지 에러 확인
 */

public class WolProfileDialogFragment extends DialogFragment {
    DialogfragmentWolProfileBinding binding; // View Binding
    private WolProfileDAO table;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // View Binding
        binding = DialogfragmentWolProfileBinding.inflate(inflater, container, false);
        table = AppDatabase.getInstance(getContext()).wolProfileDAO();

        binding.outlinedBtnWolProfile.setOnClickListener(v -> {
            dismiss();
        });

        return binding.getRoot();
    }

    @Override
    public void dismiss() {
        String name = binding.textFieldWolProfileName.getEditText().getText().toString();
        String ip = binding.textFieldWolProfileIp.getEditText().getText().toString();
        String mac = binding.textFieldWolProfileMac.getEditText().getText().toString();
        String port = binding.textFieldWolProfilePort.getEditText().getText().toString();
        new Thread(() -> {
            table.insert(new WolProfileEntity(name, 0, ip, mac, Integer.parseInt(port)));
        }).start();

        super.dismiss();
    }
}
