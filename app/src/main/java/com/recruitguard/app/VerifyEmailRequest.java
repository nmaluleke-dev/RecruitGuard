package com.recruitguard.app;

import com.google.gson.annotations.SerializedName;

public class VerifyEmailRequest {
    @SerializedName("email")
    private String email;

    public VerifyEmailRequest(String email) {
        this.email = email;
    }
}
