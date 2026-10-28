package com.recruitguard.app

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuditTrailActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AuditLogAdapter
    private lateinit var btnAllLogs: Button
    private lateinit var btnFraudHigh: Button
    private lateinit var btnLegitimate: Button
    private lateinit var etSearch: EditText
    private var allLogs: List<AuditLogResponse> = listOf()
    private var currentFilter = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audit_trail)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack?.setImageResource(R.drawable.js_ic_back)
        btnBack?.setOnClickListener { finish() }

        recyclerView = findViewById(R.id.recyclerViewAuditTrail)
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnAllLogs = findViewById(R.id.btnFilterAllLogs)
        btnFraudHigh = findViewById(R.id.btnFilterHighRisk)
        btnLegitimate = findViewById(R.id.btnFilterLegitimate)
        etSearch = findViewById(R.id.etSearchAudit)

        btnAllLogs.setOnClickListener { currentFilter = "all"; applyFilterAndSearch() }
        btnFraudHigh.setOnClickListener { currentFilter = "high"; applyFilterAndSearch() }
        btnLegitimate.setOnClickListener { currentFilter = "low"; applyFilterAndSearch() }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                applyFilterAndSearch()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Fetch real data from API
        fetchAuditTrail()
    }

    private fun fetchAuditTrail() {
        ApiClient.getService().getAuditTrail().enqueue(object : Callback<List<AuditLogResponse>> {
            override fun onResponse(
                call: Call<List<AuditLogResponse>>,
                response: Response<List<AuditLogResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    allLogs = response.body()!!
                    adapter = AuditLogAdapter(allLogs)
                    recyclerView.adapter = adapter
                    updateStats(allLogs)
                    updateButtonStates("all")
                } else {
                    Toast.makeText(
                        this@AuditTrailActivity,
                        "Failed to load audit trail",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<AuditLogResponse>>, t: Throwable) {
                Toast.makeText(
                    this@AuditTrailActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun applyFilterAndSearch() {
        val searchQuery = etSearch.text.toString().lowercase()
        var filtered = allLogs

        filtered = when (currentFilter) {
            "high" -> filtered.filter { it.finalDecision.contains("high", ignoreCase = true) }
            "low" -> filtered.filter { it.finalDecision.contains("low", ignoreCase = true) }
            else -> filtered
        }

        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.scanId.contains(searchQuery, ignoreCase = true) ||
                        it.adminName.contains(searchQuery, ignoreCase = true) ||
                        it.finalDecision.contains(searchQuery, ignoreCase = true)
            }
        }

        adapter.updateData(filtered)
        updateButtonStates(currentFilter)
    }

    private fun updateButtonStates(selected: String) {
        btnAllLogs.alpha = if (selected == "all") 1.0f else 0.7f
        btnFraudHigh.alpha = if (selected == "high") 1.0f else 0.7f
        btnLegitimate.alpha = if (selected == "low") 1.0f else 0.7f
    }

    private fun updateStats(logs: List<AuditLogResponse>) {
        val totalAudited = logs.size
        val highRiskCount = logs.count { it.finalDecision.contains("high", ignoreCase = true) }
        val lowRiskCount = logs.count { it.finalDecision.contains("low", ignoreCase = true) }

        findViewById<TextView>(R.id.tvTotalAudited)?.text = "Total Postings Audited: $totalAudited"
        findViewById<TextView>(R.id.tvTotalHighRisk)?.text =
            "Total High Risk (Fraud): $highRiskCount"
        findViewById<TextView>(R.id.tvTotalLowRisk)?.text =
            "Total Low Risk (Legitimate): $lowRiskCount"
    }
}

class AuditLogAdapter(private var logs: List<AuditLogResponse>) :
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
        holder.tvRiskScore.text = "Risk Score: ${log.riskScore}%"
        holder.tvDecision.text = "Decision: ${log.finalDecision.replace("_", " ")}"
        holder.tvAdminName.text = "Admin: ${log.adminName}"
        holder.tvAuditTimestamp.text = log.timestamp

        if (log.finalDecision.contains("high", ignoreCase = true)) {
            holder.tvDecision.setTextColor(Color.parseColor("#D32F2F"))
        } else {
            holder.tvDecision.setTextColor(Color.parseColor("#2E7D32"))
        }

        if (log.riskScore >= 50) {
            holder.tvRiskScore.setTextColor(Color.parseColor("#D32F2F"))
        } else {
            holder.tvRiskScore.setTextColor(Color.parseColor("#2E7D32"))
        }
    }

    override fun getItemCount() = logs.size

    fun updateData(newLogs: List<AuditLogResponse>) {
        this.logs = newLogs
        notifyDataSetChanged()
    }
}