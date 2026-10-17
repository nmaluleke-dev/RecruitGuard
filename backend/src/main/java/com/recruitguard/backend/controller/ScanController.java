package com.recruitguard.backend.controller;

import com.recruitguard.backend.dto.ScanRequest;
import com.recruitguard.backend.dto.ScanResponse;
import com.recruitguard.backend.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scan")
@CrossOrigin(origins = "*")
public class ScanController {

    @Autowired
    private ScanService scanService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitScan(@RequestBody ScanRequest request) {
        try {
            ScanResponse response = scanService.scanJobPosting(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}