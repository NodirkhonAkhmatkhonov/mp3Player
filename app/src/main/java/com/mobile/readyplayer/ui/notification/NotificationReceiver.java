package com.mobile.readyplayer.ui.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.mobile.readyplayer.Constants;
import com.mobile.readyplayer.MainActivity;

import java.util.ArrayList;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String keys[] = {"previous", "play", "next", "close"};

        int id = -1;
        for (String key : keys) {
            if (intent.getIntExtra(key, -1) != -1) {
                id = intent.getIntExtra(key, -1);
                break;
            }
        }

        Intent intent1 = new Intent(Constants.MAIN_ACTION);
        intent1.putExtra("type", id);
        context.sendBroadcast(intent1);
    }
}
