package com.livecoding.arena.controller;

import com.livecoding.arena.entity.User;
import com.livecoding.arena.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/update-score")
    public Map<String, Object> updateScore() {
        User user = userService.findByUsername("student").orElse(null);
        if (user == null) {
            user = userService.registerUser("student", "student@test.com", "password", User.Role.STUDENT);
        }
        
        userService.updateUserScore(user, 100);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalScore", user.getTotalScore());
        response.put("problemsSolved", user.getProblemsSolved());
        return response;
    }
}