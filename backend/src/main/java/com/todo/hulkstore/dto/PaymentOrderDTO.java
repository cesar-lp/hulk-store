package com.todo.hulkstore.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentOrderDTO implements Serializable {

    private static final long serialVersionUID = -1334910142943528788L;

    Long id;
    Long productId;
    String productName;
    BigDecimal total;
    Integer productStockLeft;
}
