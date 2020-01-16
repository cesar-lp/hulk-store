package com.todo1.hulkstore.service.impl;

import com.todo1.hulkstore.dto.ProductDTO;
import com.todo1.hulkstore.converter.ProductConverter;
import com.todo1.hulkstore.repository.ProductRepository;
import com.todo1.hulkstore.service.ProductService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    ProductConverter converter;

    @Override
    public List<ProductDTO> getAllProducts() {
        return null;
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return null;
    }

    @Override
    public ProductDTO createProduct(ProductDTO newProduct) {
        return null;
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO updatedProduct) {
        return null;
    }

    @Override
    public void deleteProductById(Long id) {

    }
}

