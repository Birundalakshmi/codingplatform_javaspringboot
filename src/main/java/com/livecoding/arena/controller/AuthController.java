package com.livecoding.arena.controller;

import com.livecoding.arena.dto.LoginRequest;
import com.livecoding.arena.dto.RegisterRequest;
import com.livecoding.arena.entity.User;
import com.livecoding.arena.service.JwtService;
import com.livecoding.arena.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtService jwtService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("userId", user.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> user = userService.authenticateUser(request.getUsername(), request.getPassword());
        
        if (user.isPresent()) {
            UserDetails userDetails = userService.loadUserByUsername(request.getUsername());
            String token = jwtService.generateToken(userDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("token", token);
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.get().getId());
            userInfo.put("username", user.get().getUsername());
            userInfo.put("email", user.get().getEmail());
            userInfo.put("role", user.get().getRole());
            userInfo.put("totalScore", user.get().getTotalScore());
            response.put("user", userInfo);
            
            // Set redirect URL based on role
            String redirectUrl;
            if (user.get().getRole() == User.Role.ADMIN) {
                redirectUrl = "/admin-dashboard";
            } else if (user.get().getRole() == User.Role.MENTOR) {
                redirectUrl = "/mentor?role=MENTOR&username=" + user.get().getUsername();
            } else {
                redirectUrl = "/dashboard?role=" + user.get().getRole() + "&username=" + user.get().getUsername();
            }
            response.put("redirectUrl", redirectUrl);
            
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Invalid credentials");
            return ResponseEntity.badRequest().body(response);
        }
    }
}