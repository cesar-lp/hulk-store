package com.herostore.products.service;

import com.herostore.products.constants.FileType;
import com.herostore.products.dto.request.ProductOrderRequest;
import com.herostore.products.dto.response.ProductOrderResponse;

import java.io.OutputStream;
import java.util.List;

public interface ProductOrderService {

    List<ProductOrderResponse> getAllProductOrders();

    ProductOrderResponse registerProductOrder(ProductOrderRequest productOrderRequest);

    void exportProductOrders(OutputStream os, FileType fileType);
}
