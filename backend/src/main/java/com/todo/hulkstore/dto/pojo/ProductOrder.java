package com.todo.hulkstore.dto.pojo;

import com.todo.hulkstore.domain.Product;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class ProductOrder {
    Long productId;
    String name;
    BigDecimal price;
    Integer quantity;

    public static ProductOrder getFor(Product product, Integer requestedQuantity) {
        return ProductOrder.builder()
                .productId(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(requestedQuantity)
                .build();
    }
}
