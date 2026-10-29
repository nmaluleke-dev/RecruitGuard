package com.recruitguard.app.jobseeker;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.recruitguard.app.R;

import java.util.Locale;

public class ResultActivity extends AppCompatActivity {
    private ScanScore.Result r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.js_result);
        JsUi.window(this);
        JsUi.setupBack(this);

        r = ScanScore.get().current;
        if (r == null) {
            finish();
            return;
        }

        findViewById(R.id.js_review_btn).setOnClickListener(v -> {
            ScanScore.get().requestReview();
            render();
        });
        render();

        // Show fraud alert for high risk OR upfront payment (even if score < 50)
        if (ScanScore.get().pendingAlert && !r.fromHistory) {
            ScanScore.get().pendingAlert = false;
            showFraudAlert();
        }
    }

    private void render() {
        ((TextView) findViewById(R.id.js_date)).setText(JsUi.fmtDate(r.time));
        JsUi.tint(findViewById(R.id.js_dot), r.color());

        TextView label = findViewById(R.id.js_risk_label);

        // Check if human review exists
        if (r.reviewed && r.finalDecision != null) {
            // Show admin's final decision
            String adminDecision = r.finalDecision.replace("_", " ").toUpperCase(Locale.ROOT);
            label.setText("ADMIN REVIEW: " + adminDecision);

            // Color based on admin decision, not automated score
            if (r.finalDecision.toLowerCase().contains("high")) {
                label.setTextColor(Color.parseColor("#D32F2F"));
                JsUi.tint(findViewById(R.id.js_dot), Color.parseColor("#D32F2F"));
            } else {
                label.setTextColor(Color.parseColor("#2E9E44"));
                JsUi.tint(findViewById(R.id.js_dot), Color.parseColor("#2E9E44"));
            }
        } else {
            // Show automated result
            label.setText(r.label().toUpperCase(Locale.ROOT));
            label.setTextColor(r.color());
        }

        ((TextView) findViewById(R.id.js_score_text)).setText("Risk Score: " + r.score + "/100");
        ((TextView) findViewById(R.id.js_type_text)).setText("Type: " + r.typeLabel());
        ((TextView) findViewById(R.id.js_content)).setText("\"" + r.input + "\"");

        LinearLayout flags = findViewById(R.id.js_flags);
        flags.removeAllViews();

        // Show automated scan indicators
        if (r.hits.isEmpty()) {
            JsUi.line(flags, "No suspicious rules triggered.");
        } else {
            JsUi.line(flags, "Automated Scan Results:");
            for (ScanScore.Hit h : r.hits) {
                JsUi.line(flags, "- " + h.description);
            }
        }

        // Show human review section if reviewed
        if (r.reviewed) {
            JsUi.line(flags, ""); // Spacer

            if (r.adminName != null && !r.adminName.isEmpty()) {
                JsUi.line(flags, "Reviewed by: " + r.adminName);
            }

            if (r.recommendations != null && !r.recommendations.isEmpty()) {
                JsUi.line(flags, "Admin Notes: " + r.recommendations);
            }
        }

        TextView review = findViewById(R.id.js_review_btn);

        // Disable button if: from history, already requested, or already reviewed
        if (r.fromHistory || r.reviewRequested || r.reviewed) {
            review.setEnabled(false);
            if (r.reviewed) {
                review.setText("Reviewed by Admin");
            } else if (r.reviewRequested) {
                review.setText("Review Requested");
            } else {
                review.setText("View Only");
            }
            review.setAlpha(0.5f);
        } else {
            review.setEnabled(true);
            review.setText("Request Human Review");
            review.setAlpha(1.0f);
        }
    }

    private void showFraudAlert() {
        String message;
        if (r.hasUpfrontPayment() && r.score < 50) {
            message = "WARNING: This posting requests upfront payment which is a common fraud indicator, even though the risk score is " + r.score + "/100. Do not send money. We recommend a human review.";
        } else {
            message = "This posting scored " + r.score + "/100. Do not send money or personal documents. We recommend a human review.";
        }

        new AlertDialog.Builder(this)
                .setTitle("Likely Fraud Detected")
                .setMessage(message)
                .setPositiveButton("Request Review", (d, w) -> {
                    ScanScore.get().requestReview();
                    render();
                })
                .setNegativeButton("View Details", null)
                .show();
    }
}
