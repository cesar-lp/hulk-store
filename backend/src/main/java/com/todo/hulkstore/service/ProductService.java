package com.todo.hulkstore.service;

import com.todo.hulkstore.dto.ProductDTO;

import java.util.List;

public interface ProductService {

    List<ProductDTO> getAllProducts();

    ProductDTO getProductById(Long id);

    ProductDTO createProduct(ProductDTO newProduct);

    ProductDTO updateProduct(Long id, ProductDTO updatedProduct);

    void deleteProductById(Long id);
}
