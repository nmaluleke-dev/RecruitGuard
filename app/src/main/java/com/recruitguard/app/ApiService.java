package com.recruitguard.app;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // Existing Auth Endpoints
    @POST("api/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("api/auth/verify-email")
    Call<Void> verifyEmail(@Body VerifyEmailRequest request);

    @POST("api/auth/security-question")
    Call<SecurityQuestionResponse> getSecurityQuestion(@Body VerifyEmailRequest request);

    @POST("api/auth/reset-password")
    Call<Void> resetPassword(@Body ResetPasswordRequest request);

    // Admin Endpoints
    @GET("api/admin/flagged")
    Call<List<FlaggedJobResponse>> getFlaggedJobs();

    @POST("api/admin/review")
    Call<Void> submitReviewDecision(@Body ReviewDecisionRequest request);

    @GET("api/admin/audit")
    Call<List<AuditLogResponse>> getAuditTrail();

    // Job Seeker Endpoints
    @POST("api/scan/submit")
    Call<com.recruitguard.app.ScanResponse> submitScan(@Body com.recruitguard.app.ScanRequest request);

    @GET("api/history/{userId}")
    Call<List<com.recruitguard.app.HistoryResponse>> getHistory(@Path("userId") int userId);

    @GET("api/notifications/{userId}")
    Call<List<com.recruitguard.app.NotificationResponse>> getNotifications(@Path("userId") int userId);

    @POST("api/scan/request-review")
    Call<Void> requestReview(@Body com.recruitguard.app.ReviewRequest request);

    @POST("api/scan/{postingId}/request-review")
    Call<Void> markReviewRequested(@Path("postingId") int postingId);
}