package com.todo.hulkstore.converter;

import com.todo.hulkstore.domain.Product;
import com.todo.hulkstore.domain.ProductType;
import com.todo.hulkstore.dto.ProductTypeDTO;
import com.todo.hulkstore.dto.request.ProductRequestDTO;
import com.todo.hulkstore.dto.response.ProductResponseDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class ProductConverter {

    public ProductResponseDTO toProductResponseDTO(Product p) {
        if (p == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }

        return ProductResponseDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .productType(toProductTypeDTO(p.getProductType()))
                .stock(p.getStock())
                .price(p.getPrice())
                .build();
    }

    public Product toProduct(ProductRequestDTO productRequest, ProductType productType) {
        if (productRequest == null || productType == null) {
            throw new IllegalArgumentException("ProductRequestDTO nor ProductType cannot be null.");
        }

        return Product.builder()
                .id(productRequest.getId())
                .name(productRequest.getName())
                .productType(productType)
                .stock(productRequest.getStock())
                .price(productRequest.getPrice())
                .build();
    }

    public List<ProductResponseDTO> toProductResponseDTOList(List<Product> source) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>();
        }

        return source.stream()
                .map(this::toProductResponseDTO)
                .collect(toList());
    }

    public ProductTypeDTO toProductTypeDTO(ProductType source) {
        if (source == null) {
            throw new IllegalArgumentException("ProductTypeDTO cannot be null.");
        }

        return new ProductTypeDTO(source.getId(), source.getName());
    }

    public ProductType toProductType(ProductTypeDTO source) {
        if (source == null) {
            throw new IllegalArgumentException("ProductType cannot be null.");
        }

        return new ProductType(source.getId(), source.getName());
    }

    public List<ProductTypeDTO> toProductTypeDTOList(List<ProductType> source) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>();
        }

        return source.stream()
                .map(this::toProductTypeDTO)
                .collect(toList());
    }
}
