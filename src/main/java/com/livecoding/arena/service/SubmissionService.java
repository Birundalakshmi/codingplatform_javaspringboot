package com.livecoding.arena.service;

import com.livecoding.arena.entity.Submission;
import com.livecoding.arena.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    public List<Submission> findByUserId(Long userId) {
        return submissionRepository.findByUserIdOrderBySubmittedAtDesc(userId);
    }

    public List<Submission> findByProblemId(Long problemId) {
        return submissionRepository.findByProblemIdOrderBySubmittedAtDesc(problemId);
    }

    public Submission save(Submission submission) {
        return submissionRepository.save(submission);
    }
    
    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }
    
    public List<Submission> getSubmissionsByUserId(Long userId) {
        return submissionRepository.findByUserIdOrderBySubmittedAtDesc(userId);
    }
}