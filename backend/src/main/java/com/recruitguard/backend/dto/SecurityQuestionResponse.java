package com.recruitguard.backend.dto;

public class SecurityQuestionResponse {
    private Integer securityQuestion;

    public SecurityQuestionResponse(Integer securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public Integer getSecurityQuestion() { return securityQuestion; }
}