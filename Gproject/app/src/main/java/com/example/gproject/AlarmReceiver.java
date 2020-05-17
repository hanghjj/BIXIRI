package com.example.gproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager noti = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notiint  = new Intent(context, MainActivity.class);
        notiint.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendint = PendingIntent.getActivity(context,0,notiint,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"10001")
                .setContentTitle("Test").setContentText("test").setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendint).setAutoCancel(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder.setSmallIcon(R.drawable.op2_background);
            CharSequence channalName = "TEST";
            String description = "test";
            int imp = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel ch = new NotificationChannel("10001",channalName,imp);
            ch.setDescription(description);

            assert noti != null;
            noti.createNotificationChannel(ch);

        }else builder.setSmallIcon(R.mipmap.ic_launcher);

        assert noti != null;
        noti.notify(1234,builder.build());
    }
}
