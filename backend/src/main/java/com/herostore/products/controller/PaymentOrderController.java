package com.herostore.products.controller;

import com.herostore.products.constants.FileType;
import com.herostore.products.dto.request.PaymentOrderRequest;
import com.herostore.products.dto.response.PaymentOrderResponse;
import com.herostore.products.exception.ServiceException;
import com.herostore.products.service.PaymentOrderService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.herostore.products.utils.FileUtils.buildFileName;
import static com.herostore.products.utils.HttpUtils.adaptHttpResponseForFileDownload;

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

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    static String FILE_NAME = "payment_orders";

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<PaymentOrderResponse> getAllOrders() {
        return paymentOrderService.getAllPaymentOrders();
    }

    @GetMapping(value = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void exportPaymentOrders(@RequestParam(name = "format") FileType fileType,
                                    HttpServletResponse response) {
        var fileName = buildFileName(FILE_NAME, fileType);
        adaptHttpResponseForFileDownload(response, fileName);

        try {
            paymentOrderService.exportPaymentOrders(response.getOutputStream(), fileType);
        } catch (IOException e) {
            logger.error("Couldn't extract output stream from response", e);
            throw new ServiceException("Couldn't extract output stream from response", e);
        }
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentOrderResponse registerPaymentOrder(@RequestBody @Valid PaymentOrderRequest paymentOrderRequest) {
        return paymentOrderService.registerPaymentOrder(paymentOrderRequest);
    }
}
