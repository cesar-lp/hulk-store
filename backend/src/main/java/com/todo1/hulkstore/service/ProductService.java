package com.todo1.hulkstore.service;

import com.todo1.hulkstore.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(Long id);
    ProductDTO createProduct(ProductDTO newProduct);
    ProductDTO updateProduct(Long id, ProductDTO updatedProduct);
    void deleteProductById(Long id);
}
