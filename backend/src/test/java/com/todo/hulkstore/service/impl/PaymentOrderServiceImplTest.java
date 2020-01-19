package com.todo.hulkstore.service.impl;

import com.todo.hulkstore.domain.Inventory;
import com.todo.hulkstore.domain.PaymentOrder;
import com.todo.hulkstore.domain.Product;
import com.todo.hulkstore.domain.ProductType;
import com.todo.hulkstore.dto.PaymentOrderRequestDTO;
import com.todo.hulkstore.dto.ProductOrderRequestDTO;
import com.todo.hulkstore.exception.InvalidProductOrderException;
import com.todo.hulkstore.exception.ResourceNotFoundException;
import com.todo.hulkstore.exception.error.InvalidProductOrderError;
import com.todo.hulkstore.repository.InventoryRepository;
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
    InventoryRepository inventoryRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    PaymentOrderServiceImpl paymentOrderService;

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(paymentOrderRepository, inventoryRepository, productRepository);
    }

    @Test
    void shouldCreatePaymentOrderSuccessfully() {
        var paymentOrderRequest = mockNewPaymentOrderRequestDTO();
        var orderProductIds = asList(1L, 2L, 3L);
        var existingProducts = mockExistingProducts();
        var existingInventories = mockExistingInventories();
        var updatedInventories = mockUpdatedInventories();
        var newPaymentOrders = mockNewPaymentOrders(existingProducts);
        var createdPaymentOrders = mockCreatedPaymentOrders(existingProducts);

        when(productRepository.findByIdIn(orderProductIds)).thenReturn(existingProducts);
        when(inventoryRepository.findByProductIdIn(orderProductIds)).thenReturn(existingInventories);
        when(inventoryRepository.saveAll(existingInventories)).thenReturn(updatedInventories);
        //when(paymentOrderRepository.saveAll(newPaymentOrders)).thenReturn(createdPaymentOrders);

        paymentOrderService.registerOrder(paymentOrderRequest);

        verify(productRepository, times(1)).findByIdIn(orderProductIds);
        verify(inventoryRepository, times(1)).findByProductIdIn(orderProductIds);
        verify(inventoryRepository, times(1)).saveAll(existingInventories);
        // TODO: fix stubbing error
        verify(paymentOrderRepository, times(1)).saveAll(any());
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaymentOrderForNonExistingProduct() {
        var paymentOrderRequest = new PaymentOrderRequestDTO(
                singletonList(new ProductOrderRequestDTO(5L, 10)));
        var orderProductIds = singletonList(5L);
        var expectedError = "Couldn't register payment order: product not found for id 5";

        when(productRepository.findByIdIn(orderProductIds)).thenReturn(mockExistingProducts());
        when(inventoryRepository.findByProductIdIn(orderProductIds)).thenReturn(mockExistingInventories());

        var exc = assertThrows(ResourceNotFoundException.class,
                () -> paymentOrderService.registerOrder(paymentOrderRequest));

        assertEquals(expectedError, exc.getMessage());
        verify(productRepository, times(1)).findByIdIn(orderProductIds);
        verify(inventoryRepository, times(1)).findByProductIdIn(orderProductIds);
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaymentOrderForProductWithoutInventory() {
        var orderProductIds = asList(1L, 2L, 3L);
        var existingInventories = asList(
                new Inventory(1L, 1L, 50),
                new Inventory(2L, 3L, 50));
        var expectedError = "Couldn't register payment order: inventory not found for product with id 2";

        when(productRepository.findByIdIn(orderProductIds)).thenReturn(mockExistingProducts());
        when(inventoryRepository.findByProductIdIn(orderProductIds)).thenReturn(existingInventories);

        var exc = assertThrows(ResourceNotFoundException.class,
                () -> paymentOrderService.registerOrder(mockNewPaymentOrderRequestDTO()));

        assertEquals(expectedError, exc.getMessage());
        verify(productRepository, times(1)).findByIdIn(orderProductIds);
        verify(inventoryRepository, times(1)).findByProductIdIn(orderProductIds);
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaymentOrderForInvalidQuantities() {
        var orderProductIds = asList(1L, 2L, 3L);

        when(productRepository.findByIdIn(orderProductIds)).thenReturn(mockExistingProducts());
        when(inventoryRepository.findByProductIdIn(orderProductIds)).thenReturn(mockInventoriesWithLowStock());

        var exc = assertThrows(InvalidProductOrderException.class,
                () -> paymentOrderService.registerOrder(mockNewPaymentOrderRequestDTO()));

        var expectedErrors = mockInvalidProductOrderErrors();

        assertThat(expectedErrors.get(0), samePropertyValuesAs(exc.getInvalidProductOrderErrors().get(0)));
        assertThat(expectedErrors.get(1), samePropertyValuesAs(exc.getInvalidProductOrderErrors().get(1)));
        verify(productRepository, times(1)).findByIdIn(orderProductIds);
        verify(inventoryRepository, times(1)).findByProductIdIn(orderProductIds);
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
                .price(BigDecimal.valueOf(30.00))
                .build();

        var p2 = Product.builder()
                .id(2L)
                .name("Batman Cup")
                .productType(type)
                .price(BigDecimal.valueOf(27.00))
                .build();

        var p3 = Product.builder()
                .id(3L)
                .name("Hulk Cup")
                .productType(type)
                .price(BigDecimal.valueOf(25.00))
                .build();

        return asList(p1, p2, p3);
    }

    private List<Inventory> mockExistingInventories() {
        var inv1 = new Inventory(1L, 1L, 50);
        var inv2 = new Inventory(2L, 2L, 50);
        var inv3 = new Inventory(3L, 3L, 50);

        return asList(inv1, inv2, inv3);
    }

    private List<Inventory> mockInventoriesWithLowStock() {
        var inv1 = new Inventory(1L, 1L, 5);
        var inv2 = new Inventory(2L, 2L, 50);
        var inv3 = new Inventory(3L, 3L, 5);

        return asList(inv1, inv2, inv3);
    }

    private List<Inventory> mockUpdatedInventories() {
        var inv1 = new Inventory(1L, 1L, 40);
        var inv2 = new Inventory(2L, 2L, 40);
        var inv3 = new Inventory(3L, 3L, 40);

        return asList(inv1, inv2, inv3);
    }

    private List<PaymentOrder> mockNewPaymentOrders(List<Product> products) {
        return products.stream()
                .map(this::toNewPaymentOrder)
                .collect(toList());
    }

    private List<PaymentOrder> mockCreatedPaymentOrders(List<Product> products) {
        var createdAt = LocalDateTime.now();
        return products.stream()
                .map(p -> toCreatedPaymentOrder(p, createdAt))
                .collect(toList());
    }

    private PaymentOrder toNewPaymentOrder(Product p) {
        var total = BigDecimal.valueOf(10).multiply(p.getPrice());
        return PaymentOrder.builder()
                .productId(p.getId())
                .unitPrice(p.getPrice())
                .quantity(10)
                .total(total)
                .build();
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
