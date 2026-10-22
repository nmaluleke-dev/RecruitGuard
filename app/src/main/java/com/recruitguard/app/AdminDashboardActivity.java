package com.recruitguard.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity
{
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private FlaggedJobAdapter adapter;
    private Button btnFilterAll, btnFilterLowRisk, btnFilterHighRisk;
    private TextView tvWelcomeAdmin;
    private List<FlaggedJobResponse> allJobs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize Drawer
        drawerLayout = findViewById(R.id.drawer_layout);

        // Setup burger menu
        ImageView btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Drawer close button
        findViewById(R.id.admin_drawer_close).setOnClickListener(v ->
                drawerLayout.closeDrawer(GravityCompat.START));

        // Drawer menu items
        View homeMenu = findViewById(R.id.admin_menu_home);
        View auditMenu = findViewById(R.id.admin_menu_audit);
        View logoutMenu = findViewById(R.id.admin_menu_logout);

        homeMenu.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        auditMenu.setOnClickListener(v -> {
            startActivity(new Intent(this, AuditTrailActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        logoutMenu.setOnClickListener(v -> {
            // Clear session
            getSharedPreferences("RecruitGuardPrefs", MODE_PRIVATE)
                    .edit().clear().apply();
            // Go to sign in
            Intent intent = new Intent(this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Highlight current menu item
        homeMenu.setAlpha(1.0f);
        auditMenu.setAlpha(0.7f);
        logoutMenu.setAlpha(0.7f);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewFlaggedJobs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with click listener
        adapter = new FlaggedJobAdapter(new ArrayList<>(), job -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminReviewActivity.class);
            intent.putExtra("scan_id", job.getScanId());
            intent.putExtra("user_name", job.getUserName());
            intent.putExtra("risk_level", job.getRiskLevel());
            intent.putExtra("scan_type", job.getScanType());
            intent.putExtra("job_title", job.getJobTitle());
            intent.putExtra("scanned_content", job.getScannedContent());
            intent.putExtra("system_risk", job.getSystemRisk());
            intent.putExtra("risk_score", job.getRiskScore());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // Initialize filter buttons
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterLowRisk = findViewById(R.id.btnFilterLowRisk);
        btnFilterHighRisk = findViewById(R.id.btnFilterHighRisk);
        tvWelcomeAdmin = findViewById(R.id.tvWelcomeAdmin);

        // Setup filter buttons
        btnFilterAll.setOnClickListener(v -> filterJobs("all"));
        btnFilterLowRisk.setOnClickListener(v -> filterJobs("low"));
        btnFilterHighRisk.setOnClickListener(v -> filterJobs("high"));

        // Set admin name
        String adminName = getSharedPreferences("RecruitGuardPrefs", MODE_PRIVATE)
                .getString("name", "Admin");
        tvWelcomeAdmin.setText("Welcome back, " + adminName);

        // Load initial data
        fetchFlaggedJobs();
    }

    private void fetchFlaggedJobs() {
        ApiService apiService = ApiClient.getService();

        apiService.getFlaggedJobs().enqueue(new Callback<List<FlaggedJobResponse>>() {
            @Override
            public void onResponse(Call<List<FlaggedJobResponse>> call, Response<List<FlaggedJobResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allJobs = response.body();
                    filterJobs("all");
                    updateFilterCounts(allJobs);
                } else {
                    Toast.makeText(AdminDashboardActivity.this, "Failed to load flagged jobs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FlaggedJobResponse>> call, Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterJobs(String filter) {
        List<FlaggedJobResponse> filtered = new ArrayList<>();
        switch (filter) {
            case "low":
                for (FlaggedJobResponse job : allJobs) {
                    if (job.getRiskLevel().equalsIgnoreCase("LOW RISK")) filtered.add(job);
                }
                break;
            case "high":
                for (FlaggedJobResponse job : allJobs) {
                    if (job.getRiskLevel().equalsIgnoreCase("HIGH RISK")) filtered.add(job);
                }
                break;
            default:
                filtered = new ArrayList<>(allJobs);
        }
        adapter.updateData(filtered);
    }

    private void updateFilterCounts(List<FlaggedJobResponse> jobs) {
        int highCount = 0, lowCount = 0;
        for (FlaggedJobResponse job : jobs) {
            if (job.getRiskLevel().equalsIgnoreCase("HIGH RISK")) highCount++;
            else lowCount++;
        }

        btnFilterAll.setText("All");
        btnFilterLowRisk.setText("Low Risk");
        btnFilterHighRisk.setText("High Risk");
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Adapter using FlaggedJobResponse
    public static class FlaggedJobAdapter extends RecyclerView.Adapter<FlaggedJobAdapter.ViewHolder> {
        private List<FlaggedJobResponse> jobs;
        private OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(FlaggedJobResponse job);
        }

        public FlaggedJobAdapter(List<FlaggedJobResponse> jobs, OnItemClickListener listener) {
            this.jobs = jobs;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_flagged_job, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FlaggedJobResponse job = jobs.get(position);

            holder.tvUserNameScanId.setText(job.getUserName() + " • " + job.getScanId());
            holder.tvRiskStatus.setText(job.getRiskLevel());
            holder.tvScanType.setText("- " + job.getScanType());
            holder.tvTimeStamp.setText(job.getCreatedAt());

            if (job.getRiskLevel().equalsIgnoreCase("HIGH RISK")) {
                holder.tvRiskStatus.setTextColor(0xFFD32F2F);
            } else {
                holder.tvRiskStatus.setTextColor(0xFF2E7D32);
            }

            holder.itemView.setOnClickListener(v -> listener.onItemClick(job));
        }

        @Override
        public int getItemCount() {
            return jobs.size();
        }

        public void updateData(List<FlaggedJobResponse> newJobs) {
            this.jobs = newJobs;
            notifyDataSetChanged();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvUserNameScanId, tvRiskStatus, tvScanType, tvTimeStamp;

            ViewHolder(View itemView) {
                super(itemView);
                tvUserNameScanId = itemView.findViewById(R.id.tvUserNameScanId);
                tvRiskStatus = itemView.findViewById(R.id.tvRiskStatus);
                tvScanType = itemView.findViewById(R.id.tvScanType);
                tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            }
        }
    }
}
