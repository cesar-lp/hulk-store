package com.todo.hulkstore.controller;

import com.todo.hulkstore.dto.request.PaymentOrderRequest;
import com.todo.hulkstore.dto.response.PaymentOrderResponse;
import com.todo.hulkstore.service.PaymentOrderService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(
        value = "/api/orders",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class PaymentOrderController {

    PaymentOrderService paymentOrderService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<PaymentOrderResponse> getAllOrders() {
        return paymentOrderService.getAllPaymentOrders();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentOrderResponse registerPaymentOrder(@RequestBody @Valid PaymentOrderRequest paymentOrderRequest) {
        return paymentOrderService.registerPaymentOrder(paymentOrderRequest);
    }
}
