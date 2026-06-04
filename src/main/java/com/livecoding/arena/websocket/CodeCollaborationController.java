package com.livecoding.arena.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class CodeCollaborationController {
    
    @MessageMapping("/code.update")
    @SendTo("/topic/code")
    public CodeUpdateMessage updateCode(CodeUpdateMessage message) {
        return message;
    }
    
    @MessageMapping("/cursor.update")
    @SendTo("/topic/cursor")
    public CursorUpdateMessage updateCursor(CursorUpdateMessage message) {
        return message;
    }
    
    public static class CodeUpdateMessage {
        private String sessionId;
        private String code;
        private String username;
        
        public CodeUpdateMessage() {}
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }
    
    public static class CursorUpdateMessage {
        private String sessionId;
        private int line;
        private int column;
        private String username;
        
        public CursorUpdateMessage() {}
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public int getLine() { return line; }
        public void setLine(int line) { this.line = line; }
        
        public int getColumn() { return column; }
        public void setColumn(int column) { this.column = column; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }
}