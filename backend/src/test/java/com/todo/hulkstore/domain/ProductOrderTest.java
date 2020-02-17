package com.todo.hulkstore.domain;

import com.todo.hulkstore.domain.embedded.ProductDetail;
import com.todo.hulkstore.exception.InvalidEntityStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.todo.hulkstore.utils.NumberUtils.roundToTwoDecimalPlaces;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductOrderTest {

    @Test
    void shouldCreateProductOrderSuccessfully() {
        var productDetail = mockProductDetail();

        var productOrder = ProductOrder.builder()
                .id(1L)
                .productDetail(productDetail)
                .quantity(2)
                .total(BigDecimal.valueOf(51.00))
                .build();

        var expectedId = 1L;
        var expectedQuantity = 2;
        var expectedTotal = roundToTwoDecimalPlaces(BigDecimal.valueOf(51.00));

        assertEquals(expectedId, productOrder.getId());
        assertEquals(expectedQuantity, productOrder.getQuantity());
        assertEquals(expectedTotal, productOrder.getTotal());
        assertThat(productOrder.getProductDetail(), samePropertyValuesAs(productDetail));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductOrderWithInvalidQuantity() {
        var productDetail = mockProductDetail();

        var expectedError = "Quantity cannot be null";
        var ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrder.builder()
                        .productDetail(productDetail)
                        .quantity(null)
                        .total(BigDecimal.ZERO)
                        .build());

        assertEquals(expectedError, ex.getFieldErrorMessage("quantity"));

        expectedError = "Quantity cannot be negative";
        ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrder.builder()
                        .productDetail(productDetail)
                        .quantity(-50)
                        .total(BigDecimal.ZERO)
                        .build());

        assertEquals(expectedError, ex.getFieldErrorMessage("quantity"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaymentOrderWithInvalidTotal() {
        var productDetail = mockProductDetail();

        var expectedError = "Total cannot be null";
        var ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrder.builder()
                        .productDetail(productDetail)
                        .quantity(0)
                        .total(null)
                        .build());

        assertEquals(expectedError, ex.getFieldErrorMessage("total"));

        expectedError = "Total cannot be negative";
        ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrder.builder()
                        .productDetail(productDetail)
                        .quantity(0)
                        .total(BigDecimal.valueOf(-75))
                        .build());

        assertEquals(expectedError, ex.getFieldErrorMessage("total"));

        expectedError = "Supplied total and calculated total do not match";
        ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrder.builder()
                        .productDetail(productDetail)
                        .quantity(2)
                        .total(BigDecimal.valueOf(50.00))
                        .build());

        assertEquals(expectedError, ex.getFieldErrorMessage("total"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductOrderWithoutProductDetail() {
        var expectedError = "Product detail cannot be null";
        var ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrder.builder()
                        .productDetail(null)
                        .quantity(0)
                        .total(BigDecimal.ZERO)
                        .build());

        assertEquals(expectedError, ex.getFieldErrorMessage("productDetail"));
    }

    private ProductDetail mockProductDetail() {
        return ProductDetail.builder()
                .id(1L)
                .name("Iron Man Cup")
                .price(BigDecimal.valueOf(25.50))
                .build();
    }
}
