package com.todo.hulkstore.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaymentOrderTest {

    @Test
    void shouldThrowExceptionWhenCreatingPaymentOrderWithInvalidTotal() {
        var expectedError = "Payment order total cannot be null nor negative.";

        var ex = assertThrows(IllegalArgumentException.class, () -> PaymentOrder.builder().total(null).build());
        assertEquals(expectedError, ex.getMessage());

        ex = assertThrows(IllegalArgumentException.class, () ->
                PaymentOrder.builder().total(BigDecimal.valueOf(-25.00)).build());
        assertEquals(expectedError, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaymentOrderWithInvalidQuantity() {
        var expectedError = "Payment order quantity cannot be null nor negative.";

        var ex = assertThrows(IllegalArgumentException.class, () -> PaymentOrder.builder().quantity(null).build());
        assertEquals(expectedError, ex.getMessage());

        ex = assertThrows(IllegalArgumentException.class, () -> PaymentOrder.builder().quantity(-25).build());
        assertEquals(expectedError, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaymentOrderWithInvalidUnitPrice() {
        var expectedError = "Payment order unit price cannot be null nor negative.";

        var ex = assertThrows(IllegalArgumentException.class, () -> PaymentOrder.builder().unitPrice(null).build());
        assertEquals(expectedError, ex.getMessage());

        ex = assertThrows(IllegalArgumentException.class,
                () -> PaymentOrder.builder().unitPrice(BigDecimal.valueOf(-25.50)).build());
        assertEquals(expectedError, ex.getMessage());
    }
}
