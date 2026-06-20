package com.btob.app.mcp.handler;

import com.btob.app.domain.entity.SalesOrder;
import com.btob.app.domain.repository.SalesOrderRepository;
import com.btob.app.exception.ResourceNotFoundException;
import com.btob.app.mcp.McpTool;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class CheckOrderStatusTool implements McpTool {

    private final SalesOrderRepository orderRepository;

    public CheckOrderStatusTool(SalesOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public String getName() {
        return "check_order_status";
    }

    @Override
    public String getDescription() {
        return "Check the current status of a sales order by its order number.";
    }

    @Override
    public Map<String, Object> getInputSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new LinkedHashMap<>();
        Map<String, Object> orderIdProp = new LinkedHashMap<>();
        orderIdProp.put("type", "string");
        orderIdProp.put("description", "The sales order number (e.g., ORD-001)");
        properties.put("orderId", orderIdProp);
        schema.put("properties", properties);

        schema.put("required", List.of("orderId"));
        return schema;
    }

    @Override
    public Object execute(JsonNode arguments) {
        String orderId = arguments.get("orderId").asText();
        SalesOrder order = orderRepository.findByOrderNumber(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("SalesOrder", orderId));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", order.getOrderNumber());
        result.put("status", order.getStatus().name());
        result.put("accountName", order.getAccount().getCompanyName());
        result.put("totalAmount", order.getTotalAmount());
        result.put("orderDate", order.getOrderDate().toString());

        var items = order.getLineItems().stream().map(item -> {
            Map<String, Object> itemMap = new LinkedHashMap<>();
            itemMap.put("sku", item.getPart().getSku());
            itemMap.put("partName", item.getPart().getName());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("unitPrice", item.getUnitPrice());
            return itemMap;
        }).toList();
        result.put("items", items);

        return result;
    }
}
