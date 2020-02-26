package com.todo.hulkstore.service;

import com.todo.hulkstore.constants.FileType;
import com.todo.hulkstore.constants.ProductStockCondition;
import com.todo.hulkstore.dto.request.ProductRequest;
import com.todo.hulkstore.dto.response.ProductResponse;

import java.io.PrintWriter;
import java.util.List;

public interface ProductService {

    List<ProductResponse> getAllProducts(ProductStockCondition stockCondition);

    void exportToFile(PrintWriter writer, FileType fileType, ProductStockCondition stockCondition);

    ProductResponse getProductById(Long id);

    ProductResponse createProduct(ProductRequest newProduct);

    ProductResponse updateProduct(Long id, ProductRequest updatedProduct);

    void deleteProductById(Long id);
}
