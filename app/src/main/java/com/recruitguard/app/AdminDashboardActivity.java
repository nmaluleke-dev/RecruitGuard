package com.recruitguard.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Open Admin Review Screen
        Button btnReview = findViewById(R.id.btnReview);
        if (btnReview != null) {
            btnReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminDashboardActivity.this, AdminReviewActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Open Audit Trail Screen
        Button btnAuditTrail = findViewById(R.id.btnAuditTrail);
        if (btnAuditTrail != null) {
            btnAuditTrail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminDashboardActivity.this, AuditTrailActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}