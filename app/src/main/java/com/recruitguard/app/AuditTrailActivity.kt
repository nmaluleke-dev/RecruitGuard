package com.recruitguard.app

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response;

class AuditTrailActivity : AppCompatActivity()
{

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AuditLogAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audit_trail)

        // Setup Back Button
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack?.setOnClickListener {
            finish()
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAuditTrail)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchAuditTrail()
    }


    private fun fetchAuditTrail() {
        ApiClient.getService().getAuditTrail().enqueue(object : Callback<List<AuditLogResponse>> {
            override fun onResponse(call: Call<List<AuditLogResponse>>, response: Response<List<AuditLogResponse>>) {
                if (response.isSuccessful() && response.body() != null) {
                    val logs = response.body()!!
                    adapter = AuditLogAdapter(logs)
                    recyclerView.adapter = adapter
                    updateStats(logs)
                } else {
                    Toast.makeText(this@AuditTrailActivity, "Failed to load audit trail", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<AuditLogResponse>>, t: Throwable) {
                Toast.makeText(this@AuditTrailActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateStats(logs: List<AuditLogResponse>) {
        val totalAudited = logs.size
        val highRiskCount = logs.count { it.decision == "Flagged" }
        val lowRiskCount = logs.count { it.decision == "Cleared" }

        findViewById<TextView>(R.id.tvTotalAudited)?.text = "Total Postings Audited: $totalAudited"
        findViewById<TextView>(R.id.tvTotalHighRisk)?.text = "Total High Risk (Fraud): $highRiskCount"
        findViewById<TextView>(R.id.tvTotalLowRisk)?.text = "Total Low Risk (Legitimate): $lowRiskCount"
    }
}

// Adapter using AuditLogResponse
class AuditLogAdapter(private val logs: List<AuditLogResponse>) :
    RecyclerView.Adapter<AuditLogAdapter.ViewHolder>() {

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val tvScanId: TextView = itemView.findViewById(R.id.tvScanId)
        val tvAuditDate: TextView = itemView.findViewById(R.id.tvAuditDate)
        val tvRiskScore: TextView = itemView.findViewById(R.id.tvRiskScore)
        val tvDecision: TextView = itemView.findViewById(R.id.tvDecision)
        val tvAdminName: TextView = itemView.findViewById(R.id.tvAdminName)
        val tvAuditTimestamp: TextView = itemView.findViewById(R.id.tvAuditTimestamp)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_audit_log, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val log = logs[position]
        holder.tvScanId.text = "Scan ID: ${log.scanId}"
        holder.tvAuditDate.text = "Date: ${log.date}"
        holder.tvRiskScore.text = "Risk Score: ${log.riskScore}"
        holder.tvDecision.text = "Decision: ${log.decision}"
        holder.tvAdminName.text = "Admin: ${log.adminName}"
        holder.tvAuditTimestamp.text = log.timestamp

        // Color code decision
        if (log.decision == "Flagged") {
            holder.tvDecision.setTextColor(Color.parseColor("#D32F2F")) // Red
        } else {
            holder.tvDecision.setTextColor(Color.parseColor("#2E7D32")) // Green
        }

        // Color code risk
        if (log.riskScore.contains("HIGH")) {
            holder.tvRiskScore.setTextColor(Color.parseColor("#D32F2F"))
        } else {
            holder.tvRiskScore.setTextColor(Color.parseColor("#2E7D32"))
        }
    }

    override fun getItemCount() = logs.size
}