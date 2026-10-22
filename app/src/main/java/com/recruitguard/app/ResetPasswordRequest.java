package com.recruitguard.app;

public class ResetPasswordRequest
{
    private String email;
    private Integer securityQuestion;
    private String securityAnswer;
    private String newPassword;

    public ResetPasswordRequest(String email, Integer securityQuestion, String securityAnswer, String newPassword)
    {
        this.email = email;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.newPassword = newPassword;
    }
}