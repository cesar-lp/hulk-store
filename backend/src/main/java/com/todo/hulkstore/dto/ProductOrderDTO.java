package com.todo.hulkstore.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductOrderDTO {

    Long id;
    String productName;
    BigDecimal productPrice;
    Integer quantity;
    BigDecimal total;
}
