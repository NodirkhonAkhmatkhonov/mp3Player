package com.mobile.readyplayer.ui.notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_ID_1 = "channel1";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID_1,"My Example", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
