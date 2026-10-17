package com.recruitguard.backend.controller;

import com.recruitguard.backend.dto.AuditTrailResponse;
import com.recruitguard.backend.dto.FlaggedPostingResponse;
import com.recruitguard.backend.dto.ReviewRequest;
import com.recruitguard.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/flagged")
    public ResponseEntity<?> getFlaggedPostings() {
        try {
            List<FlaggedPostingResponse> postings = adminService.getFlaggedPostings();
            return ResponseEntity.ok(postings);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/review")
    public ResponseEntity<?> submitReview(@RequestBody ReviewRequest request) {
        try {
            String result = adminService.submitReview(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/audit")
    public ResponseEntity<?> getAuditTrail() {
        try {
            List<AuditTrailResponse> audit = adminService.getAuditTrail();
            return ResponseEntity.ok(audit);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}