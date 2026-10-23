package com.recruitguard.app.jobseeker;


import com.google.gson.annotations.SerializedName;

public class ResetPasswordRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("securityQuestion")
    private Integer securityQuestion;

    @SerializedName("securityAnswer")
    private String securityAnswer;

    @SerializedName("newPassword")
    private String newPassword;

    public ResetPasswordRequest(String email, Integer securityQuestion,
                                String securityAnswer, String newPassword) {
        this.email = email;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.newPassword = newPassword;
    }
}