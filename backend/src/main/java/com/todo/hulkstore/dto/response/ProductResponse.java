package com.todo.hulkstore.dto.response;

import com.todo.hulkstore.dto.ProductTypeDTO;
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
    Integer stock;
    BigDecimal price;
}
