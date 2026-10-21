package com.recruitguard.app.jobseeker;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
        public final String input, type, riskLevel;
        public final int score;
        public final List<Hit> hits;
        public final long time;
        public final boolean fromHistory; // history list has no rule details
        public boolean reviewRequested;

        Result(int postingId, String input, String type, int score, String riskLevel,
               List<Hit> hits, long time, boolean fromHistory) {
            this.postingId = postingId; this.input = input; this.type = type;
            this.score = score; this.riskLevel = riskLevel; this.hits = hits;
            this.time = time; this.fromHistory = fromHistory;
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
                default: return Color.parseColor("#2E9E44");
            }
        }
        public String typeLabel() { return type.equals("URL") ? "URL Scan" : "Text Scan"; }

        // The backend sends no job title, so build one from the content
        public String title() {
            String s = input == null ? "" : input.trim();
            if (type.equals("URL")) {
                s = s.replaceFirst("(?i)^https?://", "").replaceFirst("(?i)^www\\.", "");
                int slash = s.indexOf('/');
                String host = slash > 0 ? s.substring(0, slash) : s;
                return host.isEmpty() ? "Job link" : host;
            }
            s = s.replaceAll("\\s+", " ");
            if (s.isEmpty()) return "Job posting";
            if (s.length() <= 36) return s;
            int cut = s.lastIndexOf(' ', 36);
            return s.substring(0, cut > 10 ? cut : 36) + "...";
        }
    }

    public static class Note {
        public final int id, postingId;
        public final String message;
        public final long time;
        public boolean read;
        Note(int id, int postingId, String message, long time, boolean read) {
            this.id = id; this.postingId = postingId; this.message = message;
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

    private final Map<Integer, Result> session = new HashMap<>();   // scans made in this app run (have rule details)
    private final Map<Integer, String> reviews = new HashMap<>();   // postingId -> admin review message
    private final Set<Integer> readIds = new HashSet<>();           // notifications opened (local only)
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    /* ---------- logged-in user (saved by the login screen) ---------- */
    private static Context appCtx;

    public static void init(Context c) { appCtx = c.getApplicationContext(); }

    private static SharedPreferences prefs() {
        return appCtx == null ? null : appCtx.getSharedPreferences("RecruitGuardPrefs", Context.MODE_PRIVATE);
    }
    public static int userId() {
        SharedPreferences p = prefs();
        return p == null ? 1 : p.getInt("userId", 1);
    }
    public static String userName() {
        SharedPreferences p = prefs();
        return p == null ? "" : p.getString("name", "");
    }
    public static String token() {
        SharedPreferences p = prefs();
        return p == null ? "" : p.getString("token", "");
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
                o.optString("riskLevel", ""), hits, System.currentTimeMillis(), false);
    }

    private void addResult(Result r) {
        history.add(0, r);
        session.put(r.postingId, r);
        current = r;
        pendingAlert = r.score >= 50;
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
                        int id = o.optInt("postingId");
                        Result local = session.get(id);
                        if (local != null) { list.add(local); continue; }
                        String type = "url".equalsIgnoreCase(o.optString("scanType")) ? "URL" : "Text";
                        list.add(new Result(id, o.optString("contentPreview"), type, o.optInt("riskScore"),
                                o.optString("riskLevel", ""), new ArrayList<Hit>(),
                                parseTime(o.optString("submissionDate")), true));
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
                    reviews.clear();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        int id = o.optInt("notificationId");
                        int pid = o.optInt("postingId");
                        String msg = o.optString("message");
                        boolean read = o.optBoolean("isRead") || readIds.contains(id);
                        list.add(new Note(id, pid, msg, parseTime(o.optString("createdAt")), read));
                        reviews.put(pid, msg);
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
        readIds.add(n.id); // local only - no mark-read endpoint yet
    }

    public Result findByPosting(int postingId) {
        for (Result r : history) if (r.postingId == postingId) return r;
        return null;
    }

    public String titleFor(int postingId) {
        Result r = findByPosting(postingId);
        return r != null ? r.title() : "Posting #" + postingId;
    }

    public String reviewFor(int postingId) { return reviews.get(postingId); }

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

    public void requestReview() {
        if (current == null) return;
        current.reviewRequested = true; // local only - backend has no review endpoint yet
    }

    public int unread() {
        int n = 0;
        for (Note x : notes) if (!x.read) n++;
        return n;
    }
}