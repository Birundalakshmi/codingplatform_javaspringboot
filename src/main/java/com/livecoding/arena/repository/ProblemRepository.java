package com.livecoding.arena.repository;

import com.livecoding.arena.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findByCategory(String category);
    List<Problem> findByDifficulty(Problem.Difficulty difficulty);
    List<Problem> findByCategoryAndDifficulty(String category, Problem.Difficulty difficulty);
}