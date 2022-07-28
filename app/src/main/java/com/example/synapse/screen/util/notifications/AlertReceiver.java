package com.example.synapse.screen.util.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MedicineNotificationHelper medicineNotificationHelper = new MedicineNotificationHelper(context);
        NotificationCompat.Builder nb = medicineNotificationHelper.getChannelNotification();
        medicineNotificationHelper.getManager().notify(1, nb.build());

        context.sendBroadcast(new Intent("NOTIFY"));
       // MediaPlayer mp = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
       // mp.setLooping(true);
       // mp.start();

       }
     }
