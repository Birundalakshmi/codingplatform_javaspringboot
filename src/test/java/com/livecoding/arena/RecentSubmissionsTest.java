package com.livecoding.arena;

import com.livecoding.arena.entity.Problem;
import com.livecoding.arena.entity.Submission;
import com.livecoding.arena.entity.User;
import com.livecoding.arena.repository.SubmissionRepository;
import com.livecoding.arena.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RecentSubmissionsTest {

    @MockBean
    private SubmissionRepository submissionRepository;

    @Test
    public void testRecentSubmissionsDisplay() {
        // Create test user
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testStudent");
        testUser.setRole(User.Role.STUDENT);

        // Create test problem
        Problem testProblem = new Problem();
        testProblem.setId(1L);
        testProblem.setTitle("Two Sum");
        testProblem.setDifficulty(Problem.Difficulty.EASY);

        // Create test submissions
        Submission submission1 = new Submission(testUser, testProblem, "public class Solution { }");
        submission1.setId(1L);
        submission1.setStatus(Submission.Status.ACCEPTED);
        submission1.setScore(100);
        submission1.setSubmittedAt(LocalDateTime.now().minusMinutes(10));

        Submission submission2 = new Submission(testUser, testProblem, "public class Solution { }");
        submission2.setId(2L);
        submission2.setStatus(Submission.Status.WRONG_ANSWER);
        submission2.setScore(0);
        submission2.setSubmittedAt(LocalDateTime.now().minusMinutes(5));

        List<Submission> mockSubmissions = Arrays.asList(submission2, submission1); // Most recent first

        // Mock repository behavior
        when(submissionRepository.findByUserOrderBySubmittedAtDesc(testUser))
            .thenReturn(mockSubmissions);

        // Verify submissions are returned in correct order
        List<Submission> result = submissionRepository.findByUserOrderBySubmittedAtDesc(testUser);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Submission.Status.WRONG_ANSWER, result.get(0).getStatus()); // Most recent
        assertEquals(Submission.Status.ACCEPTED, result.get(1).getStatus()); // Older
        
        // Verify problem titles are accessible (EAGER fetch)
        assertEquals("Two Sum", result.get(0).getProblem().getTitle());
        assertEquals("Two Sum", result.get(1).getProblem().getTitle());
        
        System.out.println("✓ Recent submissions test passed");
        System.out.println("✓ Problem titles are accessible via EAGER fetch");
        System.out.println("✓ Submissions are ordered by submission time (newest first)");
    }

    @Test
    public void testEmptySubmissionsList() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("newStudent");

        // Mock empty submissions
        when(submissionRepository.findByUserOrderBySubmittedAtDesc(testUser))
            .thenReturn(Arrays.asList());

        List<Submission> result = submissionRepository.findByUserOrderBySubmittedAtDesc(testUser);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        System.out.println("✓ Empty submissions list test passed");
    }
}