package com.btob.app.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;

public interface McpTool {
    String getName();
    String getDescription();
    Map<String, Object> getInputSchema();
    Object execute(JsonNode arguments);
}
