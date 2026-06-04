package com.livecoding.arena.controller;

import com.livecoding.arena.entity.User;
import com.livecoding.arena.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserStatsController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats(@RequestParam String username) {
        try {
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            userService.recalculateUserStats(user);
            User freshUser = userService.findById(user.getId()).orElse(user);
            System.out.println("API: Stats for " + username + " - Score: " + freshUser.getTotalScore() + ", Problems: " + freshUser.getProblemsSolved());
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalScore", freshUser.getTotalScore());
            stats.put("problemsSolved", freshUser.getProblemsSolved());
            stats.put("rank", userService.getUserRank(freshUser));
            
            var submissions = userService.getRecentSubmissions(freshUser, 5);
            stats.put("recentSubmissions", submissions);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.out.println("API Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}