package com.recruitguard.app.jobseeker;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanScore {

    private static final String BASE_URL = "http://192.168.0.179:8080";

    public enum Risk { LOW, MEDIUM, HIGH }

    public static class Hit {
        public final String name, description;
        public final int points;
        Hit(String name, String description, int points) {
            this.name = name; this.description = description; this.points = points;
        }
    }

    public static class Result {
        public final int postingId;
        public final String input, type, riskLevel, decision;
        public final int score;
        public final List<Hit> hits;
        public final long time;
        public final boolean fromHistory; // history list has no rule details
        public boolean reviewRequested;

        Result(int postingId, String input, String type, int score, String riskLevel,
               String decision, List<Hit> hits, long time, boolean fromHistory) {
            this.postingId = postingId; this.input = input; this.type = type;
            this.score = score; this.riskLevel = riskLevel; this.decision = decision;
            this.hits = hits; this.time = time; this.fromHistory = fromHistory;
        }

        public Risk risk() {
            String l = riskLevel == null ? "" : riskLevel.toUpperCase(Locale.ROOT);
            if (l.contains("HIGH")) return Risk.HIGH;
            if (l.contains("MED")) return Risk.MEDIUM;
            if (l.contains("LOW")) return Risk.LOW;
            return score >= 50 ? Risk.HIGH : Risk.LOW;
        }
        public String label() {
            switch (risk()) {
                case HIGH: return "High Risk";
                case MEDIUM: return "Medium Risk";
                default: return "Low Risk";
            }
        }
        public int color() {
            switch (risk()) {
                case HIGH: return Color.parseColor("#D32F2F");
                case MEDIUM: return Color.parseColor("#F9A825");
                default: return Color.parseColor("#388E3C");
            }
        }
    }

    public static class Note {
        public final int id; 
        public final String title, message;
        public final long time;
        public boolean read;
        Note(int id, String title, String message, long time, boolean read) {
            this.id = id; this.title = title; this.message = message;
            this.time = time; this.read = read;
        }
    }

    public interface Callback {
        void onSuccess(Result r);
        void onError(String message);
    }

    public interface LoadCallback {
        void onDone(String json);
        void onError(String message);
    }

    /** error == null means success */
    public interface Done {
        void finished(String error);
    }

    private static final ScanScore INSTANCE = new ScanScore();
    public static ScanScore get() { return INSTANCE; }

    public final List<Result> history = new ArrayList<>();
    public final List<Note> notes = new ArrayList<>();
    public Result current;
    public boolean pendingAlert;

    private final Set<Integer> readIds = new HashSet<>(); // notifications tapped as read
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    /* ---------- logged-in user (saved by the login screen) ---------- */
    private static Context appCtx;

    public static void init(Context c) { appCtx = c.getApplicationContext(); }

    public static int userId() {
        if (appCtx == null) return 1;
        return appCtx.getSharedPreferences("RecruitGuardPrefs", Context.MODE_PRIVATE).getInt("userId", 1);
    }

    public static String token() {
        if (appCtx == null) return "";
        return appCtx.getSharedPreferences("RecruitGuardPrefs", Context.MODE_PRIVATE).getString("token", "");
    }

    /* ---------- SCAN: POST /api/scan/submit ---------- */
    public void scan(final String input, final String type, final Callback cb) {
        io.execute(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("userId", userId());
                body.put("scanType", type.toLowerCase(Locale.ROOT)); // backend enum: text, url
                body.put("content", input);

                HttpURLConnection c = (HttpURLConnection) new URL(BASE_URL + "/api/scan/submit").openConnection();
                c.setRequestMethod("POST");
                c.setConnectTimeout(10000);
                c.setReadTimeout(20000);
                c.setRequestProperty("Content-Type", "application/json");
                c.setDoOutput(true);
                try (OutputStream os = c.getOutputStream()) {
                    os.write(body.toString().getBytes(StandardCharsets.UTF_8));
                }

                final int code = c.getResponseCode();
                boolean ok = code >= 200 && code < 300;
                final String text = read(ok ? c.getInputStream() : c.getErrorStream());
                c.disconnect();

                if (!ok) {
                    final String err = text.isEmpty() ? "Server error " + code : text;
                    main.post(() -> cb.onError(err));
                    return;
                }

                final Result r = parseScan(text, input, type);
                main.post(() -> {
                    addResult(r);
                    cb.onSuccess(r);
                });
            } catch (Exception e) {
                final String msg = "Could not reach server: " + e.getMessage();
                main.post(() -> cb.onError(msg));
            }
        });
    }

    private Result parseScan(String text, String input, String type) throws Exception {
        JSONObject o = new JSONObject(text);
        List<Hit> hits = new ArrayList<>();
        JSONArray arr = o.optJSONArray("flaggedIndicators");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject h = arr.getJSONObject(i);
                hits.add(new Hit(pretty(h.optString("indicatorType")),
                        h.optString("description"), h.optInt("points")));
            }
        }
        return new Result(o.optInt("postingId"), input, type, o.optInt("riskScore"),
                o.optString("riskLevel", ""), o.optString("systemDecision", ""),
                hits, System.currentTimeMillis(), false);
    }

    private void addResult(Result r) {
        history.add(0, r);
        current = r;
        if (r.risk() == Risk.HIGH) {
            pendingAlert = true;
            addLocalNote("High-risk posting detected", "Scan scored " + r.score + "/100.");
        }
    }

    /* ---------- HISTORY: GET /api/history/{userId} ---------- */
    public void loadHistory(final Done d) {
        fetch("/api/history/" + userId(), new LoadCallback() {
            @Override public void onDone(String json) {
                try {
                    JSONArray arr = new JSONArray(json);
                    List<Result> list = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        String type = "url".equalsIgnoreCase(o.optString("scanType")) ? "URL" : "Text";
                        list.add(new Result(o.optInt("postingId"), o.optString("contentPreview"), type,
                                o.optInt("riskScore"), o.optString("riskLevel", ""), "",
                                new ArrayList<Hit>(), parseTime(o.optString("submissionDate")), true));
                    }
                    Collections.sort(list, (a, b) -> Long.compare(b.time, a.time));
                    history.clear();
                    history.addAll(list);
                    d.finished(null);
                } catch (Exception e) {
                    d.finished("Could not read history");
                }
            }
            @Override public void onError(String message) { d.finished(message); }
        });
    }

    /* ---------- NOTIFICATIONS: GET /api/notifications/{userId} ---------- */
    public void loadNotifications(final Done d) {
        fetch("/api/notifications/" + userId(), new LoadCallback() {
            @Override public void onDone(String json) {
                try {
                    JSONArray arr = new JSONArray(json);
                    List<Note> list = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        int id = o.optInt("notificationId");
                        String msg = o.optString("message");
                        String title = msg.contains("high_risk") ? "High-risk posting reviewed" : "Posting reviewed";
                        boolean read = o.optBoolean("isRead") || readIds.contains(id);
                        list.add(new Note(id, title, msg, parseTime(o.optString("createdAt")), read));
                    }
                    Collections.sort(list, (a, b) -> Long.compare(b.time, a.time));
                    notes.clear();
                    notes.addAll(list);
                    d.finished(null);
                } catch (Exception e) {
                    d.finished("Could not read notifications");
                }
            }
            @Override public void onError(String message) { d.finished(message); }
        });
    }

    public void markRead(Note n) {
        n.read = true;
        if (n.id != -1) readIds.add(n.id); // local only - no mark-read endpoint yet
    }

    /* ---------- shared helpers ---------- */
    public void fetch(final String path, final LoadCallback cb) {
        io.execute(() -> {
            try {
                HttpURLConnection c = (HttpURLConnection) new URL(BASE_URL + path).openConnection();
                c.setRequestMethod("GET");
                c.setConnectTimeout(10000);
                c.setReadTimeout(20000);
                final int code = c.getResponseCode();
                final boolean ok = code >= 200 && code < 300;
                final String text = read(ok ? c.getInputStream() : c.getErrorStream());
                c.disconnect();
                if (ok) main.post(() -> cb.onDone(text));
                else main.post(() -> cb.onError(text.isEmpty() ? "Server error " + code : text));
            } catch (Exception e) {
                final String msg = "Could not reach server: " + e.getMessage();
                main.post(() -> cb.onError(msg));
            }
        });
    }

    private long parseTime(String s) {
        try {
            if (s == null || s.length() < 19) return System.currentTimeMillis();
            Date d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(s.substring(0, 19));
            return d == null ? System.currentTimeMillis() : d.getTime();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }

    private String pretty(String s) {
        if (s == null || s.isEmpty()) return "";
        s = s.replace('_', ' ');
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String read(InputStream is) throws Exception {
        if (is == null) return "";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = is.read(buf)) != -1) out.write(buf, 0, n);
        is.close();
        return out.toString("UTF-8");
    }

    private void addLocalNote(String title, String message) {
        notes.add(0, new Note(-1, title, message, System.currentTimeMillis(), false));
    }

    public void requestReview() {
        if (current == null) return;
        current.reviewRequested = true; // local only - no review endpoint yet
        addLocalNote("Review requested", "A human reviewer will check this posting.");
    }

    public int unread() {
        int n = 0;
        for (Note x : notes) if (!x.read) n++;
        return n;
    }
}