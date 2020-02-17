package com.todo.hulkstore.service;

import com.todo.hulkstore.dto.request.PaymentOrderRequest;
import com.todo.hulkstore.dto.response.PaymentOrderResponse;

import java.util.List;

public interface PaymentOrderService {

    List<PaymentOrderResponse> getAllPaymentOrders();

    PaymentOrderResponse registerPaymentOrder(PaymentOrderRequest paymentOrderRequest);
}
