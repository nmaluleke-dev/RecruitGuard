package com.recruitguard.app.jobseeker;


import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("role")
    private String role;

    @SerializedName("name")
    private String name;

    @SerializedName("userId")
    private int userId;

    public String getToken() { return token; }
    public String getRole() { return role; }
    public String getName() { return name; }
    public int getUserId() { return userId; }
}