package com.example.fpmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends AppCompatActivity {
    Handler handlerSplashScreen = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handlerSplashScreen.postDelayed(new Runnable() {
            @Override
            public void run() {
                // need conditional if there is current user
                Intent loginIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        }, 3000);
    }
}
