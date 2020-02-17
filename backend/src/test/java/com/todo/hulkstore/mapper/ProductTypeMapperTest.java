package com.todo.hulkstore.mapper;

import com.todo.hulkstore.domain.ProductType;
import com.todo.hulkstore.dto.ProductTypeDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductTypeMapperTest {

    private ProductTypeMapper mapper = Mappers.getMapper(ProductTypeMapper.class);

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
