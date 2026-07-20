package com.example.farmerassistant.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.farmerassistant.MainActivity;
import com.example.farmerassistant.R;

public class OrderReminderService extends Service {

    public static final String CHANNEL_ID = "order_reminder_channel";
    private static final int NOTIFICATION_ID = 101;

    private Handler handler;
    private Runnable reminderRunnable;

    @Override
    public void onCreate() {
        super.onCreate();

        handler = new Handler();

        reminderRunnable = new Runnable() {
            @Override
            public void run() {
                showReminderNotification(
                        "Buyer Order Reminder",
                        "Check your buyer orders in Farmer Assistant"
                );

                handler.postDelayed(this, 30000); // every 30 seconds
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        int pendingFlags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                : PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, pendingFlags
        );

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Farmer Assistant Service")
                .setContentText("Order reminder service is running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        startForeground(NOTIFICATION_ID, notification.build());

        handler.post(reminderRunnable);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (handler != null && reminderRunnable != null) {
            handler.removeCallbacks(reminderRunnable);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showReminderNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);

        int pendingFlags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                : PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 1, intent, pendingFlags
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Order Reminder Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            serviceChannel.setDescription("Channel for order reminder foreground service");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}