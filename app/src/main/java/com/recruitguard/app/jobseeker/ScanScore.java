package com.recruitguard.app.jobseeker;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanScore {

    // Emulator ->
    private static final String BASE_URL = "http://10.0.2.2:8080";
    // user that exists in the backend database until login is built
    private static final int USER_ID = 1;

    public enum Risk { LOW, MEDIUM, HIGH }

    public static class Hit {
        public final String name, description;
        public final int points;
        Hit(String name, String description, int points) {
            this.name = name; this.description = description; this.points = points;
        }
    }

    public static class Result {
        public final String input, type, riskLevel, decision;
        public final int score;
        public final List<Hit> hits;
        public final long time;
        public boolean reviewRequested;

        Result(String input, String type, int score, String riskLevel, String decision, List<Hit> hits) {
            this.input = input; this.type = type; this.score = score;
            this.riskLevel = riskLevel; this.decision = decision;
            this.hits = hits; this.time = System.currentTimeMillis();
        }

        public Risk risk() {
            String l = riskLevel == null ? "" : riskLevel.toUpperCase(Locale.ROOT);
            if (l.contains("HIGH")) return Risk.HIGH;
            if (l.contains("MED")) return Risk.MEDIUM;
            if (l.contains("LOW")) return Risk.LOW;
            return score >= 70 ? Risk.HIGH : score >= 40 ? Risk.MEDIUM : Risk.LOW;
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
        public final String title, message;
        public final long time = System.currentTimeMillis();
        public boolean read;
        Note(String title, String message) { this.title = title; this.message = message; }
    }

    public interface Callback {
        void onSuccess(Result r);
        void onError(String message);
    }

    private static final ScanScore INSTANCE = new ScanScore();
    public static ScanScore get() { return INSTANCE; }

    public final List<Result> history = new ArrayList<>();
    public final List<Note> notes = new ArrayList<>();
    public Result current;
    public boolean pendingAlert;

    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    /** Calls POST /api/scan/submit on the backend. Callback runs on the main thread. */
    public void scan(final String input, final String type, final Callback cb) {
        io.execute(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("userId", USER_ID);
                body.put("scanType", type.toUpperCase(Locale.ROOT)); // confirm accepted values with backend
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

                final Result r = parse(text, input, type);
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

    private Result parse(String text, String input, String type) throws Exception {
        JSONObject o = new JSONObject(text);
        List<Hit> hits = new ArrayList<>();
        JSONArray arr = o.optJSONArray("flaggedIndicators");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject h = arr.getJSONObject(i);
                hits.add(new Hit(h.optString("indicatorType"), h.optString("description"), h.optInt("points")));
            }
        }
        return new Result(input, type, o.optInt("riskScore"), o.optString("riskLevel", ""),
                o.optString("systemDecision", ""), hits);
    }

    private void addResult(Result r) {
        history.add(0, r);
        current = r;
        if (r.risk() == Risk.HIGH) {
            pendingAlert = true;
            notes.add(0, new Note("High-risk posting detected", "Scan scored " + r.score + "/100."));
        }
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
        current.reviewRequested = true; // local only for now - no review endpoint yet
        notes.add(0, new Note("Review requested", "A human reviewer will check this posting."));
    }

    public int unread() {
        int n = 0;
        for (Note x : notes) if (!x.read) n++;
        return n;
    }
}