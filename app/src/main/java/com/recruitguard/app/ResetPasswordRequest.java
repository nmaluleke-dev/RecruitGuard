package com.recruitguard.app;

public class ResetPasswordRequest
{
    private String email;
    private String securityAnswer;
    private String newPassword;

    public ResetPasswordRequest(String email, String securityAnswer, String newPassword)
    {
        this.email = email;
        this.securityAnswer = securityAnswer;
        this.newPassword = newPassword;
    }
}