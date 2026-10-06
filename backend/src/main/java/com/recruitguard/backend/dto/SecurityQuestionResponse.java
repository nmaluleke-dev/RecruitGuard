package com.recruitguard.backend.dto;

public class SecurityQuestionResponse {
    private String securityQuestion;

    public SecurityQuestionResponse(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityQuestion() { return securityQuestion; }
}