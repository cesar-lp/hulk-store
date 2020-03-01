package com.herostore.products.controller;

import com.herostore.products.constants.FileType;
import com.herostore.products.dto.ProductOrderLineDTO;
import com.herostore.products.dto.request.ProductOrderLineRequest;
import com.herostore.products.dto.request.ProductOrderRequest;
import com.herostore.products.dto.response.ProductOrderResponse;
import com.herostore.products.service.ProductOrderService;
import com.herostore.products.utils.SerializationUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.herostore.products.utils.ResponseBodyMatchers.responseContainsJsonCollection;
import static com.herostore.products.utils.ResponseBodyMatchers.responseContainsJsonObject;
import static com.herostore.products.utils.ResponseBodyMatchers.responseContainsValidationErrors;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentOrderController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductOrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductOrderService productOrderService;

    static final String BASE_URI = "/api/orders";

    @Test
    void getAllOrders() throws Exception {
        var expectedPaymentOrders = singletonList(ProductOrderResponse.builder()
                .id(1L)
                .productOrderLines(asList(mockIronManProductOrder(), mockBatmanProductOrder()))
                .createdAt(LocalDateTime.now())
                .total(BigDecimal.valueOf(175.00))
                .build());

        when(productOrderService.getAllProductOrders())
                .thenReturn(expectedPaymentOrders);

        mockMvc.perform(
                get(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(responseContainsJsonCollection(expectedPaymentOrders, ProductOrderResponse.class));
    }

    @Test
    void exportProductOrders() throws Exception {
        var response = mockMvc.perform(
                get(BASE_URI + "/export")
                        .param("format", FileType.PDF.getDesc())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, response.getContentType());
        assertEquals("attachment; filename=payment_orders.pdf",
                response.getHeaderValue("content-disposition"));
    }

    @Test
    void registerProductOrder() throws Exception {
        var orderLines = singletonList(ProductOrderLineRequest.builder()
                .productId(1L)
                .quantity(2)
                .build());

        var productOrder = ProductOrderRequest.builder()
                .orderLines(orderLines)
                .build();

        var expectedProductOrder = ProductOrderResponse.builder()
                .id(1L)
                .productOrderLines(singletonList(mockIronManProductOrder()))
                .createdAt(LocalDateTime.now())
                .total(BigDecimal.valueOf(100.00))
                .build();

        when(productOrderService.registerProductOrder(productOrder))
                .thenReturn(expectedProductOrder);

        mockMvc.perform(
                post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SerializationUtils.objectMapper.writeValueAsBytes(productOrder)))
                .andExpect(status().isCreated())
                .andExpect(responseContainsJsonObject(expectedProductOrder, ProductOrderResponse.class));
    }

    @Test
    void registerInvalidProductOrderThrowsValidationError() throws Exception {
        var productOrder = ProductOrderRequest.builder()
                .build();

        mockMvc.perform(
                post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SerializationUtils.objectMapper.writeValueAsBytes(productOrder)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(responseContainsValidationErrors(1));
    }

    private ProductOrderLineDTO mockIronManProductOrder() {
        return ProductOrderLineDTO.builder()
                .id(1L)
                .productId(1L)
                .productName("Iron Man Cup")
                .productPrice(BigDecimal.valueOf(25.00))
                .quantity(2)
                .total(BigDecimal.valueOf(50.00))
                .build();
    }

    private ProductOrderLineDTO mockBatmanProductOrder() {
        return ProductOrderLineDTO.builder()
                .id(2L)
                .productId(2L)
                .productName("Batman Cup")
                .productPrice(BigDecimal.valueOf(25.00))
                .quantity(5)
                .total(BigDecimal.valueOf(125.00))
                .build();
    }
}
