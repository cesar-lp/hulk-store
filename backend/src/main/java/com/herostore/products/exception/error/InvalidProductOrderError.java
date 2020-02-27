package com.herostore.products.exception.error;

import com.herostore.products.domain.ProductOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidProductOrderError implements Serializable {

    private static final long serialVersionUID = -5215419118823855534L;

    public InvalidProductOrderError(ProductOrder productOrder, Integer availableStock) {
        this.id = productOrder.getProductDetail().getId();
        this.name = productOrder.getProductDetail().getName();
        this.requestedQuantity = productOrder.getQuantity();
        this.stock = availableStock;
    }

    Long id;
    String name;
    Integer requestedQuantity;
    Integer stock;
}
