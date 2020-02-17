package com.todo.hulkstore.domain.embedded;

import com.todo.hulkstore.exception.InvalidEntityStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductDetailTest {

    @Test
    void shouldThrowExceptionWhenCreatingProductDetailWithInvalidId() {
        var expectedMessage = "Product id cannot be null";

        var ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductDetail.builder()
                        .id(null)
                        .name("Iron Man Cup")
                        .price(BigDecimal.ZERO)
                        .build());

        assertEquals(expectedMessage, ex.getFieldErrorMessage("id"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductDetailWithInvalidName() {
        var expectedMessage = "Product name cannot be empty";

        var ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductDetail.builder()
                        .id(1L)
                        .name(null)
                        .price(BigDecimal.ZERO)
                        .build());

        assertEquals(expectedMessage, ex.getFieldErrorMessage("name"));

        ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductDetail.builder()
                        .id(1L)
                        .name("   ")
                        .price(BigDecimal.ZERO)
                        .build());

        assertEquals(expectedMessage, ex.getFieldErrorMessage("name"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductDetailWithInvalidPrice() {
        var expectedMessage = "Product price cannot be null";

        var ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductDetail.builder()
                        .id(1L)
                        .name("Iron Man Cup")
                        .price(null)
                        .build());

        assertEquals(expectedMessage, ex.getFieldErrorMessage("price"));

        expectedMessage = "Product price cannot be negative";

        ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductDetail.builder()
                        .id(1L)
                        .name("Iron Man Cup")
                        .price(BigDecimal.valueOf(-25))
                        .build());

        assertEquals(expectedMessage, ex.getFieldErrorMessage("price"));
    }
}
