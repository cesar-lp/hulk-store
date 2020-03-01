package com.herostore.products.domain;

import com.herostore.products.domain.embedded.ProductDetail;
import com.herostore.products.exception.InvalidEntityStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.herostore.products.utils.NumberUtils.roundToTwoDecimalPlaces;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductOrderLineTest {

    @Test
    void shouldCreateProductOrderLineSuccessfully() {
        var productDetail = mockProductDetail();

        var productOrderLine = ProductOrderLine.builder()
                .id(1L)
                .productDetail(productDetail)
                .quantity(2)
                .total(BigDecimal.valueOf(51.00))
                .build();

        var expectedId = 1L;
        var expectedQuantity = 2;
        var expectedTotal = roundToTwoDecimalPlaces(BigDecimal.valueOf(51.00));

        assertEquals(expectedId, productOrderLine.getId());
        assertEquals(expectedQuantity, productOrderLine.getQuantity());
        assertEquals(expectedTotal, productOrderLine.getTotal());
        assertThat(productOrderLine.getProductDetail(), samePropertyValuesAs(productDetail));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductOrderLineWithInvalidQuantity() {
        var productDetail = mockProductDetail();

        var expectedError = "Quantity cannot be null";
        var ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrderLine.builder()
                        .productDetail(productDetail)
                        .quantity(null)
                        .total(BigDecimal.ZERO)
                        .build());

        assertEquals(expectedError, ex.getFieldErrorMessage("quantity"));

        expectedError = "Quantity cannot be negative";
        ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrderLine.builder()
                        .productDetail(productDetail)
                        .quantity(-50)
                        .total(BigDecimal.ZERO)
                        .build());

        assertEquals(expectedError, ex.getFieldErrorMessage("quantity"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductOrderLineWithInvalidTotal() {
        var productDetail = mockProductDetail();

        var expectedError = "Total cannot be null";
        var ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrderLine.builder()
                        .productDetail(productDetail)
                        .quantity(0)
                        .total(null)
                        .build());

        assertEquals(expectedError, ex.getFieldErrorMessage("total"));

        expectedError = "Total cannot be negative";
        ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrderLine.builder()
                        .productDetail(productDetail)
                        .quantity(0)
                        .total(BigDecimal.valueOf(-75))
                        .build());

        assertEquals(expectedError, ex.getFieldErrorMessage("total"));

        expectedError = "Supplied total and calculated total do not match";
        ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrderLine.builder()
                        .productDetail(productDetail)
                        .quantity(2)
                        .total(BigDecimal.valueOf(50.00))
                        .build());

        assertEquals(expectedError, ex.getFieldErrorMessage("total"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductOrderLineWithoutProductDetail() {
        var expectedError = "Product detail cannot be null";
        var ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductOrderLine.builder()
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
