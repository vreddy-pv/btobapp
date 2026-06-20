package com.btob.app.dto;

import com.btob.app.domain.entity.OrderLineItem;
import com.btob.app.domain.entity.SalesOrder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SalesOrderDTO {
    private String orderNumber;
    private String accountNumber;
    private String accountName;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private List<LineItemDTO> items;

    public static SalesOrderDTO fromEntity(SalesOrder order) {
        SalesOrderDTO dto = new SalesOrderDTO();
        dto.orderNumber = order.getOrderNumber();
        dto.accountNumber = order.getAccount().getAccountNumber();
        dto.accountName = order.getAccount().getCompanyName();
        dto.status = order.getStatus().name();
        dto.totalAmount = order.getTotalAmount();
        dto.orderDate = order.getOrderDate();
        dto.items = order.getLineItems().stream()
                .map(LineItemDTO::fromEntity)
                .collect(Collectors.toList());
        return dto;
    }

    public String getOrderNumber() { return orderNumber; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountName() { return accountName; }
    public String getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public List<LineItemDTO> getItems() { return items; }

    public static class LineItemDTO {
        private String sku;
        private String partName;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;

        public static LineItemDTO fromEntity(OrderLineItem item) {
            LineItemDTO dto = new LineItemDTO();
            dto.sku = item.getPart().getSku();
            dto.partName = item.getPart().getName();
            dto.quantity = item.getQuantity();
            dto.unitPrice = item.getUnitPrice();
            dto.subtotal = item.getSubtotal();
            return dto;
        }

        public String getSku() { return sku; }
        public String getPartName() { return partName; }
        public int getQuantity() { return quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public BigDecimal getSubtotal() { return subtotal; }
    }
}
