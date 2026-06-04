package com.livecoding.arena.repository;

import com.livecoding.arena.entity.Submission;
import com.livecoding.arena.entity.User;
import com.livecoding.arena.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByUser(User user);
    List<Submission> findByProblem(Problem problem);
    List<Submission> findByUserAndProblem(User user, Problem problem);
    List<Submission> findByUserOrderBySubmittedAtDesc(User user);
    List<Submission> findByUserIdOrderBySubmittedAtDesc(Long userId);
    List<Submission> findByProblemIdOrderBySubmittedAtDesc(Long problemId);
    boolean existsByUserAndProblemAndStatus(User user, Problem problem, Submission.Status status);
}