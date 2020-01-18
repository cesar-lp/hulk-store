package com.todo1.hulkstore.converter;

import com.todo1.hulkstore.domain.Product;
import com.todo1.hulkstore.domain.ProductType;
import com.todo1.hulkstore.dto.ProductDTO;
import com.todo1.hulkstore.dto.ProductTypeDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class ProductConverter {

    public ProductDTO toProductDTO(Product source) {
        if (source == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }

        return ProductDTO.builder()
                .id(source.getId())
                .name(source.getName())
                .productType(toProductTypeDTO(source.getProductType()))
                .price(source.getPrice())
                .stock(source.getStock())
                .build();
    }

    public Product toProduct(ProductDTO source) {
        if (source == null) {
            throw new IllegalArgumentException("ProductDTO cannot be null.");
        }

        return Product.builder()
                .id(source.getId())
                .name(source.getName())
                .productType(toProductType(source.getProductType()))
                .price(source.getPrice())
                .stock(source.getStock())
                .build();
    }

    public List<ProductDTO> toProductDTOList(List<Product> source) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>();
        }

        return source.stream()
                .map(this::toProductDTO)
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
