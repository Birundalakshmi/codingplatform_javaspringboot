package com.livecoding.arena.repository;

import com.livecoding.arena.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u ORDER BY u.totalScore DESC")
    List<User> findAllByOrderByTotalScoreDesc();
    
    List<User> findByRoleOrderByTotalScoreDesc(User.Role role);
    
    long countByRole(User.Role role);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.totalScore > 0 ORDER BY u.totalScore DESC LIMIT :limit")
    List<User> findTopStudents(User.Role role, int limit);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.totalScore < 50 ORDER BY u.totalScore ASC LIMIT :limit")
    List<User> findStrugglingStudents(User.Role role, int limit);
}