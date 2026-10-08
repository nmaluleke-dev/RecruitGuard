package com.recruitguard.app.jobseeker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.recruitguard.app.R;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.js_history);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JsUi.setupNav(this, R.id.js_nav_history);

        LinearLayout list = findViewById(R.id.js_list);
        list.removeAllViews();
        findViewById(R.id.js_empty).setVisibility(
                ScanScore.get().history.isEmpty() ? View.VISIBLE : View.GONE);

        for (ScanScore.Result s : ScanScore.get().history) {
            View row = JsUi.row(getLayoutInflater(), list, String.valueOf(s.score), s.color(), s.input,
                    s.label() + " • " + s.type + " • " + JsUi.fmt(s.time));
            row.setOnClickListener(v -> {
                ScanScore.get().current = s;
                ScanScore.get().pendingAlert = false;
                startActivity(new Intent(this, ResultActivity.class));
            });
        }
    }
}