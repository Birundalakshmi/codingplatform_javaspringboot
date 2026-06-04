package com.livecoding.arena.dto;

import com.livecoding.arena.entity.User;

public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private User.Role role = User.Role.STUDENT;
    
    public RegisterRequest() {}
    
    public RegisterRequest(String username, String email, String password, User.Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }
}