package com.recruitguard.app.jobseeker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.recruitguard.app.R;


public class HistoryActivity extends AppCompatActivity
{
    private static final String[] FILTERS = {"All", "URL Scan", "Text Scan", "Low Risk", "High Risk"};
    private int filter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.js_history);
        JsUi.window(this);
        JsUi.setupDrawer(this, R.id.js_menu_history);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        render();
        ScanScore.get().loadHistory(error -> {
            if (error != null) Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            else render();
        });
        ScanScore.get().loadNotifications(error -> JsUi.refreshBadge(this));
    }

    private void render() {
        JsUi.chips((LinearLayout) findViewById(R.id.js_chips), FILTERS, filter, i -> {
            filter = i;
            render();
        });

        LinearLayout list = findViewById(R.id.js_list);
        list.removeAllViews();

        for (ScanScore.Result s : ScanScore.get().history)
        {
            if (filter == 1 && !s.type.equals("URL")) continue;
            if (filter == 2 && !s.type.equals("Text")) continue;
            if (filter == 3 && s.risk() != ScanScore.Risk.LOW) continue;
            if (filter == 4 && s.risk() != ScanScore.Risk.HIGH) continue;

            View row = JsUi.historyRow(getLayoutInflater(), list, s);
            row.setOnClickListener(v -> {
                ScanScore.get().current = s;
                startActivity(new Intent(this, ResultActivity.class));
            });
        }
        findViewById(R.id.js_empty).setVisibility(list.getChildCount() == 0 ? View.VISIBLE : View.GONE);
    }
}