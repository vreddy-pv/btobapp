package com.btob.app.dto;

import com.btob.app.domain.entity.AutoPart;
import java.math.BigDecimal;

public class AutoPartDTO {
    private String sku;
    private String name;
    private String description;
    private String category;
    private BigDecimal b2bPrice;
    private Integer inventoryLevel;
    private String imageUrl;

    public static AutoPartDTO fromEntity(AutoPart part) {
        AutoPartDTO dto = new AutoPartDTO();
        dto.sku = part.getSku();
        dto.name = part.getName();
        dto.description = part.getDescription();
        dto.category = part.getCategory();
        dto.b2bPrice = part.getB2bPrice();
        dto.inventoryLevel = part.getInventoryLevel();
        dto.imageUrl = part.getImageUrl();
        return dto;
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public BigDecimal getB2bPrice() { return b2bPrice; }
    public Integer getInventoryLevel() { return inventoryLevel; }
    public String getImageUrl() { return imageUrl; }
}
