package com.bixiri.gproject;

import android.app.Notification;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

import androidx.annotation.RequiresApi;

public class MyNotificationListener extends NotificationListenerService {
    private CharSequence mContent;
    private TextToSpeech ttsKOR;
    private TextToSpeech ttsENG;

    @Override
    public void onCreate() {
        super.onCreate();
        ttsKOR = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                ttsKOR.setLanguage(Locale.KOREAN);
            }
        });

        ttsENG = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                ttsENG.setLanguage(Locale.ENGLISH);
            }
        });
    }

    // TODO: 사용자가 직접 TTS 작동하는 Notification을 설정할 수 있도록
    // com.samsung.android.incallui
    // dk.tacit.android.foldersync.lite
    // com.samsung.android.app.cocktailbarservice
    // com.samsung.android.dialer

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        String pkg = sbn.getOpPkg();
        if (!pkg.isEmpty()) {
            Log.d(pkg, pkg);
        }

        switch (sbn.getOpPkg()) {
            case "com.android.systemui":
                String tag = sbn.getTag();
                Log.d(tag, tag);
                break;
            case "com.Slack":
                mContent = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT);
                runTTS(ttsENG, mContent.toString());
                break;
//            case "com.kakao.talk":
            default:
                mContent = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
                if (mContent != null){
                    Log.d(sbn.getTag(), mContent.toString());
                    runTTS(ttsKOR, mContent.toString());
                }
                break;
        }
    }

    void runTTS(TextToSpeech tts, String text) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, null,"TTSNotification");
    }
}
