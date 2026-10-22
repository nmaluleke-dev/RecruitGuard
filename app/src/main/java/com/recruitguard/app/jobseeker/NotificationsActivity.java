package com.recruitguard.app.jobseeker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.recruitguard.app.R;
import com.recruitguard.app.jobseeker.ResultActivity;

public class NotificationsActivity extends AppCompatActivity
{
    private static final String[] FILTERS = {"All", "Read", "Unread"};
    private int filter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.js_notifications);
        JsUi.window(this);
        JsUi.setupDrawer(this, R.id.js_menu_notifications);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        render();
        // history first, so each notification can show the posting's name
        ScanScore.get().loadHistory(e1 ->
                ScanScore.get().loadNotifications(e2 -> {
                    if (e2 != null) Toast.makeText(this, e2, Toast.LENGTH_LONG).show();
                    render();
                }));
    }

    private void render()
    {
        JsUi.chips((LinearLayout) findViewById(R.id.js_chips), FILTERS, filter, i -> {
            filter = i;
            render();
        });

        LinearLayout list = findViewById(R.id.js_list);
        list.removeAllViews();

        for (ScanScore.Note n : ScanScore.get().notes)
        {
            if (filter == 1 && !n.read) continue;
            if (filter == 2 && n.read) continue;

            View row = JsUi.noteRow(getLayoutInflater(), list, n);
            row.setOnClickListener(v -> {
                ScanScore.get().markRead(n);
                ScanScore.Result r = ScanScore.get().findByPosting(n.postingId);
                if (r != null)
                {
                    ScanScore.get().current = r;
                    startActivity(new Intent(this, ResultActivity.class));
                }
                else
                {
                    Toast.makeText(this, "Go to Scan History to view this report.", Toast.LENGTH_SHORT).show();
                    render();
                }
            });
        }
        findViewById(R.id.js_empty).setVisibility(list.getChildCount() == 0 ? View.VISIBLE : View.GONE);
        JsUi.refreshBadge(this);
    }
}