package com.todo1.hulkstore.service.impl;

import com.todo1.hulkstore.converter.ProductConverter;
import com.todo1.hulkstore.dto.ProductDTO;
import com.todo1.hulkstore.exception.ResourceNotFoundException;
import com.todo1.hulkstore.repository.ProductRepository;
import com.todo1.hulkstore.service.ProductService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductServiceImpl implements ProductService {

    Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class.getName());

    ProductRepository productRepository;
    ProductConverter converter;

    @Override
    public List<ProductDTO> getAllProducts() {
        return converter.toProductDTOList(productRepository.findAll());
    }

    @Override
    public ProductDTO getProductById(Long id) {
        var product = productRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.error("getProductById: Product not found for id {}.", id);
                    return new ResourceNotFoundException("Product not found for id " + id);
                });

        return converter.toProductDTO(product);
    }

    @Override
    public ProductDTO createProduct(ProductDTO newProductDTO) {
        var newProduct = converter.toProduct(newProductDTO);
        return converter.toProductDTO(productRepository.save(newProduct));
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO updatedProductDTO) {
        var updated = converter.toProduct(updatedProductDTO);
        var existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("updateProduct: Product not found for id {}.", id);
                    return new ResourceNotFoundException("Product not found for id " + id);
                });

        return converter.toProductDTO(productRepository.save(existingProduct));
    }

    @Override
    public void deleteProductById(Long id) {
        var productToDelete = productRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.error("deleteProductById: Product not found for id {}.", id);
                    return new ResourceNotFoundException("Product not found for id " + id);
                });
        productRepository.delete(productToDelete);
    }
}

