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

public class ProductOrderTest {

    @Test
    void shouldCreateProductOrderSuccessfully() {
        var id = 1L;
        var createdAt = LocalDateTime.now();
        var productOrders = mockProductOrders();
        var total = NumberUtils.roundToTwoDecimalPlaces(BigDecimal.valueOf(30.50));

        var productOrder = ProductOrder.builder()
                .id(id)
                .productOrderLines(productOrders)
                .createdAt(createdAt)
                .total(total)
                .build();

        assertEquals(id, productOrder.getId());
        assertThat(productOrder.getProductOrderLines().get(0), samePropertyValuesAs(productOrders.get(0)));
        assertEquals(createdAt, productOrder.getCreatedAt());
        assertEquals(total, productOrder.getTotal());
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductOrderWithoutProductOrderLine() {
        var expectedMessage = "Must contain at least one product order line";

        var ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrder.builder()
                        .productOrderLines(emptyList())
                        .total(BigDecimal.ZERO)
                        .build());

        assertEquals(expectedMessage, ex.getFieldErrorMessage("productOrderLines"));

        ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrder.builder()
                        .productOrderLines(null)
                        .total(BigDecimal.ZERO)
                        .build());

        assertEquals(expectedMessage, ex.getFieldErrorMessage("productOrderLines"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductOrderWithInvalidTotal() {
        var expectedMessage = "Total cannot be null";

        List<ProductOrderLine> productOrderLines = mockProductOrders();

        var ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrder.builder()
                        .productOrderLines(productOrderLines)
                        .total(null)
                        .build());

        assertEquals(expectedMessage, ex.getFieldErrorMessage("total"));

        expectedMessage = "Total cannot be negative";

        ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrder.builder()
                        .productOrderLines(productOrderLines)
                        .total(BigDecimal.valueOf(-5))
                        .build());

        assertEquals(expectedMessage, ex.getFieldErrorMessage("total"));

        expectedMessage = "Total and order lines' total do not match";

        ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrder.builder()
                        .productOrderLines(productOrderLines)
                        .total(BigDecimal.valueOf(25.00))
                        .build());

        assertEquals(expectedMessage, ex.getFieldErrorMessage("total"));
    }

    private List<ProductOrderLine> mockProductOrders() {
        var productDetails = ProductDetail.builder()
                .id(1L)
                .name("Iron Man Cup")
                .price(BigDecimal.valueOf(15.25))
                .build();

        return singletonList(ProductOrderLine.builder()
                .productDetail(productDetails)
                .quantity(2)
                .total(BigDecimal.valueOf(30.50))
                .build());
    }
}
