package com.todo.hulkstore.converter;

import com.todo.hulkstore.domain.PaymentOrder;
import com.todo.hulkstore.domain.ProductOrder;
import com.todo.hulkstore.dto.ProductOrderDTO;
import com.todo.hulkstore.dto.response.PaymentOrderResponseDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class PaymentOrderConverter {

    public PaymentOrderResponseDTO toPaymentOrderResponseDTO(PaymentOrder source) {
        if (source == null) {
            throw new IllegalArgumentException("PaymentOrder cannot be null.");
        }

        return PaymentOrderResponseDTO.builder()
                .id(source.getId())
                .dateTime(source.getDate())
                .productOrders(toProductOrderDTOList(source.getProductOrders()))
                .total(source.getTotal())
                .build();
    }

    public List<ProductOrderDTO> toProductOrderDTOList(List<ProductOrder> source) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>();
        }

        return source.stream()
                .map(this::toProductOrderDTO)
                .collect(toList());
    }

    public ProductOrderDTO toProductOrderDTO(ProductOrder source) {
        if (source == null) {
            throw new IllegalArgumentException("ProductOrder cannot be null.");
        }

        return ProductOrderDTO.builder()
                .id(source.getId())
                .productName(source.getProductName())
                .productPrice(source.getProductPrice())
                .quantity(source.getQuantity())
                .total(source.getTotal())
                .build();
    }
}
