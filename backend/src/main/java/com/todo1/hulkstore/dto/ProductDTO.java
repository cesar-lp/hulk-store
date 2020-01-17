package com.todo1.hulkstore.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder()
public class ProductDTO {
    Long id;
    String name;
    ProductTypeDTO productType;
    BigDecimal price;
    Integer stock;
}
