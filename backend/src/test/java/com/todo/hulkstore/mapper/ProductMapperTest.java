package com.todo.hulkstore.mapper;

import com.todo.hulkstore.domain.Product;
import com.todo.hulkstore.domain.ProductType;
import com.todo.hulkstore.dto.request.ProductRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductMapperTest {

    ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void shouldMapProductToProductResponseSuccessfully() {
        var productType = ProductType.builder()
                .id(1L)
                .name("Cups")
                .build();

        var product = Product.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(productType)
                .price(BigDecimal.valueOf(35.00))
                .stock(25)
                .build();

        var productResponse = productMapper.toProductResponse(product);

        assertEquals(product.getId(), productResponse.getId());
        assertEquals(product.getName(), productResponse.getName());
        assertEquals(product.getPrice(), productResponse.getPrice());
        assertEquals(product.getStock(), productResponse.getStock());
        assertEquals(product.getProductType().getId(), productResponse.getProductType().getId());
        assertEquals(product.getProductType().getName(), productResponse.getProductType().getName());
    }

    @Test
    void shouldMapProductRequestToProductSuccessfully() {
        var productType = ProductType.builder()
                .id(1L)
                .name("Cups")
                .build();

        var productRequest = ProductRequest.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productTypeId(1L)
                .price(BigDecimal.valueOf(22.50))
                .stock(15)
                .build();

        var product = productMapper.toProduct(productRequest, productType);

        assertEquals(productRequest.getId(), product.getId());
        assertEquals(productRequest.getName(), product.getName());
        assertEquals(productRequest.getPrice(), product.getPrice());
        assertEquals(productRequest.getStock(), product.getStock());
        assertThat(product.getProductType(), samePropertyValuesAs(productType));
    }

    @Test
    void shouldMapProductListToProductResponseListSuccessfully() {
        var list = productMapper.toProductResponseList(emptyList());
        assertTrue(list.isEmpty());

        list = productMapper.toProductResponseList(null);
        assertTrue(list.isEmpty());

        var productType = ProductType.builder()
                .id(1L)
                .name("Cups")
                .build();

        var ironManCup = Product.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(productType)
                .price(BigDecimal.valueOf(22.50))
                .stock(15)
                .build();

        var batmanCup = Product.builder()
                .id(2L)
                .name("Batman Cup")
                .productType(productType)
                .price(BigDecimal.valueOf(17.50))
                .stock(10)
                .build();

        var productList = asList(ironManCup, batmanCup);
        list = productMapper.toProductResponseList(productList);

        assertEquals(2, list.size());

        var productResponse = list.get(0);
        {
            assertEquals(ironManCup.getId(), productResponse.getId());
            assertEquals(ironManCup.getName(), productResponse.getName());
            assertEquals(ironManCup.getPrice(), productResponse.getPrice());
            assertEquals(ironManCup.getStock(), productResponse.getStock());
            assertEquals(ironManCup.getProductType().getId(), productResponse.getProductType().getId());
            assertEquals(ironManCup.getProductType().getName(), productResponse.getProductType().getName());
        }

        productResponse = list.get(1);
        {
            assertEquals(batmanCup.getId(), productResponse.getId());
            assertEquals(batmanCup.getName(), productResponse.getName());
            assertEquals(batmanCup.getPrice(), productResponse.getPrice());
            assertEquals(batmanCup.getStock(), productResponse.getStock());
            assertEquals(batmanCup.getProductType().getId(), productResponse.getProductType().getId());
            assertEquals(batmanCup.getProductType().getName(), productResponse.getProductType().getName());
        }
    }

    @Test
    void shouldMapProductTypeToProductTypeDTO() {
        var productType = ProductType.builder()
                .id(1L)
                .name("Cups")
                .build();

        var productTypeDTO = productMapper.toProductTypeDTO(productType);

        assertEquals(productType.getId(), productTypeDTO.getId());
        assertEquals(productType.getName(), productTypeDTO.getName());
    }
}
