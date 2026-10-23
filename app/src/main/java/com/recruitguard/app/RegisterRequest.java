package com.recruitguard.app;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("role")
    private String role;

    @SerializedName("securityQuestion")
    private Integer securityQuestion;

    @SerializedName("securityAnswer")
    private String securityAnswer;

    public RegisterRequest(String name, String email, String password, String role,
            Integer securityQuestion, String securityAnswer) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
    }
}
