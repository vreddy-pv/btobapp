package com.btob.app.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class McpSession {

    private final String sessionId;
    private final SseEmitter emitter;
    private final ObjectMapper mapper;

    public McpSession(ObjectMapper mapper) {
        this.sessionId = UUID.randomUUID().toString();
        this.emitter = new SseEmitter(3600_000L);
        this.mapper = mapper;
    }

    public String getSessionId() { return sessionId; }
    public SseEmitter getEmitter() { return emitter; }

    public void sendEvent(String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(JsonRpcMessage message) {
        try {
            emitter.send(SseEmitter.event()
                    .name("message")
                    .data(mapper.writeValueAsString(message)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void complete() {
        try { emitter.complete(); } catch (Exception ignored) {}
    }
}
