package com.todo.hulkstore.converter;

import com.todo.hulkstore.domain.PaymentOrder;
import com.todo.hulkstore.domain.Product;
import com.todo.hulkstore.dto.PaymentOrderDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PaymentOrderConverter {

    public PaymentOrderDTO toPaymentOrderDTO(PaymentOrder order, Product p) {
        if (order == null) {
            throw new IllegalArgumentException("PaymentOrder cannot be null.");
        }

        return PaymentOrderDTO.builder()
                .id(order.getId())
                .productId(order.getProductId())
                .productName(p.getName())
                .total(order.getTotal())
                .productStockLeft(p.getStock())
                .build();
    }

    public List<PaymentOrderDTO> toPaymentOrderDTOList(List<PaymentOrder> orders, List<Product> products) {
        var paymentOrderDTOList = new ArrayList<PaymentOrderDTO>();

        for (var order : orders) {
            products.stream()
                    .filter(p -> p.getId().equals(order.getProductId()))
                    .findFirst()
                    .ifPresent(p -> paymentOrderDTOList.add(toPaymentOrderDTO(order, p)));
        }

        return paymentOrderDTOList;
    }
}
