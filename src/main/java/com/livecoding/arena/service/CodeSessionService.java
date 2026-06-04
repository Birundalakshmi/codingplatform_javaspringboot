package com.livecoding.arena.service;

import com.livecoding.arena.entity.CodeSession;
import com.livecoding.arena.entity.Problem;
import com.livecoding.arena.entity.User;
import com.livecoding.arena.repository.CodeSessionRepository;
import com.livecoding.arena.repository.ProblemRepository;
import com.livecoding.arena.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CodeSessionService {

    @Autowired
    private CodeSessionRepository codeSessionRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private UserRepository userRepository;

    public CodeSession createSession(Long problemId, Long userId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CodeSession session = new CodeSession();
        session.setSessionId(generateSessionId());
        session.setProblem(problem);
        session.setCreatedBy(user);
        session.setCode("// Start coding here...");
        session.setLanguage("java");
        session.setActive(true);

        return codeSessionRepository.save(session);
    }

    public CodeSession joinSession(String sessionId, Long userId) {
        CodeSession session = codeSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.isActive()) {
            throw new RuntimeException("Session is not active");
        }

        return session;
    }

    public CodeSession getSession(String sessionId) {
        return codeSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    public CodeSession updateSessionCode(String sessionId, String code) {
        CodeSession session = getSession(sessionId);
        session.setCode(code);
        return codeSessionRepository.save(session);
    }

    private String generateSessionId() {
        return "session_" + UUID.randomUUID().toString().substring(0, 8);
    }
}