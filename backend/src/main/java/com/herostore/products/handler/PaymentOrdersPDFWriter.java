package com.herostore.products.handler;

import com.herostore.products.dto.response.PaymentOrderResponse;
import com.herostore.products.io.PDFWriter;

import java.util.List;

public interface PaymentOrdersPDFWriter extends PDFWriter {

    void setPaymentOrders(List<PaymentOrderResponse> paymentOrders);
}
