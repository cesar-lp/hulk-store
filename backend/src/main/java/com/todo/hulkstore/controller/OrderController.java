package com.todo.hulkstore.controller;

import com.todo.hulkstore.dto.PaymentOrderDTO;
import com.todo.hulkstore.dto.request.PaymentOrderRequestDTO;
import com.todo.hulkstore.service.PaymentOrderService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(
        value = "/api/orders",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class OrderController {

    PaymentOrderService paymentOrderService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public List<PaymentOrderDTO> registerPayment(@RequestBody PaymentOrderRequestDTO paymentOrderRequestDTO) {
        return paymentOrderService.registerOrder(paymentOrderRequestDTO);
    }
}
