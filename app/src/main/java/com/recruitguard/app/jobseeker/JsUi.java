package com.recruitguard.app.jobseeker;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.recruitguard.app.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JsUi {

    public static void setupNav(final Activity a, final int selectedId) {
        BottomNavigationView nav = a.findViewById(R.id.js_bottom_nav);
        nav.setOnItemSelectedListener(null);
        nav.setSelectedItemId(selectedId);
        BadgeDrawable badge = nav.getOrCreateBadge(R.id.js_nav_alerts);
        badge.setVisible(ScanScore.get().unread() > 0);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedId) return true;
            Class<?> target = id == R.id.js_nav_scan ? HomeActivity.class
                    : id == R.id.js_nav_history ? HistoryActivity.class
                    : NotificationsActivity.class;
            a.startActivity(new Intent(a, target));
            a.overridePendingTransition(0, 0);
            a.finish();
            return true;
        });
    }

    public static void tint(View v, int color) {
        Drawable bg = v.getBackground().mutate();
        if (bg instanceof GradientDrawable) ((GradientDrawable) bg).setColor(color);
    }

    public static String fmt(long ts) {
        return new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date(ts));
    }

    
    public static View row(LayoutInflater inf, ViewGroup parent, String badge, int color,
                           String title, String sub) {
        View v = inf.inflate(R.layout.js_row, parent, false);
        TextView b = v.findViewById(R.id.js_row_badge);
        if (badge == null) b.setVisibility(View.GONE);
        else { b.setText(badge); tint(b, color); }
        ((TextView) v.findViewById(R.id.js_row_title)).setText(title);
        ((TextView) v.findViewById(R.id.js_row_sub)).setText(sub);
        parent.addView(v);
        return v;
    }
}
