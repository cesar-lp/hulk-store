package com.herostore.products.mapper;

import com.herostore.products.domain.ProductType;
import com.herostore.products.dto.ProductTypeDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ProductTypeMapperTest {

    @Autowired
    private ProductTypeMapper mapper;

    @Test
    void shouldMapToProductTypeDTOSuccessfully() {
        var productType = ProductType.builder()
                .id(1L)
                .name("Cups")
                .build();
        var dto = mapper.toProductTypeDTO(productType);

        assertEquals(productType.getId(), dto.getId());
        assertEquals(productType.getName(), dto.getName());
    }

    @Test
    void shouldMapToProductTypeSuccessfully() {
        var dto = new ProductTypeDTO(1L, "Cups");
        var productType = mapper.toProductType(dto);

        assertEquals(dto.getId(), productType.getId());
        assertEquals(dto.getName(), productType.getName());
    }

    @Test
    void shouldMapToProductTypeDTOListSuccessfully() {
        var productTypeDTOList = mapper.toProductTypeDTOList(null);
        assertTrue(productTypeDTOList.isEmpty());

        productTypeDTOList = mapper.toProductTypeDTOList(emptyList());
        assertTrue(productTypeDTOList.isEmpty());
    }
}

