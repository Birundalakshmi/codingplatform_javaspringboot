package com.livecoding.arena.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "code_sessions")
public class CodeSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String sessionId;
    
    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;
    
    @Column(columnDefinition = "TEXT")
    private String currentCode = "";
    
    @ElementCollection
    @CollectionTable(name = "session_participants")
    private Set<String> participants;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    private String language = "java";
    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastActivity = LocalDateTime.now();
    
    // Constructors
    public CodeSession() {}
    
    public CodeSession(String sessionId, Problem problem) {
        this.sessionId = sessionId;
        this.problem = problem;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public Problem getProblem() { return problem; }
    public void setProblem(Problem problem) { this.problem = problem; }
    
    public String getCurrentCode() { return currentCode; }
    public void setCurrentCode(String currentCode) { this.currentCode = currentCode; }
    
    public Set<String> getParticipants() { return participants; }
    public void setParticipants(Set<String> participants) { this.participants = participants; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }
    
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    
    public String getCode() { return currentCode; }
    public void setCode(String code) { this.currentCode = code; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}