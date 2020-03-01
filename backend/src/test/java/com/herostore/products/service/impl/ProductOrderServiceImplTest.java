package com.herostore.products.service.impl;

import com.herostore.products.constants.FileType;
import com.herostore.products.domain.Product;
import com.herostore.products.domain.ProductOrder;
import com.herostore.products.domain.ProductOrderLine;
import com.herostore.products.domain.ProductType;
import com.herostore.products.domain.embedded.ProductDetail;
import com.herostore.products.dto.ProductOrderLineDTO;
import com.herostore.products.dto.request.ProductOrderLineRequest;
import com.herostore.products.dto.request.ProductOrderRequest;
import com.herostore.products.dto.response.ProductOrderResponse;
import com.herostore.products.exception.InvalidProductOrderLineException;
import com.herostore.products.exception.ResourceNotFoundException;
import com.herostore.products.exception.error.InvalidProductOrderLineError;
import com.herostore.products.handler.ProductOrdersPDFWriter;
import com.herostore.products.mapper.ProductOrderMapper;
import com.herostore.products.repository.ProductOrderRepository;
import com.herostore.products.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
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
public class ProductOrderServiceImplTest {

    @Mock
    ProductOrderRepository productOrderRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductOrderMapper productOrderMapper;

    @Mock
    ProductOrdersPDFWriter productOrdersPDFWriter;

