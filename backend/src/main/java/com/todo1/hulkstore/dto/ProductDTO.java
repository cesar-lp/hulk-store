package com.todo1.hulkstore.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder()
public class ProductDTO {
    Long id;
    String name;
    ProductTypeDTO productType;
    BigDecimal price;
    Integer stock;
}
