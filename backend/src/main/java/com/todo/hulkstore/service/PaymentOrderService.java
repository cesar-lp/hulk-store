package com.todo.hulkstore.service;

import com.todo.hulkstore.dto.PaymentOrderRequestDTO;

public interface PaymentOrderService {
    void registerOrder(PaymentOrderRequestDTO paymentOrderRequestDTO);
}
