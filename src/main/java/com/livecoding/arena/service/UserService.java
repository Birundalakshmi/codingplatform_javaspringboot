package com.livecoding.arena.service;

import com.livecoding.arena.entity.User;
import com.livecoding.arena.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private com.livecoding.arena.repository.SubmissionRepository submissionRepository;
    
    public User registerUser(String username, String email, String password, User.Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setScore(0);
        user.setTotalScore(0);
        user.setProblemsSolved(0);
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public void updateUserScore(User user, int points) {
        user.setScore(user.getScore() + points);
        user.setTotalScore(user.getTotalScore() + points);
        
        // Count actual problems solved from accepted submissions
        long actualProblemsSolved = submissionRepository.findByUser(user).stream()
            .filter(submission -> submission.getStatus() == com.livecoding.arena.entity.Submission.Status.ACCEPTED)
            .map(submission -> submission.getProblem().getId())
            .distinct()
            .count();
        
        user.setProblemsSolved((int) actualProblemsSolved);
        
        userRepository.save(user);
        System.out.println("Updated user: " + user.getUsername() + " Score: " + user.getTotalScore() + " Problems: " + user.getProblemsSolved());
    }
    
    public java.util.List<User> getLeaderboard() {
        return userRepository.findAll();
    }
    
    public long getStudentCount() {
        return userRepository.count();
    }
    
    public java.util.List<User> getTopStudents(int limit) {
        return userRepository.findAll().stream()
            .filter(user -> user.getRole() == User.Role.STUDENT)
            .sorted((a, b) -> Integer.compare(b.getTotalScore(), a.getTotalScore()))
            .limit(limit)
            .collect(java.util.stream.Collectors.toList());
    }
    
    public java.util.List<User> getStrugglingStudents(int limit) {
        return userRepository.findAll().stream()
            .filter(user -> user.getRole() == User.Role.STUDENT)
            .sorted((a, b) -> Integer.compare(a.getTotalScore(), b.getTotalScore()))
            .limit(limit)
            .collect(java.util.stream.Collectors.toList());
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) {
        User user = findByUsername(username).orElse(null);
        if (user == null) return null;
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
    
    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> user = findByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }
    
    public void recalculateUserStats(User user) {
        // Recalculate stats if needed
    }
    
    public int getUserRank(User user) {
        return 1;
    }
    
    public java.util.List<Object> getRecentSubmissions(User user, int limit) {
        return java.util.Collections.emptyList();
    }
    
    public java.util.List<String> getSolvedProblems(String username) {
        User user = findByUsername(username).orElse(null);
        if (user == null) {
            System.out.println("DEBUG: User not found: " + username);
            return java.util.Collections.emptyList();
        }
        
        // Get actual solved problems from submissions repository
        java.util.List<com.livecoding.arena.entity.Submission> allSubmissions = submissionRepository.findByUser(user);
        System.out.println("DEBUG: Total submissions for " + username + ": " + allSubmissions.size());
        
        java.util.List<com.livecoding.arena.entity.Submission> acceptedSubmissions = 
            allSubmissions.stream()
                .filter(submission -> submission.getStatus() == com.livecoding.arena.entity.Submission.Status.ACCEPTED)
                .collect(java.util.stream.Collectors.toList());
        
        System.out.println("DEBUG: Accepted submissions for " + username + ": " + acceptedSubmissions.size());
        
        java.util.List<String> solvedProblems = acceptedSubmissions.stream()
            .map(submission -> submission.getProblem().getTitle())
            .distinct()
            .collect(java.util.stream.Collectors.toList());
        
        System.out.println("DEBUG: Solved problems for " + username + ": " + solvedProblems);
        return solvedProblems;
    }
}