package com.herostore.products.handler.impl;

import com.herostore.products.dto.ProductOrderLineDTO;
import com.herostore.products.dto.response.ProductOrderResponse;
import com.herostore.products.handler.ProductOrdersPDFWriter;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductOrdersPDFWriterImplTest {

    ProductOrdersPDFWriter pdfWriter = new ProductOrdersPDFWriterImpl();

    @Test
    void shouldWriteSuccessfully() {
        var productOrders = mockProductOrders();
        var os = new ByteArrayOutputStream();

        pdfWriter.createDocument(os);
        pdfWriter.setProductOrders(productOrders);
        pdfWriter.openDocument();
        pdfWriter.writeDocument();
        pdfWriter.closeDocument();

        assertNotNull(os.toByteArray());
        assertFalse(pdfWriter.isDocumentOpen());
    }

    private List<ProductOrderResponse> mockProductOrders() {
        var productOrderLine = ProductOrderLineDTO.builder()
                .id(1L)
                .productId(1L)
                .productName("Iron Man Cup")
                .productPrice(BigDecimal.valueOf(50.00))
                .quantity(10)
                .total(BigDecimal.valueOf(500.00))
                .build();

        var productOrder = ProductOrderResponse.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .productOrderLines(singletonList(productOrderLine))
                .total(BigDecimal.valueOf(500.00))
                .build();

        return singletonList(productOrder);
    }
}