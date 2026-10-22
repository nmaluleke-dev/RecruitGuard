package com.recruitguard.backend.service;

import com.recruitguard.backend.dto.AuthResponse;
import com.recruitguard.backend.dto.LoginRequest;
import com.recruitguard.backend.dto.RegisterRequest;
import com.recruitguard.backend.dto.ResetPasswordRequest;
import com.recruitguard.backend.dto.SecurityQuestionResponse;
import com.recruitguard.backend.model.User;
import com.recruitguard.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setSecurityQuestion(request.getSecurityQuestion());
        user.setSecurityAnswer(request.getSecurityAnswer());

        if (request.getRole() != null && request.getRole().equals("admin")) {
            user.setRole(User.Role.admin);
        } else {
            user.setRole(User.Role.job_seeker);
        }

        userRepository.save(user);

        String token = "RG-" + user.getEmail() + "-" + System.currentTimeMillis();
        return new AuthResponse(token, user.getRole().name(), user.getName(), user.getUserId());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        String token = "RG-" + user.getEmail() + "-" + System.currentTimeMillis();
        return new AuthResponse(token, user.getRole().name(), user.getName(), user.getUserId());
    }

    public SecurityQuestionResponse getSecurityQuestion(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        return new SecurityQuestionResponse(user.getSecurityQuestion());
    }
    public String resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (!user.getSecurityQuestion().equals(request.getSecurityQuestion())) {
            throw new RuntimeException("Incorrect security question selected");
        }

        if (!user.getSecurityAnswer().equalsIgnoreCase(request.getSecurityAnswer())) {
            throw new RuntimeException("Incorrect security answer");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Password reset successfully";
    }

}