package com.recruitguard.app

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminReviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_review)

        // Setup Back Button
        val btnBack = findViewById<ImageView>(R.id.btnBackReview)
        btnBack?.setOnClickListener {
            finish() // Navigates smoothly back to Admin Dashboard
        }

        // Setup Decision Action Buttons
        val btnLowRisk = findViewById<Button>(R.id.btnLowRisk)
        val btnHighRisk = findViewById<Button>(R.id.btnHighRisk)

        btnLowRisk?.setOnClickListener {
            Toast.makeText(this, "Marked as Low Risk / Legitimate", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnHighRisk?.setOnClickListener {
            Toast.makeText(this, "Flagged as High Risk / Fraud", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}