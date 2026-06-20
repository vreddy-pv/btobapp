package com.btob.app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class OrderRequestDTO {
    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<OrderItemDTO> items;

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }

    public static class OrderItemDTO {
        @NotBlank(message = "SKU is required")
        private String sku;

        private int quantity;

        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}
