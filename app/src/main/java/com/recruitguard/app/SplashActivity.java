package com.recruitguard.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().hide();
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // Always go to Sign In (manual login every time)
                startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                finish();
            }
        }, 2000);
    }
}

