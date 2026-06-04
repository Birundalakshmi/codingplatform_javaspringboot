package com.livecoding.arena.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "problems")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
    
    private String category;
    
    @Column(columnDefinition = "TEXT")
    private String testCases;
    
    @Column(columnDefinition = "TEXT")
    private String testInputs;  // JSON array of inputs, one per test case

    @Column(columnDefinition = "TEXT")
    private String expectedOutput;  // JSON array of expected outputs, one per test case
    
    private int maxScore = 100;
    private int points = 100;
    private int timeLimit = 60;
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Submission> submissions;
    
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
    
    // Constructors
    public Problem() {}
    
    public Problem(String title, String description, Difficulty difficulty, String category) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getTestCases() { return testCases; }
    public void setTestCases(String testCases) { this.testCases = testCases; }

    public String getTestInputs() { return testInputs; }
    public void setTestInputs(String testInputs) { this.testInputs = testInputs; }
    
    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
    
    public int getMaxScore() { return maxScore; }
    public void setMaxScore(int maxScore) { this.maxScore = maxScore; }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    
    public int getTimeLimit() { return timeLimit; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public List<Submission> getSubmissions() { return submissions; }
    public void setSubmissions(List<Submission> submissions) { this.submissions = submissions; }
}