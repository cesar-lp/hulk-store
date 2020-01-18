package com.todo.hulkstore.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InventoryTest {

    @Test
    void shouldThrowExceptionWhenCreatingWithInvalidStock() {
        var expectedError = "Inventory stock cannot be null nor negative.";

        var ex = assertThrows(IllegalArgumentException.class, () ->
                new Inventory(1L, 1L, null));
        assertEquals(expectedError, ex.getMessage());

        ex = assertThrows(IllegalArgumentException.class, () ->
                new Inventory(1L, 1L, -20));
        assertEquals(expectedError, ex.getMessage());
    }
}
