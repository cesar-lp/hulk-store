package com.todo1.hulkstore.converter;

import com.todo1.hulkstore.domain.Product;
import com.todo1.hulkstore.dto.ProductDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {

    public Converter<Product, ProductDTO> toProductDTO() {
        return source -> {
            ProductDTO dto = null;

            if (source != null) {
                dto = new ProductDTO();
            }

            return dto;
        };
    }
}
