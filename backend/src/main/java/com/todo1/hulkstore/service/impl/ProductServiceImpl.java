package com.todo1.hulkstore.service.impl;

import com.todo1.hulkstore.converter.ProductConverter;
import com.todo1.hulkstore.domain.Product;
import com.todo1.hulkstore.dto.ProductDTO;
import com.todo1.hulkstore.repository.ProductRepository;
import com.todo1.hulkstore.service.ProductService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    ProductConverter converter;

    @Override
    public List<ProductDTO> getAllProducts() {
        return converter.toProductDTOList(productRepository.findAll());
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository
                .findById(id)
                .map(converter::toProductDTO)
                .orElse(null);
    }

    @Override
    public ProductDTO createProduct(ProductDTO newProductDTO) {
        Product newProduct = converter.toProduct(newProductDTO);
        return converter.toProductDTO(productRepository.save(newProduct));
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO updatedProductDTO) {
        Product updated = converter.toProduct(updatedProductDTO);
        return productRepository.findById(id)
                .flatMap(old -> updateProductDetails(old, updated))
                .map(p -> converter.toProductDTO(productRepository.save(p)))
                .orElse(null);
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    private Optional<Product> updateProductDetails(Product old, Product updated) {
        old.setName(updated.getName());
        old.setPrice(updated.getPrice());
        old.setStock(updated.getStock());
        old.setProductType(updated.getProductType());
        return Optional.of(old);
    }
}

