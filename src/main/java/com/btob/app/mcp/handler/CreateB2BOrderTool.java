package com.btob.app.mcp.handler;

import com.btob.app.domain.entity.AutoPart;
import com.btob.app.domain.entity.B2BAccount;
import com.btob.app.domain.entity.OrderLineItem;
import com.btob.app.domain.entity.SalesOrder;
import com.btob.app.domain.repository.AutoPartRepository;
import com.btob.app.domain.repository.B2BAccountRepository;
import com.btob.app.domain.repository.SalesOrderRepository;
import com.btob.app.exception.ResourceNotFoundException;
import com.btob.app.mcp.McpTool;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Component
public class CreateB2BOrderTool implements McpTool {

    private final SalesOrderRepository orderRepository;
    private final B2BAccountRepository accountRepository;
    private final AutoPartRepository partRepository;

    public CreateB2BOrderTool(SalesOrderRepository orderRepository,
                              B2BAccountRepository accountRepository,
                              AutoPartRepository partRepository) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
        this.partRepository = partRepository;
    }

    @Override
    public String getName() {
        return "create_b2b_order";
    }

    @Override
    public String getDescription() {
        return "Create a new B2B sales order for a given account with specified parts and quantities.";
    }

    @Override
    public Map<String, Object> getInputSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new LinkedHashMap<>();

        Map<String, Object> accountIdProp = new LinkedHashMap<>();
        accountIdProp.put("type", "string");
        accountIdProp.put("description", "The B2B account number placing the order (e.g., ACC-001)");
        properties.put("accountId", accountIdProp);

        Map<String, Object> itemsProp = new LinkedHashMap<>();
        itemsProp.put("type", "array");
        itemsProp.put("description", "List of parts and quantities to order");
        Map<String, Object> itemsObj = new LinkedHashMap<>();
        itemsObj.put("type", "object");
        itemsObj.put("properties", new LinkedHashMap<>() {{
            put("sku", Map.of("type", "string", "description", "SKU of the auto part"));
            put("quantity", Map.of("type", "integer", "minimum", 1, "description", "Quantity to order"));
        }});
        itemsObj.put("required", List.of("sku", "quantity"));
        itemsProp.put("items", itemsObj);
        properties.put("items", itemsProp);

        schema.put("properties", properties);
        schema.put("required", List.of("accountId", "items"));
        return schema;
    }

    @Override
    @Transactional
    public Object execute(JsonNode arguments) {
        String accountId = arguments.get("accountId").asText();
        JsonNode itemsNode = arguments.get("items");

        B2BAccount account = accountRepository.findByAccountNumber(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("B2BAccount", accountId));

        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        SalesOrder order = new SalesOrder(orderNumber, account);

        BigDecimal total = BigDecimal.ZERO;
        List<Map<String, Object>> orderedItems = new ArrayList<>();

        for (JsonNode item : itemsNode) {
            String sku = item.get("sku").asText();
            int quantity = item.get("quantity").asInt();

            AutoPart part = partRepository.findBySku(sku)
                    .orElseThrow(() -> new ResourceNotFoundException("AutoPart", sku));

            part.reduceInventory(quantity);
            partRepository.save(part);

            OrderLineItem lineItem = new OrderLineItem(order, part, quantity, part.getB2bPrice());
            order.addLineItem(lineItem);
            total = total.add(lineItem.getSubtotal());

            orderedItems.add(Map.of("sku", sku, "partName", part.getName(), "quantity", quantity));
        }

        account.setCurrentBalance(account.getCurrentBalance().add(total));
        accountRepository.save(account);

        orderRepository.save(order);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", order.getOrderNumber());
        result.put("status", "PENDING");
        result.put("totalAmount", total);
        result.put("items", orderedItems);
        result.put("message", "Order " + order.getOrderNumber() + " created successfully.");
        return result;
    }
}
