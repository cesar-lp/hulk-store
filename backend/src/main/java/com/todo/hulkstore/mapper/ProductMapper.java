package com.todo.hulkstore.mapper;

import com.todo.hulkstore.domain.Product;
import com.todo.hulkstore.domain.ProductType;
import com.todo.hulkstore.dto.ProductTypeDTO;
import com.todo.hulkstore.dto.request.ProductRequest;
import com.todo.hulkstore.dto.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductTypeMapper productTypeMapper = Mappers.getMapper(ProductTypeMapper.class);

    @Mapping(source = "productType", target = "productType", qualifiedByName = "toProductTypeDTO")
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "id", source = "productRequest.id")
    @Mapping(target = "name", source = "productRequest.name")
    @Mapping(target = "stock", source = "productRequest.stock")
    @Mapping(target = "price", source = "productRequest.price")
    Product toProduct(ProductRequest productRequest, ProductType productType);

    @Named("toProductTypeDTO")
    default ProductTypeDTO toProductTypeDTO(ProductType productType) {
        return productTypeMapper.toProductTypeDTO(productType);
    }

    default List<ProductResponse> toProductResponseList(List<Product> productList) {
        if (productList == null || productList.isEmpty()) return emptyList();
        return productList.stream().map(this::toProductResponse).collect(toList());
    }
}
