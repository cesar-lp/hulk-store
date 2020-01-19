package com.todo.hulkstore.service;

import com.todo.hulkstore.dto.PaymentOrderDTO;
import com.todo.hulkstore.dto.request.PaymentOrderRequestDTO;

import java.util.List;

public interface PaymentOrderService {
    List<PaymentOrderDTO> registerOrder(PaymentOrderRequestDTO paymentOrderRequestDTO);
}
