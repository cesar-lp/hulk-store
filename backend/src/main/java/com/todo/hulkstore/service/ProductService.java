package com.todo.hulkstore.service;

import com.todo.hulkstore.dto.request.ProductRequest;
import com.todo.hulkstore.dto.response.ProductResponse;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<ProductResponse> getAllProducts(Optional<Boolean> inStock);

    ProductResponse getProductById(Long id);

    ProductResponse createProduct(ProductRequest newProduct);

    ProductResponse updateProduct(Long id, ProductRequest updatedProduct);

    void deleteProductById(Long id);
}
