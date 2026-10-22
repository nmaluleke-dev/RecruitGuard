package com.recruitguard.app.jobseeker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class ScanScore {

    public enum Risk { LOW, HIGH }

    public static class Hit {
        public final String name, description;
        public final int points;
        Hit(String name, String description, int points) {
            this.name = name;
            this.description = description;
            this.points = points;
        }
    }

    public static class Result {
        public final int postingId;
        public final String input, type, riskLevel;
        public final int score;
        public final List<Hit> hits;
        public final long time;
        public final boolean fromHistory;
        public boolean reviewRequested;

        Result(int postingId, String input, String type, int score, String riskLevel,
               List<Hit> hits, long time, boolean fromHistory) {
            this.postingId = postingId;
            this.input = input;
            this.type = type;
            this.score = score;
            this.riskLevel = riskLevel;
            this.hits = hits;
            this.time = time;
            this.fromHistory = fromHistory;
        }

        public Risk risk() {
            String l = riskLevel == null ? "" : riskLevel.toUpperCase(Locale.ROOT);
            if (l.contains("HIGH")) return Risk.HIGH;
            return Risk.LOW;
        }

        public String label() {
            return risk() == Risk.HIGH ? "High Risk" : "Low Risk";
        }

        public int color() {
            return risk() == Risk.HIGH ? Color.parseColor("#D32F2F") : Color.parseColor("#2E9E44");
        }

        public String typeLabel() {
            return type.equals("URL") ? "URL Scan" : "Text Scan";
        }

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
            this.id = id;
            this.postingId = postingId;
            this.message = message;
            this.time = time;
            this.read = read;
        }
    }

    public interface ScanCallback
    {
        void onSuccess(Result r);
        void onError(String message);
    }

    public interface Done {
        void finished(String error);
    }

    private static final ScanScore INSTANCE = new ScanScore();
    public static ScanScore get() { return INSTANCE; }

    public final List<Result> history = new ArrayList<>();
    public final List<Note> notes = new ArrayList<>();
    public Result current;
    public boolean pendingAlert;

    private final Map<Integer, Result> session = new HashMap<>();
    private final Map<Integer, String> reviews = new HashMap<>();
    private final Set<Integer> readIds = new HashSet<>();
    private final Handler main = new Handler(Looper.getMainLooper());

    private static Context appCtx;

    public static void init(Context c) {
        appCtx = c.getApplicationContext();
    }

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
    public void scan(final String input, final String type, final ScanCallback cb) {
        ApiService api = ApiClient.getService();

        ScanRequest request = new ScanRequest(
                userId(),
                type.toLowerCase(Locale.ROOT),
                input
        );

        api.submitScan(request).enqueue(new Callback<ScanResponse>() {
            @Override
            public void onResponse(Call<ScanResponse> call, Response<ScanResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ScanResponse data = response.body();
                    List<Hit> hits = new ArrayList<>();

                    if (data.getFlaggedIndicators() != null) {
                        for (ScanResponse.Indicator indicator : data.getFlaggedIndicators()) {
                            hits.add(new Hit(
                                    indicator.getIndicatorType(),
                                    indicator.getDescription(),
                                    indicator.getPoints()
                            ));
                        }
                    }

                    Result result = new Result(
                            data.getPostingId(),
                            input,
                            type,
                            data.getRiskScore(),
                            data.getRiskLevel(),
                            hits,
                            System.currentTimeMillis(),
                            false
                    );

                    addResult(result);
                    cb.onSuccess(result);
                } else {
                    String error = "Server error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        // Keep default error
                    }
                    cb.onError(error);
                }
            }

            @Override
            public void onFailure(Call<ScanResponse> call, Throwable t) {
                cb.onError("Network error: " + t.getMessage());
            }
        });
    }

    private void addResult(Result r) {
        history.add(0, r);
        session.put(r.postingId, r);
        current = r;
        pendingAlert = r.score >= 50;
    }

    /* ---------- HISTORY: GET /api/history/{userId} ---------- */
    public void loadHistory(final Done d) {
        ApiService api = ApiClient.getService();

        api.getHistory(userId()).enqueue(new Callback<List<HistoryResponse>>() {
            @Override
            public void onResponse(Call<List<HistoryResponse>> call, Response<List<HistoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Result> list = new ArrayList<>();

                    for (HistoryResponse item : response.body()) {
                        Result local = session.get(item.getPostingId());
                        if (local != null) {
                            list.add(local);
                            continue;
                        }

                        String type = "url".equalsIgnoreCase(item.getScanType()) ? "URL" : "Text";
                        list.add(new Result(
                                item.getPostingId(),
                                item.getContentPreview(),
                                type,
                                item.getRiskScore(),
                                item.getRiskLevel(),
                                new ArrayList<Hit>(),
                                parseTime(item.getSubmissionDate()),
                                true
                        ));
                    }

                    Collections.sort(list, (a, b) -> Long.compare(b.time, a.time));
                    history.clear();
                    history.addAll(list);
                    d.finished(null);
                } else {
                    d.finished("Failed to load history");
                }
            }

            @Override
            public void onFailure(Call<List<HistoryResponse>> call, Throwable t) {
                d.finished("Network error: " + t.getMessage());
            }
        });
    }

    /* ---------- NOTIFICATIONS: GET /api/notifications/{userId} ---------- */
    public void loadNotifications(final Done d) {
        ApiService api = ApiClient.getService();

        api.getNotifications(userId()).enqueue(new Callback<List<NotificationResponse>>() {
            @Override
            public void onResponse(Call<List<NotificationResponse>> call, Response<List<NotificationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Note> list = new ArrayList<>();
                    reviews.clear();

                    for (NotificationResponse item : response.body()) {
                        boolean read = item.isRead() || readIds.contains(item.getNotificationId());
                        list.add(new Note(
                                item.getNotificationId(),
                                item.getPostingId(),
                                item.getMessage(),
                                parseTime(item.getCreatedAt()),
                                read
                        ));
                        reviews.put(item.getPostingId(), item.getMessage());
                    }

                    Collections.sort(list, (a, b) -> Long.compare(b.time, a.time));
                    notes.clear();
                    notes.addAll(list);
                    d.finished(null);
                } else {
                    d.finished("Failed to load notifications");
                }
            }

            @Override
            public void onFailure(Call<List<NotificationResponse>> call, Throwable t) {
                d.finished("Network error: " + t.getMessage());
            }
        });
    }

    public void markRead(Note n) {
        n.read = true;
        readIds.add(n.id);
    }

    public Result findByPosting(int postingId) {
        for (Result r : history) if (r.postingId == postingId) return r;
        return null;
    }

    public String titleFor(int postingId) {
        Result r = findByPosting(postingId);
        return r != null ? r.title() : "Posting #" + postingId;
    }

    public String reviewFor(int postingId) {
        return reviews.get(postingId);
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

    public void requestReview()
    {
        if (current == null) return;
        current.reviewRequested = true;

        // Call POST /api/scan/{postingId}/request-review
        ApiService api = ApiClient.getService();

        ReviewRequest request = new ReviewRequest(current.postingId, userId());

        api.requestReview(request).enqueue(new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                if (response.isSuccessful())
                {
                    // Success - admin will see this in their flagged list
                    current.reviewRequested = true;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t)
            {
                // Failed to send request
                current.reviewRequested = true;
            }
        });
    }

    public int unread()
    {
        int n = 0;
        for (Note x : notes) if (!x.read) n++;
        return n;
    }
}