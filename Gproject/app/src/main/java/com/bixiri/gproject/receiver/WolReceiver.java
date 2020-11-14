package com.bixiri.gproject.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class WolReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        intent.getData();
        Uri data = intent.getData();
        String a = intent.getAction();
        Log.d(getClass().getName(), "received: " + a);
    }
}
