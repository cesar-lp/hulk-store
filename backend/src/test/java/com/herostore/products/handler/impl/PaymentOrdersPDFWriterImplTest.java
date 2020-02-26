package com.herostore.products.handler.impl;

import com.herostore.products.dto.ProductOrderDTO;
import com.herostore.products.dto.response.PaymentOrderResponse;
import com.herostore.products.handler.PaymentOrdersPDFWriter;
import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentOrdersPDFWriterImplTest {

    PaymentOrdersPDFWriter pdfWriter = new PaymentOrdersPDFWriterImpl();

    @Test
    void shouldWriteSuccessfully() throws DocumentException {
        var paymentOrders = mockPaymentOrders();
        var os = new ByteArrayOutputStream();

        pdfWriter.createDocument(os);
        pdfWriter.setPaymentOrders(paymentOrders);
        pdfWriter.openDocument();
        pdfWriter.writeDocument();
        pdfWriter.closeDocument();

        assertNotNull(os.toByteArray());
        assertFalse(pdfWriter.isDocumentOpen());
    }

    private List<PaymentOrderResponse> mockPaymentOrders() {
        var productOrder = ProductOrderDTO.builder()
                .id(1L)
                .productId(1L)
                .productName("Iron Man Cup")
                .productPrice(BigDecimal.valueOf(50.00))
                .quantity(10)
                .total(BigDecimal.valueOf(500.00))
                .build();

        var paymentOrder = PaymentOrderResponse.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .productOrders(singletonList(productOrder))
                .total(BigDecimal.valueOf(500.00))
                .build();

        return singletonList(paymentOrder);
    }
}