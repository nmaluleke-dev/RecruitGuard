package com.recruitguard.app

import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AdminReviewActivity : AppCompatActivity() {

    private lateinit var cbUpfront: CheckBox
    private lateinit var cbUnrealistic: CheckBox
    private lateinit var cbFreeEmail: CheckBox
    private lateinit var cbOffPlatform: CheckBox
    private lateinit var cbVague: CheckBox
    private lateinit var cbCoercive: CheckBox
    private lateinit var tvRiskScore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_review)

        val scanId = intent.getStringExtra("scan_id") ?: "CAS-000"
        val jobTitle = intent.getStringExtra("job_title") ?: "Job Posting"
        val riskLevel = intent.getStringExtra("risk_level") ?: "UNKNOWN"
        val scannedContent = intent.getStringExtra("scanned_content") ?: "No content"
        val riskScore = intent.getIntExtra("risk_score", 0)
        val flaggedIndicators = intent.getStringArrayListExtra("flagged_indicators") ?: arrayListOf()

        findViewById<TextView>(R.id.tvScanId)?.text = "Scan ID: $scanId"
        findViewById<TextView>(R.id.tvJobTitle)?.text = "Job Title: $jobTitle"
        findViewById<TextView>(R.id.tvSystemRisk)?.text = riskLevel.replace("_", " ")
        findViewById<TextView>(R.id.etScannedContent)?.setText(scannedContent)

        tvRiskScore = findViewById(R.id.tvRiskScoreBox)
        tvRiskScore.text = "Risk Score: $riskScore points"

        cbUpfront = findViewById(R.id.cbUpfrontPayment)
        cbUnrealistic = findViewById(R.id.cbUnrealisticCompensation)
        cbFreeEmail = findViewById(R.id.cbFreeEmailDomain)
        cbOffPlatform = findViewById(R.id.cbOffPlatform)
        cbVague = findViewById(R.id.cbVagueDescription)
        cbCoercive = findViewById(R.id.cbCoerciveUrgency)

        removeBoldFromCheckboxes()
        setCheckedBoxesFromSystem(flaggedIndicators)
        setupCheckboxListeners()

        // Fixed back arrow - straight arrow like client side
        val btnBack = findViewById<ImageView>(R.id.btnBackReview)
        btnBack?.setImageResource(R.drawable.js_ic_back)
        btnBack?.setOnClickListener { finish() }

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

    private fun setCheckedBoxesFromSystem(indicators: List<String>) {
        for (indicator in indicators) {
            when (indicator.lowercase()) {
                "upfront_payment" -> cbUpfront.isChecked = true
                "unrealistic_salary" -> cbUnrealistic.isChecked = true
                "suspicious_email" -> cbFreeEmail.isChecked = true
                "off_platform" -> cbOffPlatform.isChecked = true
                "vague_description" -> cbVague.isChecked = true
                "coercive_urgency" -> cbCoercive.isChecked = true
            }
        }
    }

    private fun removeBoldFromCheckboxes() {
        cbUpfront.setTypeface(null, Typeface.NORMAL)
        cbUnrealistic.setTypeface(null, Typeface.NORMAL)
        cbFreeEmail.setTypeface(null, Typeface.NORMAL)
        cbOffPlatform.setTypeface(null, Typeface.NORMAL)
        cbVague.setTypeface(null, Typeface.NORMAL)
        cbCoercive.setTypeface(null, Typeface.NORMAL)
    }

    private fun setupCheckboxListeners() {
        val listener = { _: android.widget.CompoundButton, _: Boolean -> calculateScore() }
        cbUpfront.setOnCheckedChangeListener(listener)
        cbUnrealistic.setOnCheckedChangeListener(listener)
        cbFreeEmail.setOnCheckedChangeListener(listener)
        cbOffPlatform.setOnCheckedChangeListener(listener)
        cbVague.setOnCheckedChangeListener(listener)
        cbCoercive.setOnCheckedChangeListener(listener)
    }

    private fun calculateScore() {
        var score = 0
        if (cbUpfront.isChecked) score += 40
        if (cbUnrealistic.isChecked) score += 20
        if (cbFreeEmail.isChecked) score += 20
        if (cbOffPlatform.isChecked) score += 10
        if (cbVague.isChecked) score += 5
        if (cbCoercive.isChecked) score += 5
        tvRiskScore.text = "Risk Score: $score points"
    }
}
