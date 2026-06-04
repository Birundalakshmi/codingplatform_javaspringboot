package com.livecoding.arena.repository;

import com.livecoding.arena.entity.CodeSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CodeSessionRepository extends JpaRepository<CodeSession, Long> {
    Optional<CodeSession> findBySessionId(String sessionId);
}