package com.todo.hulkstore.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductOrderDTO implements Serializable {

    private static final long serialVersionUID = -2041025772697180892L;

    Long id;
    Long productId;
    String productName;
    BigDecimal productPrice;
    Integer quantity;
    BigDecimal total;
}
