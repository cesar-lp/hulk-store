package com.herostore.products.domain;

import com.herostore.products.exception.InvalidEntityStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductTest {

    @Test
    void shouldCreateProductSuccessfully() {
        var product = Product.builder()
                .id(1L)
                .name("IronMan Cup")
                .productType(mockCupsProductType())
                .price(BigDecimal.valueOf(25.50))
                .stock(15)
                .build();

        assertEquals(1L, product.getId());
        assertEquals("IronMan Cup", product.getName());
        assertEquals(BigDecimal.valueOf(25.50), product.getPrice());
        assertEquals(15, product.getStock());
        assertThat(product.getProductType(), samePropertyValuesAs(mockCupsProductType()));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithInvalidName() {
        var expectedMessage = "Name cannot be empty";

        var ex = assertThrows(InvalidEntityStateException.class, () -> Product.builder()
                .name(null)
                .productType(mockCupsProductType())
                .price(BigDecimal.valueOf(25))
                .stock(5)
                .build());
        assertEquals(expectedMessage, ex.getFieldErrorMessage("name"));

        ex = assertThrows(InvalidEntityStateException.class, () -> Product.builder()
                .name("   ")
                .productType(mockCupsProductType())
                .price(BigDecimal.valueOf(25))
                .stock(5)
                .build());
        assertEquals(expectedMessage, ex.getFieldErrorMessage("name"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithInvalidType() {
        var expectedMessage = "Product type cannot be null";

        var ex = assertThrows(InvalidEntityStateException.class, () -> Product.builder()
                .name("Iron Man Cup")
                .productType(null)
                .price(BigDecimal.valueOf(25))
                .stock(5)
                .build());
        assertEquals(expectedMessage, ex.getFieldErrorMessage("productType"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithInvalidPrice() {
        var expectedMessage = "Price cannot be null";

        var ex = assertThrows(InvalidEntityStateException.class, () -> Product.builder()
                .name("Iron Man Cup")
                .productType(mockCupsProductType())
                .price(null)
                .stock(5)
                .build());
        assertEquals(expectedMessage, ex.getFieldErrorMessage("price"));

        expectedMessage = "Price cannot be negative";

        ex = assertThrows(InvalidEntityStateException.class, () -> Product.builder()
                .name("Iron Man Cup")
                .productType(mockCupsProductType())
                .price(BigDecimal.valueOf(-1))
                .stock(5)
                .build());
        assertEquals(expectedMessage, ex.getFieldErrorMessage("price"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithInvalidStock() {
        var expectedMessage = "Stock cannot be null";

        var ex = assertThrows(InvalidEntityStateException.class, () -> Product.builder()
                .name("Iron Man Cup")
                .productType(mockCupsProductType())
                .price(BigDecimal.valueOf(25))
                .stock(null)
                .build());
        assertEquals(expectedMessage, ex.getFieldErrorMessage("stock"));

        expectedMessage = "Stock cannot be negative";

        ex = assertThrows(InvalidEntityStateException.class, () -> Product.builder()
                .name("Iron Man Cup")
                .productType(mockCupsProductType())
                .price(BigDecimal.valueOf(25))
                .stock(-1)
                .build());
        assertEquals(expectedMessage, ex.getFieldErrorMessage("stock"));
    }

    @Test
    void shouldThrowExceptionWhenRequestedStockQuantityIsInvalid() {
        var expectedMessage = "Amount to remove from stock cannot be null nor negative.";
        var p = Product.builder()
                .name("Iron Man Cup")
                .productType(mockCupsProductType())
                .price(BigDecimal.valueOf(25))
                .stock(10)
                .build();

        var ex = assertThrows(IllegalArgumentException.class, () -> p.removeFromStock(null));
        assertEquals(expectedMessage, ex.getMessage());

        ex = assertThrows(IllegalArgumentException.class, () -> p.removeFromStock(-5));
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRequestedQuantityIsGreaterThanAvailableStock() {
        var expectedMessage = "Amount to remove from stock is greater than available stock.";
        var p = Product.builder()
                .name("Iron Man Cup")
                .productType(mockCupsProductType())
                .price(BigDecimal.valueOf(25))
                .stock(10)
                .build();

        var ex = assertThrows(IllegalArgumentException.class, () -> p.removeFromStock(20));
        assertEquals(expectedMessage, ex.getMessage());
    }

    private ProductType mockCupsProductType() {
        return ProductType.builder()
                .id(1L)
                .name("Cups")
                .build();
    }
}
