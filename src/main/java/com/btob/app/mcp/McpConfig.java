package com.btob.app.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider mcpToolCallbackProvider(McpTools mcpTools) {
        return MethodToolCallbackProvider.builder().toolObjects(mcpTools).build();
    }
}
