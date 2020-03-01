package com.herostore.products.handler;

import com.herostore.products.dto.response.ProductOrderResponse;
import com.herostore.products.io.PDFWriter;

import java.util.List;

public interface ProductOrdersPDFWriter extends PDFWriter {

    void setProductOrders(List<ProductOrderResponse> paymentOrders);
}
