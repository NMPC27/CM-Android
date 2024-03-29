package com.example.cinem.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.cinem.R;

public class ReminderBroadcast extends BroadcastReceiver {

    String filmeName;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        if (extras != null) {
            filmeName = extras.getString("filmeName");
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Cinem@")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(filmeName)
                .setContentText("Movie Starting in 5 minutes!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

}
