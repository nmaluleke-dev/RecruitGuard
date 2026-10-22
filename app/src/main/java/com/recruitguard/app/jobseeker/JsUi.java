package com.recruitguard.app.jobseeker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.recruitguard.app.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JsUi {

    /* ---- call right after setContentView: status bar / keyboard spacing ---- */
    public static void window(final AppCompatActivity a) {
        EdgeToEdge.enable(a);
        final View content = a.findViewById(android.R.id.content);
        WindowCompat.getInsetsController(a.getWindow(), content).setAppearanceLightStatusBars(false);
        ViewCompat.setOnApplyWindowInsetsListener(content, (v, wi) -> {
            Insets bars = wi.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = wi.getInsets(WindowInsetsCompat.Type.ime());
            View bar = a.findViewById(R.id.js_toolbar);
            if (bar != null) bar.setPadding(0, bars.top, 0, 0);
            View panel = a.findViewById(R.id.js_drawer_panel);
            if (panel != null) panel.setPadding(0, bars.top, 0, 0);
            v.setPadding(0, 0, 0, Math.max(bars.bottom, ime.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.requestApplyInsets(content);
    }

    /* ---- hamburger + side drawer (Home, History, Notifications, Logout) ---- */
    public static void setupDrawer(final AppCompatActivity a, final int selectedId) {
        final DrawerLayout drawer = a.findViewById(R.id.js_drawer);
        ImageView navBtn = a.findViewById(R.id.js_nav_btn);
        navBtn.setImageResource(R.drawable.js_ic_menu);
        navBtn.setOnClickListener(v -> drawer.openDrawer(GravityCompat.START));
        a.findViewById(R.id.js_drawer_close).setOnClickListener(v -> drawer.closeDrawer(GravityCompat.START));

        int[] ids = {R.id.js_menu_home, R.id.js_menu_history, R.id.js_menu_notifications};
        for (int id : ids) {
            View row = a.findViewById(id);
            row.setAlpha(id == selectedId ? 1f : 0.7f);
            row.setOnClickListener(v -> go(a, id, selectedId, drawer));
        }

        // Logout menu item
        View logoutRow = a.findViewById(R.id.js_menu_logout);
        if (logoutRow != null) {
            logoutRow.setAlpha(0.7f);
            logoutRow.setOnClickListener(v -> {
                // Clear session
                a.getSharedPreferences("RecruitGuardPrefs", Context.MODE_PRIVATE)
                        .edit().clear().apply();

                // Go to sign in
                Intent intent = new Intent(a, com.recruitguard.app.SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                a.startActivity(intent);
                a.finish();
            });
        }

        refreshBadge(a);

        a.getOnBackPressedDispatcher().addCallback(a, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    a.getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    public static void refreshBadge(AppCompatActivity a) {
        View b = a.findViewById(R.id.js_menu_badge);
        if (b != null) b.setVisibility(ScanScore.get().unread() > 0 ? View.VISIBLE : View.GONE);
    }

    private static void go(AppCompatActivity a, int id, int selectedId, DrawerLayout drawer) {
        drawer.closeDrawer(GravityCompat.START);
        if (id == selectedId) return;
        Class<?> target = id == R.id.js_menu_home ? HomeActivity.class
                : id == R.id.js_menu_history ? HistoryActivity.class
                : NotificationsActivity.class;
        Intent i = new Intent(a, target);
        if (target == HomeActivity.class) {
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        a.startActivity(i);
        if (!(a instanceof HomeActivity) && target != HomeActivity.class) a.finish();
        a.overridePendingTransition(0, 0);
    }

    /* ---- back arrow (Result and Report screens) ---- */
    public static void setupBack(final AppCompatActivity a) {
        ImageView b = a.findViewById(R.id.js_nav_btn);
        b.setImageResource(R.drawable.js_ic_back);
        b.setOnClickListener(v -> a.getOnBackPressedDispatcher().onBackPressed());
    }

    /* ---- small helpers ---- */
    public static int dp(Context c, float v) {
        return (int) (v * c.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static void tint(View v, int color) {
        Drawable bg = v.getBackground().mutate();
        if (bg instanceof GradientDrawable) ((GradientDrawable) bg).setColor(color);
    }

    public static String fmtDate(long ts) {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(ts));
    }

    public static String fmtShort(long ts) {
        return new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new Date(ts));
    }

    public static String ago(long ts) {
        long min = (System.currentTimeMillis() - ts) / 60000;
        if (min < 1) return "Now";
        if (min < 60) return min + " min ago";
        long h = min / 60;
        if (h < 24) return h + (h == 1 ? " hour ago" : " hours ago");
        long d = h / 24;
        return d + (d == 1 ? " day ago" : " days ago");
    }

    public static void line(LinearLayout parent, String text) {
        Context c = parent.getContext();
        TextView t = new TextView(c);
        t.setText(text);
        t.setTextSize(15);
        t.setTextColor(Color.parseColor("#1B1B1B"));
        t.setPadding(0, dp(c, 3), 0, dp(c, 3));
        parent.addView(t);
    }

    /* ---- filter chips ---- */
    public interface OnChip {
        void picked(int index);
    }

    public static void chips(LinearLayout bar, String[] labels, int selected, final OnChip cb) {
        Context c = bar.getContext();
        bar.removeAllViews();
        for (int i = 0; i < labels.length; i++) {
            final int idx = i;
            TextView t = new TextView(c);
            t.setText(labels[i]);
            t.setTextSize(13);
            t.setPadding(dp(c, 16), dp(c, 8), dp(c, 16), dp(c, 8));
            t.setBackgroundResource(R.drawable.js_bg_chip);
            t.setSelected(i == selected);
            t.setTextColor(i == selected ? Color.WHITE : Color.parseColor("#1B1B1B"));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(dp(c, 8));
            t.setLayoutParams(lp);
            t.setOnClickListener(v -> cb.picked(idx));
            bar.addView(t);
        }
    }

    /* ---- list rows ---- */
    public static View historyRow(LayoutInflater inf, LinearLayout parent, ScanScore.Result r) {
        View v = inf.inflate(R.layout.js_item_history, parent, false);
        tint(v.findViewById(R.id.js_h_dot), r.color());
        ((TextView) v.findViewById(R.id.js_h_title)).setText(r.title());
        ((TextView) v.findViewById(R.id.js_h_type)).setText(r.typeLabel());
        ((TextView) v.findViewById(R.id.js_h_date)).setText(fmtShort(r.time));
        ((TextView) v.findViewById(R.id.js_h_preview)).setText(r.input);
        TextView view = v.findViewById(R.id.js_h_view);
        view.setPaintFlags(view.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        parent.addView(v);
        return v;
    }

    public static View noteRow(LayoutInflater inf, LinearLayout parent, ScanScore.Note n) {
        View v = inf.inflate(R.layout.js_item_notification, parent, false);
        tint(v.findViewById(R.id.js_n_avatar), Color.parseColor("#BDBDBD"));
        TextView title = v.findViewById(R.id.js_n_title);
        title.setText("Admin");
        title.setTypeface(null, n.read ? Typeface.NORMAL : Typeface.BOLD);
        ((TextView) v.findViewById(R.id.js_n_name)).setText("[" + ScanScore.get().titleFor(n.postingId) + "]");
        ((TextView) v.findViewById(R.id.js_n_time)).setText(ago(n.time));
        parent.addView(v);
        return v;
    }
}
