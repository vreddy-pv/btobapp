package com.btob.app.mcp;

import com.btob.app.domain.entity.AutoPart;
import com.btob.app.domain.entity.B2BAccount;
import com.btob.app.domain.entity.OrderLineItem;
import com.btob.app.domain.entity.SalesOrder;
import com.btob.app.domain.repository.AutoPartRepository;
import com.btob.app.domain.repository.B2BAccountRepository;
import com.btob.app.domain.repository.SalesOrderRepository;
import com.btob.app.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class McpTools {

    private final SalesOrderRepository orderRepository;
    private final B2BAccountRepository accountRepository;
    private final AutoPartRepository partRepository;
    private final ObjectMapper mapper;

    public McpTools(SalesOrderRepository orderRepository,
                    B2BAccountRepository accountRepository,
                    AutoPartRepository partRepository,
                    ObjectMapper mapper) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
        this.partRepository = partRepository;
        this.mapper = mapper;
    }

    @Tool(description = "Check the current status of a sales order by its order number. Returns order details including status, account name, total amount, date, and line items.")
    @Transactional(readOnly = true)
    public String checkOrderStatus(
            @ToolParam(description = "The sales order number (e.g., ORD-001)") String orderId) {

        SalesOrder order = orderRepository.findByOrderNumber(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("SalesOrder", orderId));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", order.getOrderNumber());
        result.put("status", order.getStatus().name());
        result.put("accountName", order.getAccount().getCompanyName());
        result.put("totalAmount", order.getTotalAmount());
        result.put("orderDate", order.getOrderDate().toString());

        List<Map<String, Object>> items = order.getLineItems().stream().map(item -> {
            Map<String, Object> itemMap = new LinkedHashMap<>();
            itemMap.put("sku", item.getPart().getSku());
            itemMap.put("partName", item.getPart().getName());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("unitPrice", item.getUnitPrice());
            return itemMap;
        }).collect(Collectors.toList());
        result.put("items", items);

        return writeJson(result);
    }

    @Tool(description = "Create a new B2B sales order for a given account with specified parts and quantities. Returns the created order details.")
    @Transactional
    public String createB2BOrder(
            @ToolParam(description = "The B2B account number placing the order (e.g., ACC-001)") String accountId,
            @ToolParam(description = "List of items to order as a JSON array, each with 'sku' (string) and 'quantity' (integer). Example: [{\"sku\":\"BRK-001\",\"quantity\":10}]") List<Map<String, Object>> items) {

        B2BAccount account = accountRepository.findByAccountNumber(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("B2BAccount", accountId));

        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        SalesOrder order = new SalesOrder(orderNumber, account);

        BigDecimal total = BigDecimal.ZERO;
        List<Map<String, Object>> orderedItems = new ArrayList<>();

        for (Map<String, Object> item : items) {
            String sku = (String) item.get("sku");
            int quantity = ((Number) item.get("quantity")).intValue();

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

        return writeJson(result);
    }

    private String writeJson(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize response", e);
        }
    }
}
