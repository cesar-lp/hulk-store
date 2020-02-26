package com.herostore.products.domain;

import com.herostore.products.domain.embedded.ProductDetail;
import com.herostore.products.exception.InvalidEntityStateException;
import com.herostore.products.utils.NumberUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaymentOrderTest {

    @Test
    void shouldCreatePaymentOrderSuccessfully() {
        var id = 1L;
        var createdAt = LocalDateTime.now();
        var productOrders = mockProductOrders();
        var total = NumberUtils.roundToTwoDecimalPlaces(BigDecimal.valueOf(30.50));

        var paymentOrder = PaymentOrder.builder()
                .id(id)
                .productOrders(productOrders)
                .createdAt(createdAt)
                .total(total)
                .build();

        assertEquals(id, paymentOrder.getId());
        assertThat(paymentOrder.getProductOrders().get(0), samePropertyValuesAs(productOrders.get(0)));
        assertEquals(createdAt, paymentOrder.getCreatedAt());
        assertEquals(total, paymentOrder.getTotal());
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaymentOrderWithoutProductOrder() {
        var expectedMessage = "Must contain at least one product order";

        var ex = assertThrows(InvalidEntityStateException.class,
                () -> PaymentOrder.builder()
                        .productOrders(emptyList())
                        .total(BigDecimal.ZERO)
                        .build());

        assertEquals(expectedMessage, ex.getFieldErrorMessage("productOrders"));

        ex = assertThrows(InvalidEntityStateException.class,
                () -> PaymentOrder.builder()
                        .productOrders(null)
                        .total(BigDecimal.ZERO)
                        .build());

        assertEquals(expectedMessage, ex.getFieldErrorMessage("productOrders"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaymentOrderWithInvalidTotal() {
        var expectedMessage = "Total cannot be null";

        List<ProductOrder> productOrders = mockProductOrders();

        var ex = assertThrows(InvalidEntityStateException.class,
                () -> PaymentOrder.builder()
                        .productOrders(productOrders)
                        .total(null)
                        .build());

        assertEquals(expectedMessage, ex.getFieldErrorMessage("total"));

        expectedMessage = "Total cannot be negative";

        ex = assertThrows(InvalidEntityStateException.class,
                () -> PaymentOrder.builder()
                        .productOrders(productOrders)
                        .total(BigDecimal.valueOf(-5))
                        .build());

        assertEquals(expectedMessage, ex.getFieldErrorMessage("total"));

        expectedMessage = "Payment order total and sum of product orders' total are not equal";

        ex = assertThrows(InvalidEntityStateException.class,
                () -> PaymentOrder.builder()
                        .productOrders(productOrders)
                        .total(BigDecimal.valueOf(25.00))
                        .build());

        assertEquals(expectedMessage, ex.getFieldErrorMessage("total"));
    }

    private List<ProductOrder> mockProductOrders() {
        var productDetails = ProductDetail.builder()
                .id(1L)
                .name("Iron Man Cup")
                .price(BigDecimal.valueOf(15.25))
                .build();

        return singletonList(ProductOrder.builder()
                .productDetail(productDetails)
                .quantity(2)
                .total(BigDecimal.valueOf(30.50))
                .build());
    }
}
