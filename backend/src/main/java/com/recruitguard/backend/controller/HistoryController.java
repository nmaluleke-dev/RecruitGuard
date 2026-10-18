package com.recruitguard.backend.controller;

import com.recruitguard.backend.dto.ScanHistoryResponse;
import com.recruitguard.backend.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "*")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserHistory(@PathVariable Integer userId) {
        try {
            List<ScanHistoryResponse> history = historyService.getUserHistory(userId);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}