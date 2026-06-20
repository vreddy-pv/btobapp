package com.btob.app.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "auto_parts")
public class AutoPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private BigDecimal b2bPrice;

    @Column(nullable = false)
    private Integer inventoryLevel;

    private String imageUrl;

    protected AutoPart() {}

    public AutoPart(String sku, String name, String description, String category,
                    BigDecimal b2bPrice, Integer inventoryLevel, String imageUrl) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.category = category;
        this.b2bPrice = b2bPrice;
        this.inventoryLevel = inventoryLevel;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public BigDecimal getB2bPrice() { return b2bPrice; }
    public Integer getInventoryLevel() { return inventoryLevel; }
    public String getImageUrl() { return imageUrl; }

    public void reduceInventory(int quantity) {
        if (this.inventoryLevel < quantity) {
            throw new IllegalStateException("Insufficient inventory for SKU: " + this.sku);
        }
        this.inventoryLevel -= quantity;
    }
}
