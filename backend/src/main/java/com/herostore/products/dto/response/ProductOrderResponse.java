package com.herostore.products.dto.response;

import com.herostore.products.dto.ProductOrderLineDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductOrderResponse implements Serializable {

    static final long serialVersionUID = 2722848793699982149L;

    Long id;
    List<ProductOrderLineDTO> productOrderLines;
    LocalDateTime createdAt;
    BigDecimal total;
}
