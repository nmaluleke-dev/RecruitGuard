package com.recruitguard.app.jobseeker;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.recruitguard.app.jobseeker.JsUi;
import com.recruitguard.app.jobseeker.ScanScore;
import com.recruitguard.app.R;


import java.util.Locale;

public class ResultActivity extends AppCompatActivity
{
    private ScanScore.Result r;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.js_result);
        JsUi.window(this);
        JsUi.setupBack(this);

        r = ScanScore.get().current;
        if (r == null)
        {
            finish();
            return;
        }

        findViewById(R.id.js_review_btn).setOnClickListener(v -> {
            ScanScore.get().requestReview();
            render();
        });
        render();

        // Show fraud alert only for new high-risk scans
        if (ScanScore.get().pendingAlert && !r.fromHistory)
        {
            ScanScore.get().pendingAlert = false;
            showFraudAlert();
        }
    }

    private void render()
    {
        ((TextView) findViewById(R.id.js_date)).setText(JsUi.fmtDate(r.time));
        JsUi.tint(findViewById(R.id.js_dot), r.color());

        TextView label = findViewById(R.id.js_risk_label);
        label.setText(r.label().toUpperCase(Locale.ROOT));
        label.setTextColor(r.color());

        ((TextView) findViewById(R.id.js_score_text)).setText("Risk Score: " + r.score + "/100");
        ((TextView) findViewById(R.id.js_type_text)).setText("Type: " + r.typeLabel());
        ((TextView) findViewById(R.id.js_content)).setText("\"" + r.input + "\"");

        LinearLayout flags = findViewById(R.id.js_flags);
        flags.removeAllViews();
        if (r.hits.isEmpty())
        {
            JsUi.line(flags, "No suspicious rules triggered.");
        }
        else
        {
            for (ScanScore.Hit h : r.hits) JsUi.line(flags, "- " + h.description);
        }

        TextView review = findViewById(R.id.js_review_btn);

        if(r.fromHistory || r.reviewRequested)
        {
            review.setEnabled(false);
            review.setText(r.reviewRequested ? "Review Requested" : "View Only");
            review.setAlpha(0.5f);
        }
        else
        {
            review.setEnabled(true);
            review.setText("Request Human Review");
            review.setAlpha(1.0f);
        }
    }

    private void showFraudAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Likely Fraud Detected")
                .setMessage("This posting scored " + r.score + "/100. Do not send money or personal documents. We recommend a human review.")
                .setPositiveButton("Request Review", (d, w) -> {
                    ScanScore.get().requestReview();
                    render();
                })
                .setNegativeButton("View Details", null)
                .show();
    }
}