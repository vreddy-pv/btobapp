package com.btob.app.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_orders")
public class SalesOrder {
    public enum Status { PENDING, SHIPPED, DELIVERED, CANCELLED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private B2BAccount account;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderLineItem> lineItems = new ArrayList<>();

    protected SalesOrder() {}

    public SalesOrder(String orderNumber, B2BAccount account) {
        this.orderNumber = orderNumber;
        this.account = account;
        this.status = Status.PENDING;
        this.orderDate = LocalDateTime.now();
        this.totalAmount = BigDecimal.ZERO;
    }

    public void addLineItem(OrderLineItem item) {
        item.setOrder(this);
        this.lineItems.add(item);
        this.totalAmount = lineItems.stream()
                .map(OrderLineItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public B2BAccount getAccount() { return account; }
    public Status getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public List<OrderLineItem> getLineItems() { return lineItems; }

    public void setStatus(Status status) { this.status = status; }
}
