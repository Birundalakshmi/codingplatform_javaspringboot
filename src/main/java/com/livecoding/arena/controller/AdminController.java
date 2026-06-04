package com.livecoding.arena.controller;

import com.livecoding.arena.entity.Problem;
import com.livecoding.arena.entity.User;
import com.livecoding.arena.entity.Submission;
import com.livecoding.arena.service.ProblemService;
import com.livecoding.arena.service.UserService;
import com.livecoding.arena.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private ProblemService problemService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SubmissionService submissionService;
    
    @PostMapping("/problems")
    public ResponseEntity<?> addProblem(@RequestBody ProblemRequest request) {
        try {
            Problem problem = problemService.createProblem(
                request.getTitle(),
                request.getDescription(),
                Problem.Difficulty.valueOf(request.getDifficulty()),
                request.getCategory(),
                request.getTestCases(),
                request.getExpectedOutput()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Problem added successfully");
            response.put("problemId", problem.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/problems/{id}")
    public ResponseEntity<?> updateProblem(@PathVariable Long id, @RequestBody ProblemRequest request) {
        try {
            System.out.println("Updating problem with ID: " + id);
            System.out.println("Request data: " + request.getTitle());
            
            Problem problem = problemService.updateProblem(id, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Problem updated successfully");
            response.put("problemId", problem.getId());
            
            System.out.println("Problem updated successfully: " + problem.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error updating problem: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/problems/{id}")
    public ResponseEntity<?> deleteProblem(@PathVariable Long id) {
        try {
            problemService.deleteProblem(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Problem deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/submissions")
    public ResponseEntity<List<Submission>> getAllSubmissions() {
        return ResponseEntity.ok(submissionService.getAllSubmissions());
    }
    
    @GetMapping("/users/{userId}/submissions")
    public ResponseEntity<List<Submission>> getUserSubmissions(@PathVariable Long userId) {
        return ResponseEntity.ok(submissionService.getSubmissionsByUserId(userId));
    }
    
    public static class ProblemRequest {
        private String title;
        private String description;
        private String difficulty;
        private String category;
        private String testCases;
        private String expectedOutput;
        
        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getTestCases() { return testCases; }
        public void setTestCases(String testCases) { this.testCases = testCases; }
        
        public String getExpectedOutput() { return expectedOutput; }
        public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
    }
}