    @InjectMocks
    ProductOrderServiceImpl productOrderService;

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(productOrderRepository, productRepository, productOrderMapper, productOrdersPDFWriter);
    }

    @Test
    void shouldGetAllProductOrdersSuccessfully() {
        var existingProductOrders = mockExistingProductOrders();
        var expectedProductOrders = mockExistingProductOrdersResponse(existingProductOrders);

        when(productOrderRepository.findAll())
                .thenReturn(existingProductOrders);

        when(productOrderMapper.toProductOrderResponseList(existingProductOrders))
                .thenReturn(expectedProductOrders);

        var actualProductOrder = productOrderService.getAllProductOrders();

        assertThat(actualProductOrder, samePropertyValuesAs(expectedProductOrders));

        verify(productOrderRepository, times(1)).findAll();
        verify(productOrderMapper, times(1)).toProductOrderResponseList(existingProductOrders);
    }

    @Test
    void shouldGetEmptyListWhenRetrievingNonExistingProductOrders() {
        List<ProductOrder> existingProductOrders = emptyList();
        List<ProductOrderResponse> expectedProductOrders = emptyList();

        when(productOrderRepository.findAll())
                .thenReturn(existingProductOrders);

        when(productOrderMapper.toProductOrderResponseList(existingProductOrders))
                .thenReturn(expectedProductOrders);

        var actualProductOrders = productOrderService.getAllProductOrders();

        assertTrue(actualProductOrders.isEmpty());

        verify(productOrderRepository, times(1)).findAll();
        verify(productOrderMapper, times(1)).toProductOrderResponseList(existingProductOrders);
    }

    @Test
    void shouldWriteProductOrdersToPDFSuccessfully() {
        var existingProductOrders = mockExistingProductOrders();
        var expectedProductOrders = mockExistingProductOrdersResponse(existingProductOrders);

        when(productOrderRepository.findAll())
                .thenReturn(existingProductOrders);

        when(productOrderMapper.toProductOrderResponseList(existingProductOrders))
                .thenReturn(expectedProductOrders);

        var outputStream = new ByteArrayOutputStream();

        productOrderService.exportProductOrders(outputStream, FileType.PDF);

        verify(productOrderRepository, times(1)).findAll();
        verify(productOrderMapper, times(1)).toProductOrderResponseList(existingProductOrders);
        verify(productOrdersPDFWriter, times(1)).setProductOrders(expectedProductOrders);
        verify(productOrdersPDFWriter, times(1)).createDocument(outputStream);
        verify(productOrdersPDFWriter, times(1)).openDocument();
        verify(productOrdersPDFWriter, times(1)).writeDocument();
        verify(productOrdersPDFWriter, times(1)).closeDocument();
    }

    @Test
    void shouldThrowExceptionWhenExportingToInvalidFileFormat() {
        var existingProductOrders = mockExistingProductOrders();
        var expectedProductOrders = mockExistingProductOrdersResponse(existingProductOrders);
        var expectedError = "File format csv not valid.";

        when(productOrderRepository.findAll())
                .thenReturn(existingProductOrders);

        when(productOrderMapper.toProductOrderResponseList(existingProductOrders))
                .thenReturn(expectedProductOrders);

        var outputStream = new ByteArrayOutputStream();

        var exc = assertThrows(IllegalArgumentException.class,
                () -> productOrderService.exportProductOrders(outputStream, FileType.CSV));

        assertEquals(expectedError, exc.getMessage());

        expectedError = "File format xlsx not valid.";

        exc = assertThrows(IllegalArgumentException.class,
                () -> productOrderService.exportProductOrders(outputStream, FileType.EXCEL));

        assertEquals(expectedError, exc.getMessage());
    }

    @Test
    void shouldCreateProductOrderSuccessfully() {
        var orderLinesRequests = new ArrayList<>(asList(
                ProductOrderLineRequest.builder().productId(1L).quantity(5).build(),
                ProductOrderLineRequest.builder().productId(2L).quantity(10).build(),
                ProductOrderLineRequest.builder().productId(3L).quantity(5).build()
        ));

        var productOrderRequest = ProductOrderRequest.builder()
                .orderLines(orderLinesRequests)
                .build();

        var orderProductIds = asList(1L, 2L, 3L);
        var existingProducts = mockExistingProducts();

        var createdAt = LocalDateTime.now();
        var createdProductOrderLines = mockCreatedOrderLines(existingProducts, orderLinesRequests);
        var createdProductOrder = mockCreatedProductOrder(createdProductOrderLines, createdAt);

        var expectedProductOrderResponse = mockProductOrderResponse(createdProductOrder);

        when(productRepository.findByIdIn(orderProductIds))
                .thenReturn(existingProducts);

        when(productOrderRepository.save(any(ProductOrder.class)))
                .thenReturn(createdProductOrder);

        when(productOrderMapper.toProductOrderResponse(createdProductOrder))
                .thenReturn(expectedProductOrderResponse);

        var actualResponse = productOrderService.registerProductOrder(productOrderRequest);

        assertThat(actualResponse, samePropertyValuesAs(expectedProductOrderResponse));

        verify(productRepository, times(1)).findByIdIn(orderProductIds);
        verify(productRepository, times(1)).saveAll(existingProducts);
        verify(productOrderRepository, times(1)).save(any(ProductOrder.class));
        verify(productOrderMapper, times(1)).toProductOrderResponse(createdProductOrder);
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductOrderForNonExistingProduct() {
        var productOrderRequest = ProductOrderRequest.builder()
                .orderLines(singletonList(ProductOrderLineRequest.builder().productId(5L).build()))
                .build();

        var orderProductIds = singletonList(5L);
        var expectedError = "Couldn't register product order: product not found for id 5";

        when(productRepository.findByIdIn(orderProductIds))
                .thenReturn(emptyList());

        var exc = assertThrows(ResourceNotFoundException.class,
                () -> productOrderService.registerProductOrder(productOrderRequest));

        assertEquals(expectedError, exc.getMessage());
        verify(productRepository, times(1)).findByIdIn(orderProductIds);
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductOrderWithInvalidQuantities() {
        var productOrderLineRequests = singletonList(
                ProductOrderLineRequest.builder()
                        .productId(5L)
                        .quantity(5)
                        .build());

        var productOrderRequest = ProductOrderRequest.builder()
                .orderLines(productOrderLineRequests)
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

        var exc = assertThrows(InvalidProductOrderLineException.class,
                () -> productOrderService.registerProductOrder(productOrderRequest));

        var expectedError = InvalidProductOrderLineError.builder()
                .id(5L)
                .name("Iron Man Cup")
                .requestedQuantity(5)
                .stock(2)
                .build();

        assertThat(expectedError, samePropertyValuesAs(exc.getInvalidProductOrderLineErrors().get(0)));
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

    private ProductOrderLine mockCreatedProductOrder(Product product, Integer quantity) {
        var productDetail = ProductDetail.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .build();

        return ProductOrderLine.builder()
                .id(product.getId())
                .productDetail(productDetail)
                .quantity(quantity)
                .total(BigDecimal.valueOf(quantity).multiply(product.getPrice()))
                .build();
    }

    private List<ProductOrderLine> mockCreatedOrderLines(
            List<Product> existingProducts, List<ProductOrderLineRequest> productOrderLineRequests) {
        var createdProductOrders = new ArrayList<ProductOrderLine>();

        for (var i = 0; i < existingProducts.size(); i++) {
            var product = existingProducts.get(i);
            var quantity = productOrderLineRequests.get(i).getQuantity();
            createdProductOrders.add(mockCreatedProductOrder(product, quantity));
        }

        return createdProductOrders;
    }

    private ProductOrder mockCreatedProductOrder(List<ProductOrderLine> productOrderLines, LocalDateTime createdAt) {
        return ProductOrder.builder()
                .id(1L)
                .productOrderLines(productOrderLines)
                .createdAt(createdAt)
                .total(BigDecimal.valueOf(545.00))
                .build();
    }

    private List<ProductOrder> mockExistingProductOrders() {
        var ironManCup = ProductDetail.builder()
                .id(1L)
                .name("Iron Man Cup")
                .price(BigDecimal.valueOf(50))
                .build();

        var ironManCupOrderLine = ProductOrderLine.builder()
                .id(1L)
                .productDetail(ironManCup)
                .quantity(10)
                .total(BigDecimal.valueOf(500))
                .build();

        var firstPaymentOrder = ProductOrder.builder()
                .id(1L)
                .productOrderLines(singletonList(ironManCupOrderLine))
                .total(BigDecimal.valueOf(500))
                .createdAt(LocalDateTime.now())
                .build();

        var batmanCup = ProductDetail.builder()
                .id(2L)
                .name("Batman Cup")
                .price(BigDecimal.valueOf(125))
                .build();

        var batmanCupOrderLine = ProductOrderLine.builder()
                .id(2L)
                .productDetail(batmanCup)
                .quantity(2)
                .total(BigDecimal.valueOf(250))
                .build();

        var secondPaymentOrder = ProductOrder.builder()
                .id(2L)
                .productOrderLines(singletonList(batmanCupOrderLine))
                .total(BigDecimal.valueOf(250))
                .createdAt(LocalDateTime.now())
                .build();

        return asList(firstPaymentOrder, secondPaymentOrder);
    }

    private ProductOrderResponse mockProductOrderResponse(ProductOrder productOrder) {
        var orderLineDetails = productOrder
                .getProductOrderLines()
                .stream()
                .map(this::mockOrderLineDTO)
                .collect(toList());

        return ProductOrderResponse.builder()
                .id(productOrder.getId())
                .productOrderLines(orderLineDetails)
                .createdAt(productOrder.getCreatedAt())
                .total(productOrder.getTotal())
                .build();
    }

    private ProductOrderLineDTO mockOrderLineDTO(ProductOrderLine productOrderLine) {
        return ProductOrderLineDTO.builder()
                .id(productOrderLine.getId())
                .productName(productOrderLine.getProductDetail().getName())
                .productPrice(productOrderLine.getProductDetail().getPrice())
                .quantity(productOrderLine.getQuantity())
                .total(productOrderLine.getTotal())
                .build();
    }

    private List<ProductOrderResponse> mockExistingProductOrdersResponse(List<ProductOrder> productOrderDetails) {
        return productOrderDetails.stream()
                .map(this::mockProductOrderResponse)
                .collect(toList());
    }
}
