package com.recruitguard.app.jobseeker;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.recruitguard.app.R;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.js_notifications);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        JsUi.setupNav(this, R.id.js_nav_alerts);

        LinearLayout list = findViewById(R.id.js_list);
        list.removeAllViews();
        findViewById(R.id.js_empty).setVisibility(
                ScanScore.get().notes.isEmpty() ? View.VISIBLE : View.GONE);

        for (ScanScore.Note n : ScanScore.get().notes) {
            View row = JsUi.row(getLayoutInflater(), list, null, 0, n.title,
                    n.message + "\n" + JsUi.fmt(n.time));
            ((TextView) row.findViewById(R.id.js_row_title))
                    .setTypeface(null, n.read ? Typeface.NORMAL : Typeface.BOLD);
            row.setOnClickListener(v -> {
                n.read = true;
                refresh();
            });
        }
    }
}