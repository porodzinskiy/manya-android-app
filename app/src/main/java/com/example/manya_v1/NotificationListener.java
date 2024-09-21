package com.example.manya_v1;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.ComponentName;
import android.content.Context;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotificationListener extends NotificationListenerService {

    public  boolean isListenerConnected;


    @Override
    public void onListenerConnected() {
        super.onListenerConnected();

    }




}
