package com.todo.hulkstore.dto.response;

import com.todo.hulkstore.dto.ProductOrderDTO;
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
public class PaymentOrderResponse implements Serializable {

    static final long serialVersionUID = 2722848793699982149L;

    Long id;
    List<ProductOrderDTO> productOrders;
    LocalDateTime createdAt;
    BigDecimal total;
}
