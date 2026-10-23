package com.recruitguard.app;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService
{

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
}