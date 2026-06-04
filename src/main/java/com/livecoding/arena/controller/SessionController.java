package com.livecoding.arena.controller;

import com.livecoding.arena.entity.CodeSession;
import com.livecoding.arena.service.CodeSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @Autowired
    private CodeSessionService codeSessionService;

    @PostMapping("/create")
    public ResponseEntity<?> createSession(@RequestParam Long problemId, @RequestParam Long userId) {
        try {
            CodeSession session = codeSessionService.createSession(problemId, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sessionId", session.getSessionId());
            response.put("message", "Session created successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinSession(@RequestParam String sessionId, @RequestParam Long userId) {
        try {
            CodeSession session = codeSessionService.joinSession(sessionId, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("session", session);
            response.put("message", "Joined session successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<?> getSession(@PathVariable String sessionId) {
        try {
            CodeSession session = codeSessionService.getSession(sessionId);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}