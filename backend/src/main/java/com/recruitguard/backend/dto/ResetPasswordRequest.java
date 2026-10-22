package com.recruitguard.backend.dto;

public class ResetPasswordRequest {
    private String email;
    private Integer securityQuestion;
    private String securityAnswer;
    private String newPassword;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getSecurityQuestion() { return securityQuestion; }
    public void setSecurityQuestion(Integer securityQuestion) { this.securityQuestion = securityQuestion; }

    public String getSecurityAnswer() { return securityAnswer; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}