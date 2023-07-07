package com.example.moupass10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.example.moupass10.R;

public class Splash extends AppCompatActivity {
    private static final String PREFS_NAME = "PrefsStatus";
    private static final String KEY_REGISTERED = "userRegistered";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Fix white bar under screen
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean userRegistered = preferences.getBoolean(KEY_REGISTERED, false);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userRegistered) {
                    startActivity(new Intent(Splash.this,Login.class));
                    finish();
                }
                else{
                    startActivity(new Intent(Splash.this,Register.class));
                }
            }
        },1000); // Improvement -50~100 Each Improvement
    }
}
