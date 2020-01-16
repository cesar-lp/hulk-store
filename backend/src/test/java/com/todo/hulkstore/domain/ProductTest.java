package com.todo.hulkstore.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductTest {

    @Test
    void shouldThrowExceptionWhenCreatingProductWithInvalidName() {
        var expectedMessage = "Name cannot be null nor empty.";

        var ex = assertThrows(IllegalArgumentException.class, () -> Product.builder().name(null).build());
        assertEquals(expectedMessage, ex.getMessage());

        ex = assertThrows(IllegalArgumentException.class, () -> Product.builder().name("  ").build());
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithInvalidType() {
        var expectedMessage = "Product type cannot be null.";

        var ex = assertThrows(IllegalArgumentException.class, () -> Product.builder().productType(null).build());
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithInvalidPrice() {
        var expectedMessage = "Price cannot be null nor negative.";

        var ex = assertThrows(IllegalArgumentException.class,
                () -> Product.builder().price(BigDecimal.valueOf(-25.00)).build());
        assertEquals(expectedMessage, ex.getMessage());

        ex = assertThrows(IllegalArgumentException.class, () -> Product.builder().price(null).build());
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithInvalidStock() {
        var expectedMessage = "Stock cannot be null nor negative.";

        var ex = assertThrows(IllegalArgumentException.class, () -> Product.builder().stock(null).build());
        assertEquals(expectedMessage, ex.getMessage());

        ex = assertThrows(IllegalArgumentException.class, () -> Product.builder().stock(-25).build());
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRequestedStockQuantityIsInvalid() {
        var expectedMessage = "Amount to remove from stock cannot be null nor negative.";
        var p = Product.builder().stock(0).build();

        var ex = assertThrows(IllegalArgumentException.class, () -> p.removeFromStock(null));
        assertEquals(expectedMessage, ex.getMessage());

        ex = assertThrows(IllegalArgumentException.class, () -> p.removeFromStock(-5));
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRequestedQuantityIsGreaterThanAvailableStock() {
        var expectedMessage = "Amount to remove from stock is greater than available stock.";
        var p = Product.builder().stock(1).build();

        var ex = assertThrows(IllegalArgumentException.class, () -> p.removeFromStock(2));
        assertEquals(expectedMessage, ex.getMessage());
    }
}
