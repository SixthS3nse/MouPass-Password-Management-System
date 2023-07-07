package com.example.moupass10;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MoupassPhishingDetection extends Service {
    private static final String CHANNEL_ID = "MyChannel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create a notification and start the service in foreground
        createNotificationChannel();
        startForeground(1, getNotification("Checking website status...", Color.GRAY));

        // Get the URL from the intent and check its status in a new thread
        new Thread(() -> {
            String url = intent.getStringExtra("url");
            String status = checkWebsiteStatus(url);
            Notification notification = getNotification("The status of the website is: " + status, getStatusColor(status));
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.notify(1, notification);
        }).start();

        return START_NOT_STICKY;
    }

    //API
    private String checkWebsiteStatus(String url) {
        String status = "unknown";
        OkHttpClient client = new OkHttpClient();

        // Here, you need to replace <YOUR_API_KEY> and <CLIENT_ID> with your actual API key and client ID
        String apiUrl = "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=AIzaSyDmlOM-i457qZobyUJgsMQ7LJ2XIHszLSY";

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("clientId", "MOUPASS");
        requestBody.addProperty("clientVersion", "1.5.2");
        requestBody.addProperty("threatTypes", "SOCIAL_ENGINEERING");
        requestBody.addProperty("platformTypes", "WINDOWS");
        requestBody.addProperty("threatEntryTypes", "URL");
        requestBody.addProperty("threatEntries", url);

        // ... You need to construct the rest of the request body according to the Google Safe Browsing API ...

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(okhttp3.RequestBody.create(requestBody.toString(), okhttp3.MediaType.parse("application/json; charset=utf-8")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            JsonObject jsonObject = JsonParser.parseString(response.body().string()).getAsJsonObject();
            // Parse the response and determine the status ...
        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }

    private Notification getNotification(String text, int color) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        }

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Website Status")
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setColor(color)
                .build();
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "safe":
                return Color.GREEN;
            case "malicious":
                return Color.YELLOW;
            case "danger":
                return Color.RED;
            default:
                return Color.GRAY;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Website Status Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}