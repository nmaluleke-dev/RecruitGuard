package com.recruitguard.app.jobseeker;

import android.graphics.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScanScore {
    public enum Risk { LOW, MEDIUM, HIGH }

    public static class Hit {
        public final String name, description;
        public final int points;
        Hit(String name, String description, int points) {
            this.name = name; this.description = description; this.points = points;
        }
    }

    public static class Result {
        public final String input, type;
        public final int score;
        public final List<Hit> hits;
        public final long time;
        public boolean reviewRequested;

        Result(String input, String type, int score, List<Hit> hits) {
            this.input = input; this.type = type; this.score = score;
            this.hits = hits; this.time = System.currentTimeMillis();
        }
        public Risk risk() { return score >= 70 ? Risk.HIGH : score >= 40 ? Risk.MEDIUM : Risk.LOW; }
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

    private static final ScanScore INSTANCE = new ScanScore();
    public static ScanScore get() { return INSTANCE; }

    public final List<Result> history = new ArrayList<>();
    public final List<Note> notes = new ArrayList<>();
    public Result current;
    public boolean pendingAlert;

    // Placeholder rules - swap for the team's real detection API.
    public Result scan(String input, String type) {
        String t = input.toLowerCase(Locale.ROOT);
        List<Hit> hits = new ArrayList<>();

        if (hasAny(t, "registration fee", "pay a fee", "deposit", "training fee", "western union", "send money"))
            hits.add(new Hit("Upfront payment", "Asks for a fee, deposit or registration payment.", 35));
        if (hasAny(t, "guaranteed income", "earn $", "earn r", "no experience needed", "easy money"))
            hits.add(new Hit("Unrealistic pay", "Promises high earnings with little effort.", 20));
        if (hasAny(t, "urgent", "apply now", "limited slots", "immediately"))
            hits.add(new Hit("Urgency pressure", "Pushes you to act immediately.", 15));
        if (hasAny(t, "whatsapp", "telegram", "@gmail.com", "@yahoo.com"))
            hits.add(new Hit("Informal contact channel", "Uses WhatsApp/Telegram or a free email for hiring.", 20));
        if (hasAny(t, "bit.ly", "tinyurl", "http://"))
            hits.add(new Hit("Suspicious link", "Shortened or non-secure link.", 15));
        if (type.equals("Text") && t.length() < 80)
            hits.add(new Hit("Too little detail", "Posting is very short and lacks company info.", 10));

        int score = 0;
        for (Hit h : hits) score += h.points;
        score = Math.min(score, 100);

        Result r = new Result(input, type, score, hits);
        history.add(0, r);
        current = r;
        if (r.risk() == Risk.HIGH) {
            pendingAlert = true;
            notes.add(0, new Note("High-risk posting detected", "Scan scored " + score + "/100."));
        }
        return r;
    }

    public void requestReview() {
        if (current == null) return;
        current.reviewRequested = true; // TODO: POST to backend
        notes.add(0, new Note("Review requested", "A human reviewer will check this posting."));
    }

    public int unread() {
        int n = 0;
        for (Note x : notes) if (!x.read) n++;
        return n;
    }

    private boolean hasAny(String t, String... keys) {
        for (String k : keys) if (t.contains(k)) return true;
        return false;
    }
}
