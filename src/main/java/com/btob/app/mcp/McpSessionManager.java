package com.btob.app.mcp;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class McpSessionManager {

    private final Map<String, McpSession> sessions = new ConcurrentHashMap<>();

    public McpSession createSession(McpSession session) {
        sessions.put(session.getSessionId(), session);
        return session;
    }

    public McpSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        McpSession session = sessions.remove(sessionId);
        if (session != null) {
            session.complete();
        }
    }
}
