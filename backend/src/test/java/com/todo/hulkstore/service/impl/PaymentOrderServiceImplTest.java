package com.todo.hulkstore.service.impl;

import com.todo.hulkstore.converter.PaymentOrderConverter;
import com.todo.hulkstore.domain.PaymentOrder;
import com.todo.hulkstore.domain.Product;
import com.todo.hulkstore.domain.ProductType;
import com.todo.hulkstore.dto.PaymentOrderDTO;
import com.todo.hulkstore.dto.request.PaymentOrderRequestDTO;
import com.todo.hulkstore.dto.request.ProductOrderRequestDTO;
import com.todo.hulkstore.exception.InvalidProductOrderException;
import com.todo.hulkstore.exception.ResourceNotFoundException;
import com.todo.hulkstore.exception.error.InvalidProductOrderError;
import com.todo.hulkstore.repository.PaymentOrderRepository;
import com.todo.hulkstore.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentOrderServiceImplTest {

    @Mock
    PaymentOrderRepository paymentOrderRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    PaymentOrderConverter converter;

    @InjectMocks
    PaymentOrderServiceImpl paymentOrderService;

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(paymentOrderRepository, productRepository);
    }

    @Test
    void shouldCreatePaymentOrderSuccessfully() {
        var paymentOrderRequest = mockNewPaymentOrderRequestDTO();
        var orderProductIds = asList(1L, 2L, 3L);
        var existingProducts = mockExistingProducts();
        var createdPaymentOrders = mockCreatedPaymentOrders(existingProducts);
        var expectedPaymentOrdersCreated = mockPaymentOrdersDTOList();

        when(productRepository.findByIdIn(orderProductIds))
                .thenReturn(existingProducts);

        when(productRepository.saveAll(any()))
                .thenReturn(existingProducts);

        when(paymentOrderRepository.saveAll(any()))
                .thenReturn(createdPaymentOrders);

        when(converter.toPaymentOrderDTOList(any(), any()))
                .thenReturn(expectedPaymentOrdersCreated);

        var actualPaymentOrdersCreated = paymentOrderService.registerOrder(paymentOrderRequest);

        assertThat(expectedPaymentOrdersCreated, samePropertyValuesAs(actualPaymentOrdersCreated));

        verify(productRepository, times(1)).findByIdIn(orderProductIds);
        verify(productRepository, times(1)).saveAll(existingProducts);
        verify(paymentOrderRepository, times(1)).saveAll(any());
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaymentOrderForNonExistingProduct() {
        var paymentOrderRequest = new PaymentOrderRequestDTO(
                singletonList(new ProductOrderRequestDTO(5L, 10)));
        var orderProductIds = singletonList(5L);
        var expectedError = "Couldn't register payment order: product not found for id 5";

        when(productRepository.findByIdIn(orderProductIds)).thenReturn(mockExistingProducts());

        var exc = assertThrows(ResourceNotFoundException.class,
                () -> paymentOrderService.registerOrder(paymentOrderRequest));

        assertEquals(expectedError, exc.getMessage());
        verify(productRepository, times(1)).findByIdIn(orderProductIds);
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaymentOrderForInvalidQuantities() {
        var orderProductIds = asList(1L, 2L, 3L);

        when(productRepository.findByIdIn(orderProductIds))
                .thenReturn(mockExistingProductsWitLowStock());

        var exc = assertThrows(InvalidProductOrderException.class,
                () -> paymentOrderService.registerOrder(mockNewPaymentOrderRequestDTO()));

        var expectedErrors = mockInvalidProductOrderErrors();

        assertThat(expectedErrors.get(0), samePropertyValuesAs(exc.getInvalidProductOrderErrors().get(0)));
        assertThat(expectedErrors.get(1), samePropertyValuesAs(exc.getInvalidProductOrderErrors().get(1)));
        verify(productRepository, times(1)).findByIdIn(orderProductIds);
    }

    private List<InvalidProductOrderError> mockInvalidProductOrderErrors() {
        var invalidOrder1 = InvalidProductOrderError.builder()
                .id(1L)
                .name("Iron Man Cup")
                .requestedQuantity(10)
                .stock(5)
                .build();

        var invalidOrder2 = InvalidProductOrderError.builder()
                .id(3L)
                .name("Hulk Cup")
                .requestedQuantity(10)
                .stock(5)
                .build();

        return asList(invalidOrder1, invalidOrder2);
    }

    private PaymentOrderRequestDTO mockNewPaymentOrderRequestDTO() {
        var order1 = new ProductOrderRequestDTO(1L, 10);
        var order2 = new ProductOrderRequestDTO(2L, 10);
        var order3 = new ProductOrderRequestDTO(3L, 10);

        return new PaymentOrderRequestDTO(asList(order1, order2, order3));
    }

    private List<Product> mockExistingProducts() {
        var type = new ProductType(1L, "Cups");

        var p1 = Product.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(type)
                .stock(20)
                .price(BigDecimal.valueOf(30.00))
                .build();

        var p2 = Product.builder()
                .id(2L)
                .name("Batman Cup")
                .productType(type)
                .stock(20)
                .price(BigDecimal.valueOf(27.00))
                .build();

        var p3 = Product.builder()
                .id(3L)
                .name("Hulk Cup")
                .productType(type)
                .stock(20)
                .price(BigDecimal.valueOf(25.00))
                .build();

        return asList(p1, p2, p3);
    }

    private List<Product> mockExistingProductsWitLowStock() {
        var type = new ProductType(1L, "Cups");

        var p1 = Product.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(type)
                .stock(5)
                .price(BigDecimal.valueOf(30.00))
                .build();

        var p2 = Product.builder()
                .id(2L)
                .name("Batman Cup")
                .productType(type)
                .stock(15)
                .price(BigDecimal.valueOf(27.00))
                .build();

        var p3 = Product.builder()
                .id(3L)
                .name("Hulk Cup")
                .productType(type)
                .stock(5)
                .price(BigDecimal.valueOf(25.00))
                .build();

        return asList(p1, p2, p3);
    }

    private List<PaymentOrder> mockCreatedPaymentOrders(List<Product> products) {
        var createdAt = LocalDateTime.now();
        return products.stream()
                .map(p -> toCreatedPaymentOrder(p, createdAt))
                .collect(toList());
    }

    private List<PaymentOrderDTO> mockPaymentOrdersDTOList() {
        var ironManCup = PaymentOrderDTO.builder()
                .id(1L)
                .productName("Iron Man Cup")
                .productStockLeft(10)
                .total(BigDecimal.valueOf(300.00))
                .build();

        var batmanCup = PaymentOrderDTO.builder()
                .id(2L)
                .productName("Batman Cup")
                .productStockLeft(10)
                .total(BigDecimal.valueOf(270.00))
                .build();

        var hulkCup = PaymentOrderDTO.builder()
                .id(3L)
                .productName("Hulk Cup")
                .productStockLeft(10)
                .total(BigDecimal.valueOf(250.00))
                .build();

        return asList(ironManCup, batmanCup, hulkCup);
    }

    private PaymentOrder toCreatedPaymentOrder(Product p, LocalDateTime time) {
        var total = BigDecimal.valueOf(10).multiply(p.getPrice());
        return PaymentOrder.builder()
                .id(p.getId())
                .productId(p.getId())
                .unitPrice(p.getPrice())
                .quantity(10)
                .total(total)
                .date(time)
                .build();
    }
}
