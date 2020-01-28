package com.todo.hulkstore.dto.response;

import com.todo.hulkstore.dto.ProductOrderDTO;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentOrderResponseDTO implements Serializable {

    static final long serialVersionUID = 2722848793699982149L;

    Long id;
    LocalDateTime dateTime;
    List<ProductOrderDTO> productOrders;
    BigDecimal total;
}
