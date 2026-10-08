package com.recruitguard.app.jobseeker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.recruitguard.app.R;

public class ResultActivity extends AppCompatActivity {
    private ScanScore.Result r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.js_result);

        r = ScanScore.get().current;
        if (r == null) { finish(); return; }

        findViewById(R.id.js_back).setOnClickListener(v -> finish());
        findViewById(R.id.js_review_btn).setOnClickListener(v -> {
            ScanScore.get().requestReview();
            render();
        });
        render();

        if (ScanScore.get().pendingAlert) {
            ScanScore.get().pendingAlert = false;
            showFraudAlert();
        }
    }

    private void render() {
        TextView score = findViewById(R.id.js_score);
        score.setText(String.valueOf(r.score));
        JsUi.tint(score, r.color());

        TextView label = findViewById(R.id.js_label);
        label.setText(r.label());
        label.setTextColor(r.color());

        ((TextView) findViewById(R.id.js_input_text)).setText(r.input);

        LinearLayout hits = findViewById(R.id.js_hits);
        hits.removeAllViews();
        if (r.hits.isEmpty()) {
            TextView none = new TextView(this);
            none.setText("No suspicious rules triggered.");
            hits.addView(none);
        } else {
            for (ScanScore.Hit h : r.hits) {
                JsUi.row(getLayoutInflater(), hits, "+" + h.points, r.color(), h.name, h.description);
            }
        }

        Button review = findViewById(R.id.js_review_btn);
        review.setEnabled(!r.reviewRequested);
        review.setText(r.reviewRequested ? "Review Requested " : "Request Human Review");
    }

    private void showFraudAlert() {
        new AlertDialog.Builder(this)
                .setTitle(" Likely Fraud Detected")
                .setMessage("This posting scored " + r.score + "/100. Do not send money or personal documents. We recommend a human review.")
                .setPositiveButton("Request Review", (d, w) -> {
                    ScanScore.get().requestReview();
                    render();
                })
                .setNegativeButton("View Details", null)
                .show();
    }
}
