package com.recruitguard.app

import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        val flaggedIndicators = intent.getStringArrayListExtra("flagged_indicators") ?: arrayListOf()
        val postingId = intent.getIntExtra("posting_id", 0)

        findViewById<TextView>(R.id.tvScanId)?.text = "Scan ID: $scanId"
        findViewById<TextView>(R.id.tvJobTitle)?.text = "Job Title: $jobTitle"
        findViewById<TextView>(R.id.tvSystemRisk)?.text = riskLevel.replace("_", " ")

        val fullContent = intent.getStringExtra("full_content") ?: scannedContent
        findViewById<TextView>(R.id.etScannedContent)?.setText(fullContent)

        tvRiskScore = findViewById(R.id.tvRiskScoreBox)

        cbUpfront = findViewById(R.id.cbUpfrontPayment)
        cbUnrealistic = findViewById(R.id.cbUnrealisticCompensation)
        cbFreeEmail = findViewById(R.id.cbFreeEmailDomain)
        cbOffPlatform = findViewById(R.id.cbOffPlatform)
        cbVague = findViewById(R.id.cbVagueDescription)
        cbCoercive = findViewById(R.id.cbCoerciveUrgency)

        removeBoldFromCheckboxes()
        setCheckedBoxesFromSystem(flaggedIndicators)
        calculateScore()
        setupCheckboxListeners()

        val btnBack = findViewById<ImageView>(R.id.btnBackReview)
        btnBack?.setImageResource(R.drawable.js_ic_back)
        btnBack?.setOnClickListener { finish() }

        val btnLowRisk = findViewById<Button>(R.id.btnLowRisk)
        val btnHighRisk = findViewById<Button>(R.id.btnHighRisk)

        btnLowRisk?.setOnClickListener {
            submitReview(postingId, "low_risk")
        }
        btnHighRisk?.setOnClickListener {
            submitReview(postingId, "high_risk")
        }
    }

    private fun submitReview(postingId: Int, decision: String) {
        if (postingId == 0) {
            Utils.showToast(this, "Error: Invalid posting ID")
            return
        }

        val adminId = getSharedPreferences("RecruitGuardPrefs", MODE_PRIVATE)
            .getInt("userId", 1)

        var finalScore = 0
        if (cbUpfront.isChecked) finalScore += 40
        if (cbUnrealistic.isChecked) finalScore += 20
        if (cbFreeEmail.isChecked) finalScore += 20
        if (cbOffPlatform.isChecked) finalScore += 10
        if (cbVague.isChecked) finalScore += 5
        if (cbCoercive.isChecked) finalScore += 5
        if (finalScore > 100) finalScore = 100

        val recommendations = findViewById<TextView>(R.id.etRecommendations)?.text.toString()

        val request = ReviewDecisionRequest(
            postingId,
            adminId,
            decision,
            finalScore,
            recommendations
        )


        ApiClient.getService().submitReviewDecision(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful()) {
                    Utils.showToast(this@AdminReviewActivity, "Review submitted: ${decision.replace("_", " ")}")
                    finish()
                } else {
                    Utils.showToast(this@AdminReviewActivity, "Failed to submit review")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Utils.showToast(this@AdminReviewActivity, "Network error: ${t.message}")
            }
        })
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

        if (score > 100) score = 100

        tvRiskScore.text = "Risk Score: $score points"
    }
}
