package com.herostore.products.domain;

import com.herostore.products.exception.InvalidEntityStateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductTypeTest {

    @Test
    void shouldCreateProductTypeSuccessfully() {
        var productType = ProductType.builder()
                .id(1L)
                .name("Cups")
                .build();

        assertEquals(1L, productType.getId());
        assertEquals("Cups", productType.getName());
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductTypeWithInvalidName() {
        var expectedError = "Name cannot be empty";

        var ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductType.builder()
                        .id(1L)
                        .build());

        assertEquals(expectedError, ex.getFieldErrorMessage("name"));

        ex = assertThrows(InvalidEntityStateException.class,
                () -> ProductType.builder()
                        .id(1L)
                        .name("   ")
                        .build());

        assertEquals(expectedError, ex.getFieldErrorMessage("name"));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingProductTypeNameToInvalidValue() {
        var expectedError = "Name cannot be empty";
        var productType = ProductType.builder()
                .id(1L)
                .name("Cups")
                .build();

        var ex = assertThrows(InvalidEntityStateException.class, () -> productType.updateName("   "));
        assertEquals(expectedError, ex.getFieldErrorMessage("name"));

        ex = assertThrows(InvalidEntityStateException.class, () -> productType.updateName(null));
        assertEquals(expectedError, ex.getFieldErrorMessage("name"));
    }
}
