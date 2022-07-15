package com.example.synapse.screen.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.example.synapse.screen.util.notifications.MedicineNotificationHelper;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MedicineNotificationHelper medicineNotificationHelper = new MedicineNotificationHelper(context);
        NotificationCompat.Builder nb = medicineNotificationHelper.getChannelNotification();
        medicineNotificationHelper.getManager().notify(1, nb.build());
    }
}
