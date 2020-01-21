package com.todo.hulkstore.service;

import com.todo.hulkstore.dto.ProductOrderDTO;
import com.todo.hulkstore.dto.request.PaymentOrderRequestDTO;
import com.todo.hulkstore.dto.response.PaymentOrderResponseDTO;

import java.util.List;

public interface PaymentOrderService {

    List<PaymentOrderResponseDTO> getAllPaymentOrders();

    PaymentOrderResponseDTO registerPaymentOrder(PaymentOrderRequestDTO paymentOrderRequestDTO);
}
