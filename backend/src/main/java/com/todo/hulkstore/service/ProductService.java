package com.todo.hulkstore.service;

import com.todo.hulkstore.dto.request.ProductRequestDTO;
import com.todo.hulkstore.dto.response.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    List<ProductResponseDTO> getAllProducts();

    ProductResponseDTO getProductById(Long id);

    ProductResponseDTO createProduct(ProductRequestDTO newProduct);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO updatedProduct);

    void deleteProductById(Long id);
}
