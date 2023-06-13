package com.example.moupass10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.moupass10.R;

public class Splash extends AppCompatActivity {
    private static final String PREFS_NAME = "PrefsStatus";
    private static final String KEY_REGISTERED = "Registered";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isUserRegistered = preferences.getBoolean(KEY_REGISTERED, false);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isUserRegistered) {
                    //startActivity(new Intent(Splash.this,Login.class));
                    //finish();
                }
                else{
                    startActivity(new Intent(Splash.this,Register.class));
                }
            }
        },1000); // Improvement -50~100 Each Improvement
    }
}
