package com.recruitguard.app;

public class RegisterRequest
{
    private String name;
    private String email;
    private String password;
    private String role;
    private String securityQuestion;
    private String securityAnswer;

    public RegisterRequest(String name, String email, String password, String role, String securityQuestion, String securityAnswer)
    {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
    }
}