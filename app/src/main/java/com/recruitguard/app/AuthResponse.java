package com.recruitguard.app;

public class AuthResponse
{
    private String token;
    private String role;
    private String name;
    private int userId;

    // Getters
    public String getToken()
    {
        return token;
    }

    public String getRole()
    {
        return role;
    }

    public String getName()
    {
        return name;
    }

    public int getUserId()
    {
        return userId;
    }
}