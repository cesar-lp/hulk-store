package com.herostore.products.exception.error;

import com.herostore.products.domain.ProductOrderLine;
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
public class InvalidProductOrderLineError implements Serializable {

    private static final long serialVersionUID = -5215419118823855534L;

    public InvalidProductOrderLineError(ProductOrderLine productOrderLine, Integer availableStock) {
        this.id = productOrderLine.getProductDetail().getId();
        this.name = productOrderLine.getProductDetail().getName();
        this.requestedQuantity = productOrderLine.getQuantity();
        this.stock = availableStock;
    }

    Long id;
    String name;
    Integer requestedQuantity;
    Integer stock;
}
