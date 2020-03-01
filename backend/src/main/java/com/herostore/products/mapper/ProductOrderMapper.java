package com.herostore.products.mapper;

import com.herostore.products.domain.ProductOrder;
import com.herostore.products.domain.ProductOrderLine;
import com.herostore.products.dto.ProductOrderLineDTO;
import com.herostore.products.dto.response.ProductOrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Mapper(componentModel = "spring")
public interface ProductOrderMapper {

    @Mapping(target = "productOrderLines", source = "productOrderLines", qualifiedByName = "toProductOrderLineDTOList")
    ProductOrderResponse toProductOrderResponse(ProductOrder productOrder);

    @Named("toProductOrderLineDTOList")
    default List<ProductOrderLineDTO> toProductOrderLineDTOList(List<ProductOrderLine> productOrderLines) {
        if (productOrderLines == null || productOrderLines.isEmpty()) return emptyList();
        return productOrderLines.stream().map(this::toProductOrderLineDTO).collect(toList());
    }

    @Mapping(target = "productId", source = "productOrderLine.productDetail.id")
    @Mapping(target = "productName", source = "productOrderLine.productDetail.name")
    @Mapping(target = "productPrice", source = "productOrderLine.productDetail.price")
    ProductOrderLineDTO toProductOrderLineDTO(ProductOrderLine productOrderLine);

    default List<ProductOrderResponse> toProductOrderResponseList(List<ProductOrder> source) {
        if (source == null || source.isEmpty()) return emptyList();
        return source.stream().map(this::toProductOrderResponse).collect(toList());
    }
}
