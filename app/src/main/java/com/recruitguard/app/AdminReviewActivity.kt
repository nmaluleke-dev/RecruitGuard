package com.recruitguard.app

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AdminReviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_review)

        // Get data from intent
        val scanId = intent.getStringExtra("scan_id") ?: "CAS-000"
        val userName = intent.getStringExtra("user_name") ?: "Unknown User"
        val jobTitle = intent.getStringExtra("job_title") ?: "Job Posting"
        val riskLevel = intent.getStringExtra("risk_level") ?: "UNKNOWN"
        val scannedContent = intent.getStringExtra("scanned_content") ?: "No content"
        val riskScore = intent.getIntExtra("risk_score", 0)

        // Populate the views with data
        findViewById<TextView>(R.id.tvScanId)?.text = "Scan ID: $scanId"
        findViewById<TextView>(R.id.tvJobTitle)?.text = "Job Title: $jobTitle"
        findViewById<TextView>(R.id.tvSystemRisk)?.text = riskLevel
        findViewById<TextView>(R.id.etScannedContent)?.setText(scannedContent)
        findViewById<TextView>(R.id.tvRiskScoreBox)?.text = "Risk Score: $riskScore points"

        // Setup Back Button
        val btnBack = findViewById<ImageView>(R.id.btnBackReview)
        btnBack?.setOnClickListener {
            finish()
        }

        // Setup Decision Action Buttons
        val btnLowRisk = findViewById<Button>(R.id.btnLowRisk)
        val btnHighRisk = findViewById<Button>(R.id.btnHighRisk)

        btnLowRisk?.setOnClickListener {
            Utils.showToast(this, "Marked as Low Risk / Legitimate")
            finish()
        }

        btnHighRisk?.setOnClickListener {
            Utils.showToast(this, "Flagged as High Risk / Fraud")
            finish()
        }
    }
}
