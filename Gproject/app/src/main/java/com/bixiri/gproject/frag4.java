package com.bixiri.gproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bixiri.gproject.databinding.ActivityOption4Binding;
import com.bixiri.gproject.fragment.WolMainFragment;
import com.bixiri.gproject.fragment.dialog.WolProfileDialogFragment;

import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class frag4 extends Fragment {
    private ActivityOption4Binding binding; // View Binding
    ActionMode actionMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // View Binding
        binding = ActivityOption4Binding.inflate(inflater, container, false);

        getChildFragmentManager().beginTransaction().add(R.id.framelayout_op4, new WolMainFragment()).addToBackStack(null).commit();

        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (actionMode != null)
            actionMode.finish();
    }

    public void newProfile() {
        getChildFragmentManager().beginTransaction().add(R.id.framelayout_op4, new WolProfileDialogFragment(), "wolProfile").addToBackStack(null).commit();
//        getFragmentManager().beginTransaction().add(android.R.id.content, new WolProfileDialogFragment()).addToBackStack(null).commit();

        if (getActivity() != null) {
            actionMode = ((MainActivity) getActivity()).startSupportActionMode(actionModeCallback);
            assert actionMode != null;
            actionMode.setTitle("새 프로필 생성");
        }
    }

    // Action Mode 콜백
    ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_wol_profile_configure, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // wol Profile Fragment 제거
            FragmentManager fragmentManager = getChildFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag("wolProfile");
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
        }
    };
}