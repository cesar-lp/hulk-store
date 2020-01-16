package com.todo.hulkstore.converter;

import com.todo.hulkstore.domain.Product;
import com.todo.hulkstore.domain.ProductType;
import com.todo.hulkstore.dto.ProductTypeDTO;
import com.todo.hulkstore.dto.request.ProductRequestDTO;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductConverterTest {

    ProductConverter productConverter = new ProductConverter();

    @Test
    void shouldReturnEmptyListWhenConvertingToProductResponseDTOListFromInvalidInput() {
        var returnedList = productConverter.toProductResponseDTOList(null);
        assertNotNull(returnedList);
        assertTrue(returnedList.isEmpty());

        returnedList = productConverter.toProductResponseDTOList(new ArrayList<>());
        assertNotNull(returnedList);
        assertTrue(returnedList.isEmpty());
    }

    @Test
    void shouldConvertProductToProductResponseDTO() {
        var productTypeDTO = new ProductTypeDTO(1L, "Cups");
        var product = Product.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(new ProductType(1L, "Cups"))
                .price(BigDecimal.valueOf(25.00))
                .stock(25)
                .build();
        var dto = productConverter.toProductResponseDTO(product);

        assertEquals(product.getId(), dto.getId());
        assertEquals(product.getName(), dto.getName());
        assertEquals(product.getPrice(), dto.getPrice());
        assertEquals(product.getStock(), dto.getStock());
        assertThat(dto.getProductType(), samePropertyValuesAs(productTypeDTO));
    }

    @Test
    void shouldThrowExceptionWhenConvertingToProductResponseDTOFromInvalidInput() {
        assertThrows(IllegalArgumentException.class,
                () -> productConverter.toProductResponseDTO(null));
    }

    @Test
    void shouldConvertProductRequestDTOtoProduct() {
        var productType = new ProductType(1L, "Cups");
        var dto = ProductRequestDTO.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productTypeId(1L)
                .stock(25)
                .price(BigDecimal.valueOf(25.00))
                .build();
        Product product = productConverter.toProduct(dto, productType);

        assertEquals(dto.getId(), product.getId());
        assertEquals(dto.getName(), product.getName());
        assertEquals(dto.getStock(), product.getStock());
        assertEquals(dto.getPrice(), product.getPrice());
        assertThat(product.getProductType(), samePropertyValuesAs(productType));
    }

    @Test
    void shouldThrowExceptionWhenConvertingToProductFromInvalidInput() {
        assertThrows(IllegalArgumentException.class,
                () -> productConverter.toProduct(null, null));

        assertThrows(IllegalArgumentException.class,
                () -> productConverter.toProduct(ProductRequestDTO.builder().build(), null));

        assertThrows(IllegalArgumentException.class,
                () -> productConverter.toProduct(null, new ProductType()));
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
