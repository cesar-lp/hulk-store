package com.todo.hulkstore.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductTest {

    @Test
    void shouldThrowExceptionWhenCreatingProductWithInvalidName() {
        var expectedMessage = "Product name cannot be null nor empty.";
        var ex = assertThrows(IllegalArgumentException.class, () -> Product.builder()
                .name(null)
                .build());
        assertEquals(expectedMessage, ex.getMessage());

        ex = assertThrows(IllegalArgumentException.class, () -> {
            Product.builder()
                    .name("         ")
                    .build();
        });
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithInvalidType() {
        var expectedMessage = "Product type cannot be null.";
        var ex = assertThrows(IllegalArgumentException.class, () -> Product.builder()
                .productType(null)
                .build());
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithInvalidPrice() {
        var expectedMessage = "Product price cannot be null nor negative.";
        var ex = assertThrows(IllegalArgumentException.class, () -> Product.builder()
                .price(BigDecimal.valueOf(-25.00))
                .build());
        assertEquals(expectedMessage, ex.getMessage());

        ex = assertThrows(IllegalArgumentException.class, () -> {
            Product.builder()
                    .price(null)
                    .build();
        });
        assertEquals(expectedMessage, ex.getMessage());
    }
}
