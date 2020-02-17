package com.todo.hulkstore.mapper;

import com.todo.hulkstore.domain.ProductType;
import com.todo.hulkstore.dto.ProductTypeDTO;
import org.mapstruct.Mapper;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Mapper(componentModel = "spring")
public interface ProductTypeMapper {

    ProductTypeDTO toProductTypeDTO(ProductType productType);

    ProductType toProductType(ProductTypeDTO productTypeDTO);

    default List<ProductTypeDTO> toProductTypeDTOList(List<ProductType> productTypes) {
        if (productTypes == null || productTypes.isEmpty()) return emptyList();
        return productTypes.stream().map(this::toProductTypeDTO).collect(toList());
    }
}
