package com.livecoding.arena.controller;

import com.livecoding.arena.entity.User;
import com.livecoding.arena.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<?> getLeaderboard() {
        List<User> users = userService.getLeaderboard();
        
        List<Map<String, Object>> leaderboard = users.stream()
            .map(user -> {
                Map<String, Object> entry = new HashMap<>();
                entry.put("rank", users.indexOf(user) + 1);
                entry.put("username", user.getUsername());
                entry.put("totalScore", user.getTotalScore());
                entry.put("role", user.getRole());
                return entry;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(leaderboard);
    }
}