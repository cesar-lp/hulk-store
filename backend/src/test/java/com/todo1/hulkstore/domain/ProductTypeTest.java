package com.todo1.hulkstore.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductTypeTest {

    @Test
    void shouldThrowExceptionWhenCreatingProductTypeWithInvalidName() {
        var expectedError = "Product type name cannot be null nor empty.";
        var ex = assertThrows(IllegalArgumentException.class, () -> new ProductType(1L, null));
        assertEquals(expectedError, ex.getMessage());

        ex = assertThrows(IllegalArgumentException.class, () -> new ProductType(1L, "   "));
        assertEquals(expectedError, ex.getMessage());
    }
}
