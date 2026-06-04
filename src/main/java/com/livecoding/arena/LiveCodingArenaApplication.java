package com.livecoding.arena;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.livecoding.arena.service.ProblemService;

@SpringBootApplication
public class LiveCodingArenaApplication implements CommandLineRunner {
    
    @Autowired
    private ProblemService problemService;
    
    public static void main(String[] args) {
        SpringApplication.run(LiveCodingArenaApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize sample problems on startup
        System.out.println("Initializing problems...");
        problemService.initializeSampleProblems();
        
        // Check if problems were created
        long count = problemService.getAllProblems().size();
        System.out.println("Total problems in database: " + count);
        
        System.out.println("Live Coding Arena started successfully!");
        System.out.println("Access the application at: http://localhost:8080");
    }
}