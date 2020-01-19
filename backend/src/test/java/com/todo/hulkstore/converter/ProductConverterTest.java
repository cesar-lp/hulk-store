package com.todo.hulkstore.converter;

import com.todo.hulkstore.domain.Product;
import com.todo.hulkstore.domain.ProductType;
import com.todo.hulkstore.dto.ProductDTO;
import com.todo.hulkstore.dto.ProductTypeDTO;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductConverterTest {

    static ProductConverter productConverter;

    @BeforeAll
    static void setUp() {
        productConverter = new ProductConverter();
    }

    @Test
    void shouldReturnEmptyProductDTOListWhenConvertingFromInvalidProductList() {
        var returnedList = productConverter.toProductDTOList(null);
        assertNotNull(returnedList);
        assertTrue(returnedList.isEmpty());

        returnedList = productConverter.toProductDTOList(new ArrayList<>());
        assertNotNull(returnedList);
        assertTrue(returnedList.isEmpty());
    }

    @Test
    void shouldConvertProductToProductDTO() {
        var product = Product.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductType(1L, "Cup"))
                .price(BigDecimal.valueOf(25.00))
                .build();
        ProductDTO dto = productConverter.toProductDTO(product);

        assertEquals(product.getId(), dto.getId());
        assertEquals(product.getName(), dto.getName());
        assertEquals(product.getPrice(), dto.getPrice());
        assertEquals(product.getProductType().getName(), dto.getProductType().getName());
    }

    @Test
    void shouldThrowExceptionWhenConvertingFromNullToProductDTO() {
        assertThrows(IllegalArgumentException.class, () -> productConverter.toProductDTO(null));
    }

    @Test
    void shouldConvertProductDTOToProduct() {
        var dto = ProductDTO.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductTypeDTO(1L, "Cup"))
                .price(BigDecimal.valueOf(25.00))
                .build();
        Product product = productConverter.toProduct(dto);

        assertEquals(dto.getId(), product.getId());
        assertEquals(dto.getName(), product.getName());
        assertEquals(dto.getPrice(), product.getPrice());
        assertEquals(dto.getProductType().getName(), product.getProductType().getName());
    }

    @Test
    void shouldThrowExceptionWhenConvertingFromNullToProduct() {
        assertThrows(IllegalArgumentException.class, () -> productConverter.toProduct(null));
    }

    @Test
    void shouldReturnEmptyProductTypeDTOListWhenConvertingFromInvalidValues() {
        var returnedList = productConverter.toProductTypeDTOList(null);
        assertNotNull(returnedList);
        assertTrue(returnedList.isEmpty());

        returnedList = productConverter.toProductTypeDTOList(new ArrayList<>());
        assertNotNull(returnedList);
        assertTrue(returnedList.isEmpty());
    }

    @Test
    void shouldConvertProductTypeToProductTypeDTO() {
        var productType = new ProductType(1L, "Cup");
        var dto = productConverter.toProductTypeDTO(productType);

        assertEquals(productType.getId(), dto.getId());
        assertEquals(productType.getName(), dto.getName());
    }

    @Test
    void shouldThrowExceptionWhenConvertingFromNullToProductTypeDTO() {
        assertThrows(IllegalArgumentException.class, () -> productConverter.toProductTypeDTO(null));
    }

    @Test
    void shouldConvertProductTypeDTOToProductType() {
        var dto = new ProductTypeDTO(1L, "Cup");
        var productType = productConverter.toProductType(dto);

        assertEquals(dto.getId(), productType.getId());
        assertEquals(dto.getName(), productType.getName());
    }

    @Test
    void shouldThrowExceptionWhenConvertingFromNullToProductType() {
        assertThrows(IllegalArgumentException.class, () -> productConverter.toProductType(null));
    }
}
