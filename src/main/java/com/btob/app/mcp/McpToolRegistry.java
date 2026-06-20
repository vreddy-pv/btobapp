package com.btob.app.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class McpToolRegistry {

    private final Map<String, McpTool> tools = new LinkedHashMap<>();
    private final ObjectMapper mapper;

    public McpToolRegistry(List<McpTool> toolList, ObjectMapper mapper) {
        this.mapper = mapper;
        toolList.forEach(tool -> tools.put(tool.getName(), tool));
    }

    public List<JsonRpcMessage.ToolDefinition> getToolDefinitions() {
        return tools.values().stream()
                .map(t -> new JsonRpcMessage.ToolDefinition(
                        t.getName(), t.getDescription(), t.getInputSchema()))
                .collect(Collectors.toList());
    }

    public Object callTool(String name, JsonNode arguments) {
        McpTool tool = tools.get(name);
        if (tool == null) {
            throw new IllegalArgumentException("Unknown tool: " + name);
        }
        return tool.execute(arguments);
    }
}
