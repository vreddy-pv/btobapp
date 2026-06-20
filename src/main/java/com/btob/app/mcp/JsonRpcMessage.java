package com.btob.app.mcp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonRpcMessage {

    private String jsonrpc = "2.0";
    private Object id;
    private String method;
    private Object params;
    private Object result;
    private JsonRpcError error;

    public JsonRpcMessage() {}

    public static JsonRpcMessage request(Object id, String method, Object params) {
        JsonRpcMessage msg = new JsonRpcMessage();
        msg.id = id;
        msg.method = method;
        msg.params = params;
        return msg;
    }

    public static JsonRpcMessage success(Object id, Object result) {
        JsonRpcMessage msg = new JsonRpcMessage();
        msg.id = id;
        msg.result = result;
        return msg;
    }

    public static JsonRpcMessage error(Object id, int code, String message) {
        JsonRpcMessage msg = new JsonRpcMessage();
        msg.id = id;
        msg.error = new JsonRpcError(code, message);
        return msg;
    }

    public String getJsonrpc() { return jsonrpc; }
    public Object getId() { return id; }
    public String getMethod() { return method; }
    public Object getParams() { return params; }
    public Object getResult() { return result; }
    public JsonRpcError getError() { return error; }

    public void setId(Object id) { this.id = id; }
    public void setMethod(String method) { this.method = method; }
    public void setParams(Object params) { this.params = params; }

    @JsonProperty("id")
    public void setIdFromString(String id) { this.id = id; }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class JsonRpcError {
        private int code;
        private String message;

        public JsonRpcError() {}
        public JsonRpcError(int code, String message) { this.code = code; this.message = message; }

        public int getCode() { return code; }
        public String getMessage() { return message; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ToolDefinition {
        private String name;
        private String description;
        private Map<String, Object> inputSchema;

        public ToolDefinition(String name, String description, Map<String, Object> inputSchema) {
            this.name = name;
            this.description = description;
            this.inputSchema = inputSchema;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public Map<String, Object> getInputSchema() { return inputSchema; }
    }
}
