package com.livecoding.arena.service;

import com.livecoding.arena.entity.Problem;
import com.livecoding.arena.entity.Submission;
import com.livecoding.arena.entity.User;
import com.livecoding.arena.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DataInitializationService implements ApplicationRunner {
    
    @Autowired
    private ProblemService problemService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SubmissionRepository submissionRepository;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        problemService.initializeSampleProblems();
        createSampleSubmissions();
    }
    
    private void createSampleSubmissions() {
        try {
            // Create a test student if doesn't exist
            User testUser;
            try {
                testUser = userService.registerUser("student1", "student1@test.com", "password", User.Role.STUDENT);
            } catch (Exception e) {
                testUser = userService.findByUsername("student1").orElse(null);
            }
            
            if (testUser != null && problemService.getAllProblems().size() > 0) {
                Problem problem = problemService.getAllProblems().get(0);
                
                Submission submission = new Submission(testUser, problem, "public class Solution { public static void main(String[] args) { System.out.println(\"Hello World\"); } }");
                submission.setStatus(Submission.Status.ACCEPTED);
                submission.setScore(100);
                submission.setSubmittedAt(LocalDateTime.now().minusHours(1));
                submissionRepository.save(submission);
                
                System.out.println("Sample submission created for testing");
            }
        } catch (Exception e) {
            System.out.println("Could not create sample submissions: " + e.getMessage());
        }
    }
}