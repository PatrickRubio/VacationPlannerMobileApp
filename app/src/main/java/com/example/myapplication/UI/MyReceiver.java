package com.example.myapplication.UI;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;

public class MyReceiver extends BroadcastReceiver {
    String main_Channel = "test";
    static int notificationID;

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // Intent for the Broadcast
        Toast.makeText(context, intent.getStringExtra("alertText"), Toast.LENGTH_LONG).show();

        createNotificationChannel(context,main_Channel);

        Notification n=new NotificationCompat.Builder(context,main_Channel)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText(intent.getStringExtra("alertText"))
                .setContentTitle("Vacation Reminder").build();

        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID++,n);
    }
    private void createNotificationChannel(Context context, String MAIN_CHANNEL){
        CharSequence name="Vacation Alerts";
        String description="Notifications for vacation reminders";
        int importance= NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel= null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(MAIN_CHANNEL,name,importance);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setDescription(description);
        }
        NotificationManager notificationManager=context.getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
    }
}
