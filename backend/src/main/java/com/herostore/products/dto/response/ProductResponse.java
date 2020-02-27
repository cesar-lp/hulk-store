package com.herostore.products.dto.response;

import com.herostore.products.dto.ProductTypeDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse implements Serializable {

    static final long serialVersionUID = 3166209577379820719L;

    Long id;
    String name;
    ProductTypeDTO productType;
    String productTypeName;
    Integer stock;
    BigDecimal price;
}
