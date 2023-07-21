package com.example.moupass10;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.graphics.Color;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.app.NotificationCompat;

public class MoupassPhishingDetection extends Service {

    private static final String CHANNEL_ID = "SafeBrowsingChannel";
    private static final int NOTIFICATION_ID = 1;

    private WebView webView;

/*
    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();
        setupWebView();
    }
*/


    private void startForegroundService() {
        createNotificationChannel();

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Safe Browsing Service")
                .setContentText("Service running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Safe Browsing Channel";
            String description = "Channel for Safe Browsing Service";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setupWebView() {
        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Instead of loading link right away, the application will check it first
                checkUrlSafety(url);
                // Return true to indicate that we're handling the URL loading.
                return true;
            }
        });
    }

    public void loadUrl(String url) {
        // This method allows external components to ask the service to load a URL.
        webView.loadUrl(url);
    }

    private void checkUrlSafety(String url) {
        new SafeBrowsingTask(new SafeBrowsingTask.SafeBrowsingCallback() {
            @Override
            public void onSafeBrowsingComplete(String status) {
                if (status.equals("safe")) {
                    showNotification("Safe", Color.GREEN);
                    // The URL is safe, so load it in the WebView.
                    webView.loadUrl(url);
                } else {
                    // If the URL is not safe, show a notification and don't load the URL.
                    showNotification("Danger", Color.RED);
                }
            }
        }).execute(url);
    }

    private void showNotification(String status, int backgroundColor) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Website Safety")
                .setContentText(status)
                .setColor(backgroundColor)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
