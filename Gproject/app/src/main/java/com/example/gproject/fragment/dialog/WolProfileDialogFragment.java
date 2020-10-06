package com.example.gproject.fragment.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.gproject.R;
import com.example.gproject.option4Activity;

import java.util.Objects;

import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.DialogFragment;

public class WolProfileDialogFragment extends DialogFragment {
    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        ActionMode actionMode = ((option4Activity) Objects.requireNonNull(getActivity())).startSupportActionMode(actionModeCallback);
        if (actionMode != null) {
            actionMode.setTitle("프로필 구성");
        }
        return inflater.inflate(R.layout.dialogfragment_wol_profile, container, false);
    }

//    /** The system calls this only when creating the layout in a dialog. */
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // The only reason you might override this method when using onCreateView() is
//        // to modify any dialog characteristics. For example, the dialog includes a
//        // title by default, but your custom layout might not need it. So here you can
//        // remove the dialog title, but you must call the superclass to get the Dialog.
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        return dialog;
//    }

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
            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    };
}
