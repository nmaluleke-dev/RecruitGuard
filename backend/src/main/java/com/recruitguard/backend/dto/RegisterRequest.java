package com.recruitguard.backend.dto;

public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String role;
    private Integer securityQuestion;
    private String securityAnswer;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getSecurityQuestion() { return securityQuestion; }
    public void setSecurityQuestion(Integer q) { this.securityQuestion = q; }

    public String getSecurityAnswer() { return securityAnswer; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }
}