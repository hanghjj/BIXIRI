package com.bixiri.gproject;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class MyNotificationListener extends NotificationListenerService {
    private String mContent;
    private TextToSpeech textToSpeech;

    @Override
    public void onCreate() {
        super.onCreate();
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.setLanguage(Locale.KOREAN);
            }
        });
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString(Notification.EXTRA_TITLE);
        CharSequence text = extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
        CharSequence subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
        Log.d("test", text.toString());
        textToSpeech.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, null);
    }
}
