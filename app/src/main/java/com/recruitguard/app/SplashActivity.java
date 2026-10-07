package com.recruitguard.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                finish();
            }
        }, 2000);
    }

    private void checkLoginStatus() {
        // Get saved session
        SharedPreferences prefs = getSharedPreferences("RecruitGuardPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String role = prefs.getString("role", null);

        if (token != null && !token.isEmpty()) {
            // User is logged in, so go to appropriate screen based on role
            if ("admin".equals(role)) {
                // TODO: Replace with Admin Dashboard when Bile builds it
                android.widget.Toast.makeText(this, "Welcome back Admin!", android.widget.Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(this, AdminDashboardActivity.class));
            } else {
                // TODO: Replace with Home Screen when Tanaka builds it
                android.widget.Toast.makeText(this, "Welcome back " + prefs.getString("name", ""), android.widget.Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(this, HomeActivity.class));
            }
        } else {
            // Not logged in - go to Sign In
            startActivity(new Intent(SplashActivity.this, SignInActivity.class));
        }

        finish();
    }
}