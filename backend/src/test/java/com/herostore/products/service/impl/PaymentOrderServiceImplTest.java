package com.herostore.products.service.impl;

import com.herostore.products.domain.ProductOrder;
import com.herostore.products.domain.embedded.ProductDetail;
import com.herostore.products.dto.ProductOrderDTO;
import com.herostore.products.dto.response.PaymentOrderResponse;
import com.herostore.products.domain.PaymentOrder;
import com.herostore.products.domain.Product;
import com.herostore.products.domain.ProductType;
import com.herostore.products.dto.request.OrderLineRequest;
import com.herostore.products.dto.request.PaymentOrderRequest;
import com.herostore.products.exception.InvalidProductOrderException;
import com.herostore.products.exception.ResourceNotFoundException;
import com.herostore.products.exception.error.InvalidProductOrderError;
import com.herostore.products.mapper.PaymentOrderMapper;
import com.herostore.products.repository.PaymentOrderRepository;
import com.herostore.products.repository.ProductRepository;
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
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    ProductRepository productRepository;

    @Mock
    PaymentOrderMapper paymentOrderMapper;

    @InjectMocks
    PaymentOrderServiceImpl paymentOrderService;

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(paymentOrderRepository, productRepository, paymentOrderMapper);
    }

    @Test
    void shouldGetAllPaymentOrdersSuccessfully() {
        var existingPaymentOrders = mockExistingPaymentOrders();
        var expectedPaymentOrders = mockExistingPaymentOrdersResponse(existingPaymentOrders);

        when(paymentOrderRepository.findAll())
                .thenReturn(existingPaymentOrders);

        when(paymentOrderMapper.toPaymentOrderResponseList(existingPaymentOrders))
                .thenReturn(expectedPaymentOrders);

        var actualPaymentOrders = paymentOrderService.getAllPaymentOrders();

        assertThat(actualPaymentOrders, samePropertyValuesAs(expectedPaymentOrders));

        verify(paymentOrderRepository, times(1)).findAll();
        verify(paymentOrderMapper, times(1)).toPaymentOrderResponseList(existingPaymentOrders);
    }

    @Test
    void shouldGetEmptyListWhenRetrievingNonExistingPaymentOrders() {
        List<PaymentOrder> existingPaymentOrderDetails = emptyList();
        List<PaymentOrderResponse> expectedPaymentOrders = emptyList();

        when(paymentOrderRepository.findAll())
                .thenReturn(existingPaymentOrderDetails);

        when(paymentOrderMapper.toPaymentOrderResponseList(existingPaymentOrderDetails))
                .thenReturn(expectedPaymentOrders);

        var actualPaymentOrders = paymentOrderService.getAllPaymentOrders();

        assertTrue(actualPaymentOrders.isEmpty());

        verify(paymentOrderRepository, times(1)).findAll();
        verify(paymentOrderMapper, times(1)).toPaymentOrderResponseList(existingPaymentOrderDetails);
    }

    @Test
    void shouldCreatePaymentOrderSuccessfully() {
        var orderLinesRequests = new ArrayList<>(asList(
                OrderLineRequest.builder().productId(1L).quantity(5).build(),
                OrderLineRequest.builder().productId(2L).quantity(10).build(),
                OrderLineRequest.builder().productId(3L).quantity(5).build()
        ));

        var paymentOrderRequest = PaymentOrderRequest.builder()
                .orderLines(orderLinesRequests)
                .build();

        var orderProductIds = asList(1L, 2L, 3L);
        var existingProducts = mockExistingProducts();

        var createdAt = LocalDateTime.now();
        var createdProductOrders = mockCreatedOrderLines(existingProducts, orderLinesRequests);
        var createdPaymentOrder = mockCreatedPaymentOrder(createdProductOrders, createdAt);

        var expectedPaymentOrderResponse = mockPaymentOrderResponse(createdPaymentOrder);

        when(productRepository.findByIdIn(orderProductIds))
                .thenReturn(existingProducts);

        when(paymentOrderRepository.save(any(PaymentOrder.class)))
                .thenReturn(createdPaymentOrder);

        when(paymentOrderMapper.toPaymentOrderResponse(createdPaymentOrder))
                .thenReturn(expectedPaymentOrderResponse);

        var actualPaymentOrderResponse = paymentOrderService.registerPaymentOrder(paymentOrderRequest);

        assertThat(actualPaymentOrderResponse, samePropertyValuesAs(expectedPaymentOrderResponse));

        verify(productRepository, times(1)).findByIdIn(orderProductIds);
        verify(productRepository, times(1)).saveAll(existingProducts);
        verify(paymentOrderRepository, times(1)).save(any(PaymentOrder.class));
        verify(paymentOrderMapper, times(1)).toPaymentOrderResponse(createdPaymentOrder);
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaymentOrderForNonExistingProduct() {
        var paymentOrderRequest = PaymentOrderRequest.builder()
                .orderLines(singletonList(OrderLineRequest.builder().productId(5L).build()))
                .build();

        var orderProductIds = singletonList(5L);
        var expectedError = "Couldn't register payment order: product not found for id 5";

        when(productRepository.findByIdIn(orderProductIds))
                .thenReturn(emptyList());

        var exc = assertThrows(ResourceNotFoundException.class,
                () -> paymentOrderService.registerPaymentOrder(paymentOrderRequest));

        assertEquals(expectedError, exc.getMessage());
        verify(productRepository, times(1)).findByIdIn(orderProductIds);
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaymentOrderWithInvalidQuantities() {
        var orderLineRequests = singletonList(
                OrderLineRequest.builder()
                        .productId(5L)
                        .quantity(5)
                        .build());

        var paymentOrderRequest = PaymentOrderRequest.builder()
                .orderLines(orderLineRequests)
                .build();

        var orderProductIds = singletonList(5L);

        var cupsProductType = ProductType.builder()
                .id(1L)
                .name("Cups")
                .build();

        var existingProducts = singletonList(
                Product.builder()
                        .id(5L)
                        .name("Iron Man Cup")
                        .productType(cupsProductType)
                        .price(BigDecimal.valueOf(7.50))
                        .stock(2)
                        .build()
        );

        when(productRepository.findByIdIn(orderProductIds))
                .thenReturn(existingProducts);

        var exc = assertThrows(InvalidProductOrderException.class,
                () -> paymentOrderService.registerPaymentOrder(paymentOrderRequest));

        var expectedError = InvalidProductOrderError.builder()
                .id(5L)
                .name("Iron Man Cup")
                .requestedQuantity(5)
                .stock(2)
                .build();

        assertThat(expectedError, samePropertyValuesAs(exc.getInvalidProductOrderErrors().get(0)));
        verify(productRepository, times(1)).findByIdIn(orderProductIds);
    }

    private List<Product> mockExistingProducts() {
        var cups = ProductType.builder()
                .id(1L)
                .name("Cups")
                .build();

        var p1 = Product.builder()
                .id(1L)
                .name("Iron Man Cup")
                .productType(cups)
                .stock(20)
                .price(BigDecimal.valueOf(30.00))
                .build();

        var p2 = Product.builder()
                .id(2L)
                .name("Batman Cup")
                .productType(cups)
                .stock(20)
                .price(BigDecimal.valueOf(27.00))
                .build();

        var p3 = Product.builder()
                .id(3L)
                .name("Hulk Cup")
                .productType(cups)
                .stock(20)
                .price(BigDecimal.valueOf(25.00))
                .build();

        return asList(p1, p2, p3);
    }

    private ProductOrder mockCreatedProductOrder(Product product, Integer quantity) {
        var productDetail = ProductDetail.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .build();

        return ProductOrder.builder()
                .id(product.getId())
                .productDetail(productDetail)
                .quantity(quantity)
                .total(BigDecimal.valueOf(quantity).multiply(product.getPrice()))
                .build();
    }

    private List<ProductOrder> mockCreatedOrderLines(
            List<Product> existingProducts, List<OrderLineRequest> orderLineRequests) {
        var createdProductOrders = new ArrayList<ProductOrder>();

        for (var i = 0; i < existingProducts.size(); i++) {
            var product = existingProducts.get(i);
            var quantity = orderLineRequests.get(i).getQuantity();
            createdProductOrders.add(mockCreatedProductOrder(product, quantity));
        }

        return createdProductOrders;
    }

    private PaymentOrder mockCreatedPaymentOrder(List<ProductOrder> productOrders, LocalDateTime createdAt) {
        return PaymentOrder.builder()
                .id(1L)
                .productOrders(productOrders)
                .createdAt(createdAt)
                .total(BigDecimal.valueOf(545.00))
                .build();
    }

    private List<PaymentOrder> mockExistingPaymentOrders() {
        var ironManCup = ProductDetail.builder()
                .id(1L)
                .name("Iron Man Cup")
                .price(BigDecimal.valueOf(50))
                .build();

        var ironManCupOrderLine = ProductOrder.builder()
                .id(1L)
                .productDetail(ironManCup)
                .quantity(10)
                .total(BigDecimal.valueOf(500))
                .build();

        var firstPaymentOrder = PaymentOrder.builder()
                .id(1L)
                .productOrders(singletonList(ironManCupOrderLine))
                .total(BigDecimal.valueOf(500))
                .createdAt(LocalDateTime.now())
                .build();

        var batmanCup = ProductDetail.builder()
                .id(2L)
                .name("Batman Cup")
                .price(BigDecimal.valueOf(125))
                .build();

        var batmanCupOrderLine = ProductOrder.builder()
                .id(2L)
                .productDetail(batmanCup)
                .quantity(2)
                .total(BigDecimal.valueOf(250))
                .build();

        var secondPaymentOrder = PaymentOrder.builder()
                .id(2L)
                .productOrders(singletonList(batmanCupOrderLine))
                .total(BigDecimal.valueOf(250))
                .createdAt(LocalDateTime.now())
                .build();

        return asList(firstPaymentOrder, secondPaymentOrder);
    }

    private PaymentOrderResponse mockPaymentOrderResponse(PaymentOrder paymentOrder) {
        var orderLineDetails = paymentOrder
                .getProductOrders()
                .stream()
                .map(this::mockOrderLineDTO)
                .collect(toList());

        return PaymentOrderResponse.builder()
                .id(paymentOrder.getId())
                .productOrders(orderLineDetails)
                .createdAt(paymentOrder.getCreatedAt())
                .total(paymentOrder.getTotal())
                .build();
    }

    private ProductOrderDTO mockOrderLineDTO(ProductOrder productOrder) {
        return ProductOrderDTO.builder()
                .id(productOrder.getId())
                .productName(productOrder.getProductDetail().getName())
                .productPrice(productOrder.getProductDetail().getPrice())
                .quantity(productOrder.getQuantity())
                .total(productOrder.getTotal())
                .build();
    }

    private List<PaymentOrderResponse> mockExistingPaymentOrdersResponse(List<PaymentOrder> paymentOrderDetails) {
        return paymentOrderDetails.stream()
                .map(this::mockPaymentOrderResponse)
                .collect(toList());
    }
}
