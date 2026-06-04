package com.livecoding.arena.controller;

import com.livecoding.arena.dto.CodeSubmissionRequest;
import com.livecoding.arena.entity.Problem;
import com.livecoding.arena.entity.Submission;
import com.livecoding.arena.entity.User;
import com.livecoding.arena.repository.UserRepository;
import com.livecoding.arena.service.CodeEvaluationService;
import com.livecoding.arena.service.ProblemService;
import com.livecoding.arena.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.livecoding.arena.service.SubmissionService;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {
    
    @Autowired
    private CodeEvaluationService evaluationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProblemService problemService;

    @Autowired
    private SubmissionService submissionService;

    @GetMapping("/user")
    public ResponseEntity<?> getUserSubmissions(@RequestParam String username) {
        User user = userService.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(submissionService.getSubmissionsByUserId(user.getId()));
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitCode(@RequestBody CodeSubmissionRequest request) {
        try {
            System.out.println("SUBMISSION DEBUG: Received request for username: " + request.getUsername());
            String username = request.getUsername() != null ? request.getUsername() : "student";
            
            // Find or create user (same logic as WebController)
            User user = userService.findByUsername(username).orElse(null);
            if (user == null) {
                System.out.println("SUBMISSION DEBUG: Creating new user: " + username);
                try {
                    user = userService.registerUser(username, username + "@test.com", "password", User.Role.STUDENT);
                    System.out.println("SUBMISSION DEBUG: User created successfully: " + user.getId());
                } catch (Exception e) {
                    System.out.println("SUBMISSION DEBUG: Error creating user: " + e.getMessage());
                    throw e;
                }
            } else {
                System.out.println("SUBMISSION DEBUG: Found existing user: " + username + " with ID: " + user.getId());
            }
            
            // Get the problem
            Problem problem = problemService.getProblemById(request.getProblemId()).orElse(null);
            if (problem == null) {
                System.out.println("SUBMISSION DEBUG: Problem not found, using first problem");
                problem = problemService.getAllProblems().get(0);
            }
            System.out.println("SUBMISSION DEBUG: Using problem: " + problem.getTitle());
            
            // Save submission using evaluation service
            Submission submission = evaluationService.evaluateCode(user, problem, request.getCode(), request.getLanguage());
            System.out.println("SUBMISSION DEBUG: Submission saved with ID: " + submission.getId());
            
            Map<String, Object> response = new HashMap<>();
            boolean accepted = submission.getStatus() == com.livecoding.arena.entity.Submission.Status.ACCEPTED;
            response.put("success", accepted);
            response.put("score", submission.getScore());
            response.put("status", submission.getStatus().name());
            response.put("problemTitle", problem.getTitle());
            response.put("testCasesPassed", submission.getTestCasesPassed());
            response.put("totalTestCases", submission.getTotalTestCases());
            response.put("executionTime", submission.getExecutionTime());
            response.put("output", submission.getOutput());
            // For compile errors, send the compiler error as message; for runtime, send first error; else test output
            String msg;
            if (accepted) {
                msg = "All test cases passed!";
            } else if (submission.getStatus() == com.livecoding.arena.entity.Submission.Status.COMPILATION_ERROR) {
                msg = submission.getErrorMessage() != null ? submission.getErrorMessage() : submission.getOutput();
            } else if (submission.getErrorMessage() != null) {
                msg = submission.getErrorMessage();
            } else {
                msg = null;
            }
            response.put("message", msg);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Submission failed: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}