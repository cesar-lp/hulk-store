package com.herostore.products.service;

import com.herostore.products.constants.FileType;
import com.herostore.products.constants.ProductStockCondition;
import com.herostore.products.dto.request.ProductRequest;
import com.herostore.products.dto.response.ProductResponse;

import java.io.OutputStream;
import java.util.List;

public interface ProductService {

    List<ProductResponse> getAllProducts(ProductStockCondition stockCondition);

    void exportProductsToFile(OutputStream os, FileType fileType, ProductStockCondition stockCondition);

    ProductResponse getProductById(Long id);

    ProductResponse createProduct(ProductRequest newProduct);

    ProductResponse updateProduct(Long id, ProductRequest updatedProduct);

    void deleteProductById(Long id);
}
