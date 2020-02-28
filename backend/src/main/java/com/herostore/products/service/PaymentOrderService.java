package com.herostore.products.service;

import com.herostore.products.constants.FileType;
import com.herostore.products.dto.request.PaymentOrderRequest;
import com.herostore.products.dto.response.PaymentOrderResponse;

import java.io.OutputStream;
import java.util.List;

public interface PaymentOrderService {

    List<PaymentOrderResponse> getAllPaymentOrders();

    PaymentOrderResponse registerPaymentOrder(PaymentOrderRequest paymentOrderRequest);

    void exportPaymentOrders(OutputStream os, FileType fileType);
}
