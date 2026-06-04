package com.livecoding.arena.controller;

import com.livecoding.arena.dto.CodeExecutionRequest;
import com.livecoding.arena.service.CodeEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/code")
public class CodeController {

    @Autowired
    private CodeEvaluationService codeEvaluationService;

    @PostMapping("/run")
    public ResponseEntity<?> runCode(@RequestBody CodeExecutionRequest request) {
        try {
            Map<String, Object> result = codeEvaluationService.executeCode(
                request.getCode(),
                request.getLanguage()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/compile")
    public ResponseEntity<?> compileCode(@RequestBody CodeExecutionRequest request) {
        try {
            Map<String, Object> result = codeEvaluationService.compileCode(
                request.getCode(),
                request.getLanguage()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Compilation Error: " + e.getMessage());
            response.put("compilationError", true);
            return ResponseEntity.ok(response);
        }
    }
}