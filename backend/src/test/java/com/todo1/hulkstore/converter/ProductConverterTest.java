package com.todo1.hulkstore.converter;

import com.todo1.hulkstore.domain.Product;
import com.todo1.hulkstore.domain.ProductType;
import com.todo1.hulkstore.dto.ProductDTO;
import com.todo1.hulkstore.dto.ProductTypeDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductConverterTest {

    private static ProductConverter productConverter;

    @BeforeAll
    static void setUp() {
        productConverter = new ProductConverter();
    }

    @Test
    void shouldReturnEmptyProductDTOListWhenConvertingFromInvalidProductList() {
        List<ProductDTO> returnedList = productConverter.toProductDTOList(null);
        assertNotNull(returnedList);
        assertTrue(returnedList.isEmpty());

        returnedList = productConverter.toProductDTOList(new ArrayList<>());
        assertNotNull(returnedList);
        assertTrue(returnedList.isEmpty());
    }

    @Test
    void shouldConvertProductToProductDTO() {
        Product product = Product.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductType(1L, "Cup"))
                .price(BigDecimal.valueOf(25.00))
                .stock(20)
                .build();
        ProductDTO dto = productConverter.toProductDTO(product);

        assertEquals(product.getId(), dto.getId());
        assertEquals(product.getName(), dto.getName());
        assertEquals(product.getPrice(), dto.getPrice());
        assertEquals(product.getProductType().getName(), dto.getProductType().getName());
        assertEquals(product.getStock(), dto.getStock());
    }

    @Test
    void shouldThrowExceptionWhenConvertingFromNullToProductDTO() {
        assertThrows(IllegalArgumentException.class, () -> productConverter.toProductDTO(null));
    }

    @Test
    void shouldConvertProductDTOToProduct() {
        ProductDTO dto = ProductDTO.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductTypeDTO(1L, "Cup"))
                .price(BigDecimal.valueOf(25.00))
                .stock(20)
                .build();
        Product product = productConverter.toProduct(dto);

        assertEquals(dto.getId(), product.getId());
        assertEquals(dto.getName(), product.getName());
        assertEquals(dto.getPrice(), product.getPrice());
        assertEquals(dto.getProductType().getName(), product.getProductType().getName());
        assertEquals(dto.getStock(), product.getStock());
    }

    @Test
    void shouldThrowExceptionWhenConvertingFromNullToProduct() {
        assertThrows(IllegalArgumentException.class, () -> productConverter.toProduct(null));
    }

    @Test
    void shouldConvertProductTypeToProductTypeDTO() {
        ProductType productType = new ProductType(1L, "Cup");
        ProductTypeDTO dto = productConverter.toProductTypeDTO(productType);

        assertEquals(productType.getId(), dto.getId());
        assertEquals(productType.getName(), dto.getName());
    }

    @Test
    void shouldThrowExceptionWhenConvertingFromNullToProductTypeDTO() {
        assertThrows(IllegalArgumentException.class, () -> productConverter.toProductTypeDTO(null));
    }

    @Test
    void shouldConvertProductTypeDTOToProductType() {
        ProductTypeDTO dto = new ProductTypeDTO(1L, "Cup");
        ProductType productType = productConverter.toProductType(dto);

        assertEquals(dto.getId(), productType.getId());
        assertEquals(dto.getName(), productType.getName());
    }

    @Test
    void shouldThrowExceptionWhenConvertingFromNullToProductType() {
        assertThrows(IllegalArgumentException.class, () -> productConverter.toProductType(null));
    }
}
