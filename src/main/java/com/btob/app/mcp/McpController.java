package com.btob.app.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mcp")
public class McpController {

    private static final Logger log = LoggerFactory.getLogger(McpController.class);
    private static final String PROTOCOL_VERSION = "2025-03-26";

    private final McpSessionManager sessionManager;
    private final McpToolRegistry toolRegistry;
    private final ObjectMapper mapper;

    public McpController(McpSessionManager sessionManager, McpToolRegistry toolRegistry, ObjectMapper mapper) {
        this.sessionManager = sessionManager;
        this.toolRegistry = toolRegistry;
        this.mapper = mapper;
    }

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter handleSse() {
        McpSession session = new McpSession(mapper);
        sessionManager.createSession(session);

        SseEmitter emitter = session.getEmitter();

        session.sendEvent("endpoint",
                Map.of("sessionId", session.getSessionId(),
                       "messageUrl", "/mcp/message?sessionId=" + session.getSessionId()));

        emitter.onCompletion(() -> sessionManager.removeSession(session.getSessionId()));
        emitter.onTimeout(() -> sessionManager.removeSession(session.getSessionId()));

        return emitter;
    }

    @PostMapping("/message")
    public ResponseEntity<Void> handleMessage(
            @RequestParam String sessionId,
            @RequestBody JsonNode body) {

        McpSession session = sessionManager.getSession(sessionId);
        if (session == null) {
            log.warn("Unknown session: {}", sessionId);
            return ResponseEntity.ok().build();
        }

        try {
            String method = body.get("method").asText();
            Object id = body.get("id");
            JsonNode params = body.get("params");

            log.info("MCP request: method={}, id={}", method, id);

            switch (method) {
                case "initialize" -> handleInitialize(session, id, params);
                case "tools/list" -> handleToolsList(session, id);
                case "tools/call" -> handleToolCall(session, id, params);
                case "notifications/initialized" -> {
                }
                default -> session.sendMessage(JsonRpcMessage.error(id, -32601, "Method not found: " + method));
            }
        } catch (Exception e) {
            log.error("Error handling MCP message", e);
            try {
                session.sendMessage(JsonRpcMessage.error(body.get("id"), -32603, "Internal error: " + e.getMessage()));
            } catch (Exception ignored) {}
        }

        return ResponseEntity.ok().build();
    }

    private void handleInitialize(McpSession session, Object id, JsonNode params) {
        ObjectNode result = mapper.createObjectNode();
        result.put("protocolVersion", PROTOCOL_VERSION);

        ObjectNode capabilities = mapper.createObjectNode();
        ObjectNode toolsCap = mapper.createObjectNode();
        toolsCap.put("listChanged", false);
        capabilities.set("tools", toolsCap);
        result.set("capabilities", capabilities);

        ObjectNode serverInfo = mapper.createObjectNode();
        serverInfo.put("name", "btob-mcp-server");
        serverInfo.put("version", "1.0.0");
        result.set("serverInfo", serverInfo);

        session.sendMessage(JsonRpcMessage.success(id, result));
    }

    private void handleToolsList(McpSession session, Object id) {
        List<JsonRpcMessage.ToolDefinition> tools = toolRegistry.getToolDefinitions();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("tools", tools);
        session.sendMessage(JsonRpcMessage.success(id, result));
    }

    private void handleToolCall(McpSession session, Object id, JsonNode params) {
        String name = params.get("name").asText();
        JsonNode arguments = params.get("arguments");

        try {
            Object result = toolRegistry.callTool(name, arguments);
            session.sendMessage(JsonRpcMessage.success(id, Map.of("content", List.of(
                    Map.of("type", "text", "text", mapper.writeValueAsString(result))
            ))));
        } catch (Exception e) {
            session.sendMessage(JsonRpcMessage.error(id, -32602, e.getMessage()));
        }
    }
}
