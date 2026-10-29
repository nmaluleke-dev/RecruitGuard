package com.recruitguard.app;

import android.content.Intent;
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

public class AdminDashboardActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private FlaggedJobAdapter adapter;
    private Button btnFilterAll, btnFilterLowRisk, btnFilterHighRisk;
    private TextView tvWelcomeAdmin;
    private List<FlaggedJobResponse> allJobs = new ArrayList<>();
    private List<FlaggedJobResponse> reviewRequests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        ImageView btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        findViewById(R.id.admin_drawer_close).setOnClickListener(v ->
                drawerLayout.closeDrawer(GravityCompat.START));

        View homeMenu = findViewById(R.id.admin_menu_home);
        View auditMenu = findViewById(R.id.admin_menu_audit);
        View logoutMenu = findViewById(R.id.admin_menu_logout);

        homeMenu.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));
        auditMenu.setOnClickListener(v -> {
            startActivity(new Intent(this, AuditTrailActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        logoutMenu.setOnClickListener(v -> {
            getSharedPreferences("RecruitGuardPrefs", MODE_PRIVATE).edit().clear().apply();
            Intent intent = new Intent(this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        homeMenu.setAlpha(1.0f);
        auditMenu.setAlpha(0.7f);
        logoutMenu.setAlpha(0.7f);

        recyclerView = findViewById(R.id.recyclerViewFlaggedJobs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
            intent.putExtra("posting_id", job.getPostingId());
            intent.putExtra("full_content", job.getFullContent());

            ArrayList<String> indicators = new ArrayList<>();
            if (job.getFlaggedIndicators() != null) {
                for (FlaggedJobResponse.Indicator ind : job.getFlaggedIndicators()) {
                    indicators.add(ind.getIndicatorType());
                }
            }
            intent.putStringArrayListExtra("flagged_indicators", indicators);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterLowRisk = findViewById(R.id.btnFilterLowRisk);
        btnFilterHighRisk = findViewById(R.id.btnFilterHighRisk);
        tvWelcomeAdmin = findViewById(R.id.tvWelcomeAdmin);

        btnFilterAll.setOnClickListener(v -> filterJobs("all"));
        btnFilterLowRisk.setOnClickListener(v -> filterJobs("low"));
        btnFilterHighRisk.setOnClickListener(v -> filterJobs("high"));

        String adminName = getSharedPreferences("RecruitGuardPrefs", MODE_PRIVATE)
                .getString("name", "Admin");
        tvWelcomeAdmin.setText("Welcome back, " + adminName);

        fetchFlaggedJobs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchFlaggedJobs();
    }

    private void fetchFlaggedJobs() {
        ApiService apiService = ApiClient.getService();
        apiService.getFlaggedJobs().enqueue(new Callback<List<FlaggedJobResponse>>() {
            @Override
            public void onResponse(Call<List<FlaggedJobResponse>> call, Response<List<FlaggedJobResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allJobs = response.body();
                    // Filter: Only show items where review was requested AND not yet reviewed
                    reviewRequests.clear();
                    for (FlaggedJobResponse job : allJobs) {
                        if (!job.isReviewed() && job.isReviewRequested()) {
                            reviewRequests.add(job);
                        }
                    }
                    filterJobs("all");
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

        if (filter.equals("low")) {
            for (FlaggedJobResponse job : reviewRequests) {
                if (job.getRiskLevel().equalsIgnoreCase("low_risk")) {
                    filtered.add(job);
                }
            }
        } else if (filter.equals("high")) {
            for (FlaggedJobResponse job : reviewRequests) {
                if (job.getRiskLevel().equalsIgnoreCase("high_risk")) {
                    filtered.add(job);
                }
            }
        } else {
            filtered = new ArrayList<>(reviewRequests);
        }

        adapter.updateData(filtered);
        btnFilterAll.setAlpha(filter.equals("all") ? 1.0f : 0.7f);
        btnFilterLowRisk.setAlpha(filter.equals("low") ? 1.0f : 0.7f);
        btnFilterHighRisk.setAlpha(filter.equals("high") ? 1.0f : 0.7f);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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
            String displayRisk = job.getRiskLevel().replace("_", " ");
            holder.tvRiskStatus.setText(displayRisk.toUpperCase());
            holder.tvScanType.setText("- " + job.getScanType());
            holder.tvTimeStamp.setText(job.getCreatedAt());

            if (job.getRiskLevel().equalsIgnoreCase("high_risk")) {
                holder.tvRiskStatus.setTextColor(0xFFD32F2F);
            } else {
                holder.tvRiskStatus.setTextColor(0xFF2E7D32);
            }

            holder.itemView.setOnClickListener(v -> listener.onItemClick(job));
        }

        @Override
        public int getItemCount() { return jobs.size(); }

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
