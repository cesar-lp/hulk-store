package com.todo.hulkstore.mapper;

import com.todo.hulkstore.domain.PaymentOrder;
import com.todo.hulkstore.domain.ProductOrder;
import com.todo.hulkstore.domain.embedded.ProductDetail;
import com.todo.hulkstore.dto.ProductOrderDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentOrderMapperTest {

    PaymentOrderMapper mapper = Mappers.getMapper(PaymentOrderMapper.class);

    @Test
    void shouldMapPaymentOrderToPaymentOrderResponse() {
        var paymentOrder = PaymentOrder.builder()
                .id(1L)
                .productOrders(asList(mockIronManCupOrder(), mockBatmanCupOrder()))
                .createdAt(LocalDateTime.now())
                .total(BigDecimal.valueOf(450.00))
                .build();

        var paymentOrderResponse = mapper.toPaymentOrderResponse(paymentOrder);

        assertEquals(paymentOrder.getId(), paymentOrderResponse.getId());
        assertEquals(paymentOrder.getCreatedAt(), paymentOrderResponse.getCreatedAt());
        assertEquals(paymentOrder.getTotal(), paymentOrderResponse.getTotal());
        assertProductOrders(paymentOrder.getProductOrders().get(0), paymentOrderResponse.getProductOrders().get(0));
        assertProductOrders(paymentOrder.getProductOrders().get(1), paymentOrderResponse.getProductOrders().get(1));
    }

    @Test
    void shouldMapProductOrderListToProductOrderDTOList() {
        var ironManCupOrder = mockIronManCupOrder();
        var batmanCupOrder = mockBatmanCupOrder();

        var productOrderList = asList(ironManCupOrder, batmanCupOrder);
        var productOrderDTOList = mapper.toProductOrderDTOList(productOrderList);

        assertEquals(2, productOrderDTOList.size());
        assertProductOrders(ironManCupOrder, productOrderDTOList.get(0));
        assertProductOrders(batmanCupOrder, productOrderDTOList.get(1));
    }

    @Test
    void shouldMapProductOrderToProductOrderDTO() {
        var productOrder = mockIronManCupOrder();
        var productOrderDTO = mapper.toProductOrderDTO(productOrder);
        assertProductOrders(productOrder, productOrderDTO);
    }

    @Test
    void shouldMapPaymentOrderListToPaymentOrderResponseList() {
        var list = mapper.toPaymentOrderResponseList(emptyList());
        assertTrue(list.isEmpty());

        list = mapper.toPaymentOrderResponseList(null);
        assertTrue(list.isEmpty());

        var firstPaymentOrder = PaymentOrder.builder()
                .id(1L)
                .productOrders(singletonList(mockIronManCupOrder()))
                .createdAt(LocalDateTime.now())
                .total(BigDecimal.valueOf(225.00))
                .build();

        var secondPaymentOrder = PaymentOrder.builder()
                .id(2L)
                .productOrders(singletonList(mockBatmanCupOrder()))
                .createdAt(LocalDateTime.now())
                .total(BigDecimal.valueOf(225.00))
                .build();

        var paymentOrderResponse = mapper.toPaymentOrderResponseList(asList(firstPaymentOrder, secondPaymentOrder));

        assertEquals(2, paymentOrderResponse.size());

        var paymentOrder = paymentOrderResponse.get(0);
        {
            assertEquals(firstPaymentOrder.getId(), paymentOrder.getId());
            assertEquals(firstPaymentOrder.getCreatedAt(), paymentOrder.getCreatedAt());
            assertProductOrders(firstPaymentOrder.getProductOrders().get(0), paymentOrder.getProductOrders().get(0));
            assertEquals(firstPaymentOrder.getTotal(), paymentOrder.getTotal());
        }

        paymentOrder = paymentOrderResponse.get(1);
        {
            assertEquals(secondPaymentOrder.getId(), paymentOrder.getId());
            assertEquals(secondPaymentOrder.getCreatedAt(), paymentOrder.getCreatedAt());
            assertProductOrders(secondPaymentOrder.getProductOrders().get(0), paymentOrder.getProductOrders().get(0));
            assertEquals(secondPaymentOrder.getTotal(), paymentOrder.getTotal());
        }
    }

    private void assertProductOrders(ProductOrder source, ProductOrderDTO target) {
        assertEquals(source.getId(), target.getId());
        assertEquals(source.getProductDetail().getId(), target.getProductId());
        assertEquals(source.getProductDetail().getName(), target.getProductName());
        assertEquals(source.getProductDetail().getPrice(), target.getProductPrice());
        assertEquals(source.getQuantity(), target.getQuantity());
        assertEquals(source.getTotal(), target.getTotal());
    }

    private ProductOrder mockIronManCupOrder() {
        var productDetail = ProductDetail.builder()
                .id(1L)
                .name("Iron Man Cup")
                .price(BigDecimal.valueOf(75.00))
                .build();

        return ProductOrder.builder()
                .id(1L)
                .productDetail(productDetail)
                .quantity(3)
                .total(productDetail.getPrice().multiply(BigDecimal.valueOf(3)))
                .build();
    }

    private ProductOrder mockBatmanCupOrder() {
        var batmanCupDetail = ProductDetail.builder()
                .id(2L)
                .name("Batman Cup")
                .price(BigDecimal.valueOf(45.00))
                .build();

        return ProductOrder.builder()
                .id(2L)
                .productDetail(batmanCupDetail)
                .quantity(5)
                .total(batmanCupDetail.getPrice().multiply(BigDecimal.valueOf(5)))
                .build();
    }
}
