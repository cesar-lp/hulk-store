package com.todo.hulkstore.controller;

import com.todo.hulkstore.dto.ProductOrderDTO;
import com.todo.hulkstore.dto.request.OrderLineRequest;
import com.todo.hulkstore.dto.request.PaymentOrderRequest;
import com.todo.hulkstore.dto.response.PaymentOrderResponse;
import com.todo.hulkstore.exception.error.FieldValidationError;
import com.todo.hulkstore.service.PaymentOrderService;
import com.todo.hulkstore.utils.SerializationUtils;
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

import static com.todo.hulkstore.utils.ResponseBodyMatchers.response;
import static com.todo.hulkstore.utils.ResponseBodyMatchers.responseContainsJsonCollection;
import static com.todo.hulkstore.utils.ResponseBodyMatchers.responseContainsJsonObject;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentOrderController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentOrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PaymentOrderService paymentOrderService;

    static final String BASE_URI = "/api/orders";

    @Test
    void getAllOrders() throws Exception {
        var expectedPaymentOrders = singletonList(PaymentOrderResponse.builder()
                .id(1L)
                .productOrders(asList(mockIronManProductOrder(), mockBatmanProductOrder()))
                .createdAt(LocalDateTime.now())
                .total(BigDecimal.valueOf(175.00))
                .build());

        when(paymentOrderService.getAllPaymentOrders())
                .thenReturn(expectedPaymentOrders);

        mockMvc.perform(
                get(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(responseContainsJsonCollection(expectedPaymentOrders, PaymentOrderResponse.class));
    }

    @Test
    void registerPaymentOrder() throws Exception {
        var orderLines = singletonList(OrderLineRequest.builder()
                .productId(1L)
                .quantity(2)
                .build());

        var paymentOrder = PaymentOrderRequest.builder()
                .orderLines(orderLines)
                .build();

        var expectedPaymentOrder = PaymentOrderResponse.builder()
                .id(1L)
                .productOrders(singletonList(mockIronManProductOrder()))
                .createdAt(LocalDateTime.now())
                .total(BigDecimal.valueOf(100.00))
                .build();

        when(paymentOrderService.registerPaymentOrder(paymentOrder))
                .thenReturn(expectedPaymentOrder);

        mockMvc.perform(
                post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SerializationUtils.objectMapper.writeValueAsBytes(paymentOrder)))
                .andExpect(status().isCreated())
                .andExpect(responseContainsJsonObject(expectedPaymentOrder, PaymentOrderResponse.class));
    }

    @Test
    void registerInvalidPaymentOrderThrowsValidationError() throws Exception {
        var paymentOrder = PaymentOrderRequest.builder()
                .build();

        var expectedErrors = new FieldValidationError[]{
                new FieldValidationError("orderLines", "Order lines cannot be empty", null)
        };

        mockMvc.perform(
                post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SerializationUtils.objectMapper.writeValueAsBytes(paymentOrder)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(response().containsValidationErrors(expectedErrors));
    }

    private ProductOrderDTO mockIronManProductOrder() {
        return ProductOrderDTO.builder()
                .id(1L)
                .productId(1L)
                .productName("Iron Man Cup")
                .productPrice(BigDecimal.valueOf(25.00))
                .quantity(2)
                .total(BigDecimal.valueOf(50.00))
                .build();
    }

    private ProductOrderDTO mockBatmanProductOrder() {
        return ProductOrderDTO.builder()
                .id(2L)
                .productId(2L)
                .productName("Batman Cup")
                .productPrice(BigDecimal.valueOf(25.00))
                .quantity(5)
                .total(BigDecimal.valueOf(125.00))
                .build();
    }
}
